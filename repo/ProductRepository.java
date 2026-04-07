package catalog.ingredient.repo;

import catalog.ingredient.domain.Product;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
        select p from Product p
        where (:query is null or :query = ''
           or lower(p.name) like lower(concat('%', :query, '%'))
           or lower(coalesce(p.sku, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(p.category, '')) like lower(concat('%', :query, '%')))
        order by p.name asc
        """)
    List<Product> search(@Param("query") String query, Pageable pageable);
}

