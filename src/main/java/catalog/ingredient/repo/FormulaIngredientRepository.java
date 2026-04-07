package catalog.ingredient.repo;

import catalog.ingredient.domain.FormulaIngredient;
import catalog.ingredient.domain.FormulaIngredientId;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FormulaIngredientRepository extends JpaRepository<FormulaIngredient, FormulaIngredientId> {

    @EntityGraph(attributePaths = {"ingredient"})
    @Query("""
        select fi from FormulaIngredient fi
        where fi.formula.formulaId = :formulaId
        order by fi.ingredient.primaryName asc
        """)
    List<FormulaIngredient> findByFormulaId(@Param("formulaId") Long formulaId);

    @EntityGraph(attributePaths = {"formula", "formula.product", "ingredient"})
    @Query("""
        select fi from FormulaIngredient fi
        where fi.ingredient.ingredientId = :ingredientId
        order by fi.formula.product.name asc, fi.formula.versionNo desc
        """)
    List<FormulaIngredient> findUsageByIngredientId(@Param("ingredientId") Long ingredientId);
}

