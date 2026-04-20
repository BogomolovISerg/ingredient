package catalog.ingredient.repo;

import catalog.ingredient.domain.IngredientFormulationFunction;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IngredientFormulationFunctionRepository extends JpaRepository<IngredientFormulationFunction, Long> {

    @Query("""
        select distinct f.functionName
        from IngredientFormulationFunction f
        where f.functionName is not null
          and trim(f.functionName) <> ''
        order by f.functionName
        """)
    List<String> findDistinctFunctions(Pageable pageable);

    @Query("""
        select count(distinct f.functionName)
        from IngredientFormulationFunction f
        where f.functionName is not null
          and trim(f.functionName) <> ''
        """)
    long countDistinctFunctions();

    @Query("""
        select distinct f.functionName
        from IngredientFormulationFunction f
        where f.functionName is not null
          and trim(f.functionName) <> ''
          and lower(f.functionName) like lower(concat(:prefix, '%'))
        order by f.functionName
        """)
    List<String> findDistinctFunctionsByPrefix(@Param("prefix") String prefix, Pageable pageable);

    @Query("""
        select count(distinct f.functionName)
        from IngredientFormulationFunction f
        where f.functionName is not null
          and trim(f.functionName) <> ''
          and lower(f.functionName) like lower(concat(:prefix, '%'))
        """)
    long countDistinctFunctionsByPrefix(@Param("prefix") String prefix);

    @Query("""
        select f.ingredient.ingredientId as ingredientId,
               f.functionName as functionText
        from IngredientFormulationFunction f
        where f.ingredient.ingredientId in :ingredientIds
          and f.functionName is not null
          and trim(f.functionName) <> ''
        order by f.ingredient.ingredientId, f.functionName
        """)
    List<IngredientFunctionProjection> findFunctionRowsByIngredientIds(@Param("ingredientIds") Collection<Long> ingredientIds);

    interface IngredientFunctionProjection {
        Long getIngredientId();
        String getFunctionText();
    }
}
