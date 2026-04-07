package catalog.ingredient.repo;

import catalog.ingredient.domain.IngredientComponent;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import catalog.ingredient.domain.IngredientKind;
import org.springframework.data.repository.query.Param;

public interface IngredientComponentRepository extends JpaRepository<IngredientComponent, Long> {

    @Query("""
        select c
        from IngredientComponent c
        left join fetch c.parentIngredient
        left join fetch c.componentIngredient
        where c.parentIngredient.ingredientId = :ingredientId
        order by c.ingredientComponentId
        """)
    List<IngredientComponent> findByParentIngredient_IngredientIdOrderByIngredientComponentId(
            @Param("ingredientId") Long ingredientId
    );

    @EntityGraph(attributePaths = {"parentIngredient", "componentIngredient"})
    @Query("""
        select c
        from IngredientComponent c
        order by c.parentIngredient.ingredientId asc, c.ingredientComponentId asc
        """)
    List<IngredientComponent> findAllWithLinks();

    @Query("""
        select distinct c.functionRaw
        from IngredientComponent c
        where c.functionRaw is not null
          and trim(c.functionRaw) <> ''
        order by c.functionRaw
        """)
    List<String> findDistinctFunctions(Pageable pageable);

    @Query("""
        select count(distinct c.functionRaw)
        from IngredientComponent c
        where c.functionRaw is not null
          and trim(c.functionRaw) <> ''
        """)
    long countDistinctFunctions();

    @Query("""
        select distinct c.functionRaw
        from IngredientComponent c
        where c.functionRaw is not null
          and trim(c.functionRaw) <> ''
          and lower(c.functionRaw) like lower(concat(:prefix, '%'))
        order by c.functionRaw
        """)
    List<String> findDistinctFunctionsByPrefix(@Param("prefix") String prefix, Pageable pageable);

    @Query("""
        select count(distinct c.functionRaw)
        from IngredientComponent c
        where c.functionRaw is not null
          and trim(c.functionRaw) <> ''
          and lower(c.functionRaw) like lower(concat(:prefix, '%'))
        """)
    long countDistinctFunctionsByPrefix(@Param("prefix") String prefix);

    @Query("""
        select c.componentIngredient.ingredientId as ingredientId,
               c.functionRaw as functionText
        from IngredientComponent c
        where c.componentIngredient.ingredientId in :ingredientIds
          and c.functionRaw is not null
          and trim(c.functionRaw) <> ''
        order by c.componentIngredient.ingredientId, c.functionRaw
        """)
    List<IngredientFunctionProjection> findFunctionRowsByIngredientIds(@Param("ingredientIds") Collection<Long> ingredientIds);

    @EntityGraph(attributePaths = {"parentIngredient", "componentIngredient"})
    @Query("""
    select c
    from IngredientComponent c
    where c.parentIngredient.kind = :kind
    order by c.parentIngredient.ingredientId asc, c.ingredientComponentId asc
    """)
    List<IngredientComponent> findAllWithLinksByParentKind(@Param("kind") IngredientKind kind);
    interface IngredientFunctionProjection {
        Long getIngredientId();
        String getFunctionText();
    }
}