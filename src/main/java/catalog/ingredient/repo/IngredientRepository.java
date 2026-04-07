package catalog.ingredient.repo;

import catalog.ingredient.domain.Ingredient;
import catalog.ingredient.domain.IngredientKind;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    @EntityGraph(attributePaths = {"names", "identifiers"})
    Optional<Ingredient> findWithNamesAndIdentifiersByIngredientId(Long ingredientId);

    @Query(value = """
        select distinct i from Ingredient i
        left join i.names n
        left join i.identifiers ident
        where (:query is null or :query = ''
           or lower(i.primaryName) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.inciName, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.casNo, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.ecNo, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.ciNo, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(n.id.name, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(ident.idValue, '')) like lower(concat('%', :query, '%')))
        order by i.primaryName asc
        """,
            countQuery = """
        select count(distinct i.ingredientId) from Ingredient i
        left join i.names n
        left join i.identifiers ident
        where (:query is null or :query = ''
           or lower(i.primaryName) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.inciName, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.casNo, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.ecNo, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.ciNo, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(n.id.name, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(ident.idValue, '')) like lower(concat('%', :query, '%')))
        """)
    Page<Ingredient> searchPage(@Param("query") String query, Pageable pageable);

    @Query(value = """
        select distinct i from Ingredient i
        left join i.names n
        left join i.identifiers ident
        where (:query is null or :query = ''
           or lower(i.primaryName) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.inciName, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.casNo, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.ecNo, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.ciNo, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(n.id.name, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(ident.idValue, '')) like lower(concat('%', :query, '%')))
          and i.kind = :kind
        order by i.primaryName asc
        """,
            countQuery = """
        select count(distinct i.ingredientId) from Ingredient i
        left join i.names n
        left join i.identifiers ident
        where (:query is null or :query = ''
           or lower(i.primaryName) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.inciName, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.casNo, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.ecNo, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.ciNo, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(n.id.name, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(ident.idValue, '')) like lower(concat('%', :query, '%')))
          and i.kind = :kind
        """)
    Page<Ingredient> searchPageByKind(@Param("query") String query,
                                      @Param("kind") IngredientKind kind,
                                      Pageable pageable);

    @Query("""
        select count(distinct i.ingredientId) from Ingredient i
        left join i.names n
        left join i.identifiers ident
        where (:query is null or :query = ''
           or lower(i.primaryName) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.inciName, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.casNo, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.ecNo, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.ciNo, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(n.id.name, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(ident.idValue, '')) like lower(concat('%', :query, '%')))
        """)
    long countSearch(@Param("query") String query);

    @Query("""
        select count(distinct i.ingredientId) from Ingredient i
        left join i.names n
        left join i.identifiers ident
        where (:query is null or :query = ''
           or lower(i.primaryName) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.inciName, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.casNo, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.ecNo, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(i.ciNo, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(n.id.name, '')) like lower(concat('%', :query, '%'))
           or lower(coalesce(ident.idValue, '')) like lower(concat('%', :query, '%')))
          and i.kind = :kind
        """)
    long countSearchByKind(@Param("query") String query, @Param("kind") IngredientKind kind);

    default List<Ingredient> search(String query, IngredientKind kind, Pageable pageable) {
        return (kind == null
                ? searchPage(query, pageable)
                : searchPageByKind(query, kind, pageable))
                .getContent();
    }

    long countByKind(IngredientKind kind);
}