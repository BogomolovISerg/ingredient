package catalog.ingredient.repo;

import catalog.ingredient.domain.RegulatoryEntry;
import catalog.ingredient.domain.RegulatoryListType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RegulatoryEntryRepository extends JpaRepository<RegulatoryEntry, Long> {

    @Query("""
        select r from RegulatoryEntry r
        where (:query is null or :query = ''
           or lower(coalesce(r.ruName,'')) like lower(concat('%', :query, '%'))
           or lower(coalesce(r.inciInnName,'')) like lower(concat('%', :query, '%'))
           or lower(coalesce(r.chemicalName,'')) like lower(concat('%', :query, '%'))
           or lower(coalesce(r.glossaryName,'')) like lower(concat('%', :query, '%'))
           or lower(coalesce(r.casNo,'')) like lower(concat('%', :query, '%'))
           or lower(coalesce(r.ecNo,'')) like lower(concat('%', :query, '%'))
           or lower(coalesce(r.ciNo,'')) like lower(concat('%', :query, '%')))
          and (:type is null or r.listType = :type)
        order by r.entryId asc
        """)
    List<RegulatoryEntry> search(@Param("query") String query, @Param("type") RegulatoryListType type, Pageable pageable);

    Optional<RegulatoryEntry> findByEntryId(Long entryId);
}

