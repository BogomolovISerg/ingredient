package catalog.ingredient.service;

import catalog.ingredient.service.dto.DuplicateCandidateRow;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DataQualityService {

    private final EntityManager entityManager;

    public DataQualityService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<DuplicateCandidateRow> loadSafeDuplicateCandidates(int limit) {
        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager.createNativeQuery("""
                with normalized as (
                    select ingredient_id,
                           kind,
                           lower(regexp_replace(trim(primary_name), '\\s+', ' ', 'g')) as norm_primary_name,
                           lower(regexp_replace(trim(coalesce(inci_name,'')), '\\s+', ' ', 'g')) as norm_inci_name,
                           nullif(lower(regexp_replace(trim(coalesce(cas_no,'')), '\\s+', '', 'g')), '') as norm_cas_no,
                           nullif(lower(regexp_replace(trim(coalesce(ec_no,'')), '\\s+', '', 'g')), '') as norm_ec_no,
                           nullif(lower(regexp_replace(trim(coalesce(ci_no,'')), '\\s+', '', 'g')), '') as norm_ci_no,
                           nullif(lower(regexp_replace(trim(coalesce(supplier_code,'')), '\\s+', ' ', 'g')), '') as norm_supplier_code
                    from core.ingredient
                )
                select kind,
                       norm_primary_name,
                       norm_inci_name,
                       count(*) as cnt,
                       string_agg(ingredient_id::text, ', ' order by ingredient_id) as ids
                from normalized
                group by kind, norm_primary_name, norm_inci_name
                having count(*) > 1
                   and count(distinct norm_cas_no) filter (where norm_cas_no is not null) <= 1
                   and count(distinct norm_ec_no) filter (where norm_ec_no is not null) <= 1
                   and count(distinct norm_ci_no) filter (where norm_ci_no is not null) <= 1
                   and count(distinct norm_supplier_code) filter (where norm_supplier_code is not null) <= 1
                order by cnt desc, norm_primary_name
                limit :limit
                """)
                .setParameter("limit", limit)
                .getResultList();

        List<DuplicateCandidateRow> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(new DuplicateCandidateRow(
                    row[0] == null ? null : row[0].toString(),
                    row[1] == null ? null : row[1].toString(),
                    row[2] == null ? null : row[2].toString(),
                    row[3] == null ? 0L : ((Number) row[3]).longValue(),
                    row[4] == null ? "" : row[4].toString()
            ));
        }
        return result;
    }
}

