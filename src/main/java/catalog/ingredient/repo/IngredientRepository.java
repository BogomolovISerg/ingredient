package catalog.ingredient.repo;

import catalog.ingredient.domain.Ingredient;
import catalog.ingredient.domain.IngredientKind;
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
          and exists (
              select 1 from IngredientComponent c
              where c.componentIngredient = i
                and lower(c.functionRaw) = lower(:function)
          )
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
          and exists (
              select 1 from IngredientComponent c
              where c.componentIngredient = i
                and lower(c.functionRaw) = lower(:function)
          )
        """)
    Page<Ingredient> searchPageByFunction(@Param("query") String query,
                                          @Param("function") String function,
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
          and exists (
              select 1 from IngredientComponent c
              where c.componentIngredient = i
                and lower(c.functionRaw) = lower(:function)
          )
        """)
    long countSearchByFunction(@Param("query") String query, @Param("function") String function);

    @Query(value = """
    select distinct i from Ingredient i
    left join i.names n
    left join i.identifiers ident
    where i.kind = catalog.ingredient.domain.IngredientKind.SUBSTANCE
      and (:query is null or :query = ''
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
    where i.kind = catalog.ingredient.domain.IngredientKind.SUBSTANCE
      and (:query is null or :query = ''
       or lower(i.primaryName) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.inciName, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.casNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ecNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ciNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(n.id.name, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(ident.idValue, '')) like lower(concat('%', :query, '%')))
    """)
    Page<Ingredient> searchSubstancesPage(@Param("query") String query, Pageable pageable);

    @Query(value = """
    select distinct i from Ingredient i
    left join i.names n
    left join i.identifiers ident
    where i.kind = catalog.ingredient.domain.IngredientKind.SUBSTANCE
      and (:query is null or :query = ''
       or lower(i.primaryName) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.inciName, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.casNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ecNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ciNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(n.id.name, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(ident.idValue, '')) like lower(concat('%', :query, '%')))
      and exists (
          select 1 from IngredientComponent c
          where c.componentIngredient = i
            and lower(c.functionRaw) = lower(:function)
      )
    order by i.primaryName asc
    """,
            countQuery = """
    select count(distinct i.ingredientId) from Ingredient i
    left join i.names n
    left join i.identifiers ident
    where i.kind = catalog.ingredient.domain.IngredientKind.SUBSTANCE
      and (:query is null or :query = ''
       or lower(i.primaryName) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.inciName, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.casNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ecNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ciNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(n.id.name, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(ident.idValue, '')) like lower(concat('%', :query, '%')))
      and exists (
          select 1 from IngredientComponent c
          where c.componentIngredient = i
            and lower(c.functionRaw) = lower(:function)
      )
    """)
    Page<Ingredient> searchSubstancesPageByFunction(@Param("query") String query,
                                                    @Param("function") String function,
                                                    Pageable pageable);

    @Query("""
    select count(distinct i.ingredientId) from Ingredient i
    left join i.names n
    left join i.identifiers ident
    where i.kind = catalog.ingredient.domain.IngredientKind.SUBSTANCE
      and (:query is null or :query = ''
       or lower(i.primaryName) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.inciName, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.casNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ecNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ciNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(n.id.name, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(ident.idValue, '')) like lower(concat('%', :query, '%')))
    """)
    long countSubstances(@Param("query") String query);

    @Query("""
    select count(distinct i.ingredientId) from Ingredient i
    left join i.names n
    left join i.identifiers ident
    where i.kind = catalog.ingredient.domain.IngredientKind.SUBSTANCE
      and (:query is null or :query = ''
       or lower(i.primaryName) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.inciName, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.casNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ecNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ciNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(n.id.name, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(ident.idValue, '')) like lower(concat('%', :query, '%')))
      and exists (
          select 1 from IngredientComponent c
          where c.componentIngredient = i
            and lower(c.functionRaw) = lower(:function)
      )
    """)
    long countSubstancesByFunction(@Param("query") String query, @Param("function") String function);

    @Query(value = """
    select distinct i from Ingredient i
    left join i.names n
    left join i.identifiers ident
    where i.kind = :kind
      and (:query is null or :query = ''
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
    where i.kind = :kind
      and (:query is null or :query = ''
       or lower(i.primaryName) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.inciName, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.casNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ecNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ciNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(n.id.name, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(ident.idValue, '')) like lower(concat('%', :query, '%')))
    """)
    Page<Ingredient> searchPageByKindAndQuery(@Param("query") String query,
                                              @Param("kind") IngredientKind kind,
                                              Pageable pageable);

    @Query(value = """
    select distinct i from Ingredient i
    left join i.names n
    left join i.identifiers ident
    where i.kind = :kind
      and (:query is null or :query = ''
       or lower(i.primaryName) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.inciName, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.casNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ecNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ciNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(n.id.name, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(ident.idValue, '')) like lower(concat('%', :query, '%')))
      and exists (
          select 1 from IngredientComponent c
          where c.componentIngredient = i
            and lower(c.functionRaw) = lower(:function)
      )
    order by i.primaryName asc
    """,
            countQuery = """
    select count(distinct i.ingredientId) from Ingredient i
    left join i.names n
    left join i.identifiers ident
    where i.kind = :kind
      and (:query is null or :query = ''
       or lower(i.primaryName) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.inciName, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.casNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ecNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ciNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(n.id.name, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(ident.idValue, '')) like lower(concat('%', :query, '%')))
      and exists (
          select 1 from IngredientComponent c
          where c.componentIngredient = i
            and lower(c.functionRaw) = lower(:function)
      )
    """)
    Page<Ingredient> searchPageByKindAndFunction(@Param("query") String query,
                                                 @Param("kind") IngredientKind kind,
                                                 @Param("function") String function,
                                                 Pageable pageable);

    @Query("""
    select count(distinct i.ingredientId) from Ingredient i
    left join i.names n
    left join i.identifiers ident
    where i.kind = :kind
      and (:query is null or :query = ''
       or lower(i.primaryName) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.inciName, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.casNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ecNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ciNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(n.id.name, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(ident.idValue, '')) like lower(concat('%', :query, '%')))
    """)
    long countByKindAndQuery(@Param("query") String query,
                             @Param("kind") IngredientKind kind);

    @Query("""
    select count(distinct i.ingredientId) from Ingredient i
    left join i.names n
    left join i.identifiers ident
    where i.kind = :kind
      and (:query is null or :query = ''
       or lower(i.primaryName) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.inciName, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.casNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ecNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(i.ciNo, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(n.id.name, '')) like lower(concat('%', :query, '%'))
       or lower(coalesce(ident.idValue, '')) like lower(concat('%', :query, '%')))
      and exists (
          select 1 from IngredientComponent c
          where c.componentIngredient = i
            and lower(c.functionRaw) = lower(:function)
      )
    """)
    long countByKindAndFunction(@Param("query") String query,
                                @Param("kind") IngredientKind kind,
                                @Param("function") String function);
    Page<Ingredient> findByKindOrderByPrimaryNameAsc(IngredientKind kind, Pageable pageable);

    long countByKind(IngredientKind kind);
}