package catalog.ingredient.repo;

import catalog.ingredient.domain.Formula;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FormulaRepository extends JpaRepository<Formula, Long> {

    @EntityGraph(attributePaths = {"product"})
    Optional<Formula> findByFormulaId(Long formulaId);

    @EntityGraph(attributePaths = {"product"})
    @Query("""
        select f from Formula f
        join f.product p
        where (:query is null or :query = ''
           or lower(p.name) like lower(concat('%', :query, '%'))
           or lower(coalesce(p.sku, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(p.category, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(f.status, '')) like lower(concat('%', :query, '%')))
        order by p.name asc, f.versionNo desc
        """)
    List<Formula> search(@Param("query") String query, Pageable pageable);

    @EntityGraph(attributePaths = {"product"})
    List<Formula> findByProduct_ProductIdOrderByVersionNoDesc(Long productId);
}

