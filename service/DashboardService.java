package catalog.ingredient.service;

import catalog.ingredient.domain.IngredientKind;
import catalog.ingredient.repo.FormulaRepository;
import catalog.ingredient.repo.IngredientRepository;
import catalog.ingredient.repo.ProductRepository;
import catalog.ingredient.repo.RegulatoryEntryRepository;
import catalog.ingredient.service.dto.DashboardCounters;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final IngredientRepository ingredientRepository;
    private final RegulatoryEntryRepository regulatoryEntryRepository;
    private final ProductRepository productRepository;
    private final FormulaRepository formulaRepository;
    private final EntityManager entityManager;

    public DashboardService(IngredientRepository ingredientRepository,
                            RegulatoryEntryRepository regulatoryEntryRepository,
                            ProductRepository productRepository,
                            FormulaRepository formulaRepository,
                            EntityManager entityManager) {
        this.ingredientRepository = ingredientRepository;
        this.regulatoryEntryRepository = regulatoryEntryRepository;
        this.productRepository = productRepository;
        this.formulaRepository = formulaRepository;
        this.entityManager = entityManager;
    }

    public DashboardCounters loadCounters() {
        long requirementCount = queryForLong("select count(*) from core.ingredient_requirement");
        long testCount = queryForLong("select count(*) from core.ingredient_test_log");
        long duplicateGroups = queryForLong("""
                with normalized as (
                    select kind,
                           lower(regexp_replace(trim(primary_name), '\s+', ' ', 'g')) as norm_primary_name,
                           lower(regexp_replace(trim(coalesce(inci_name,'')), '\s+', ' ', 'g')) as norm_inci_name,
                           nullif(lower(regexp_replace(trim(coalesce(cas_no,'')), '\s+', '', 'g')), '') as norm_cas_no,
                           nullif(lower(regexp_replace(trim(coalesce(ec_no,'')), '\s+', '', 'g')), '') as norm_ec_no,
                           nullif(lower(regexp_replace(trim(coalesce(ci_no,'')), '\s+', '', 'g')), '') as norm_ci_no,
                           nullif(lower(regexp_replace(trim(coalesce(supplier_code,'')), '\s+', ' ', 'g')), '') as norm_supplier_code
                    from core.ingredient
                )
                select count(*)
                from (
                    select kind, norm_primary_name, norm_inci_name
                    from normalized
                    group by kind, norm_primary_name, norm_inci_name
                    having count(*) > 1
                       and count(distinct norm_cas_no) filter (where norm_cas_no is not null) <= 1
                       and count(distinct norm_ec_no) filter (where norm_ec_no is not null) <= 1
                       and count(distinct norm_ci_no) filter (where norm_ci_no is not null) <= 1
                       and count(distinct norm_supplier_code) filter (where norm_supplier_code is not null) <= 1
                ) q
                """);
        return new DashboardCounters(
                ingredientRepository.count(),
                ingredientRepository.countByKind(IngredientKind.MIXTURE),
                regulatoryEntryRepository.count(),
                productRepository.count(),
                formulaRepository.count(),
                requirementCount,
                testCount,
                duplicateGroups
        );
    }

    private long queryForLong(String sql) {
        Number number = (Number) entityManager.createNativeQuery(sql).getSingleResult();
        return number.longValue();
    }
}

