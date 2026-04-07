package catalog.ingredient.service.dto;

import catalog.ingredient.domain.Formula;
import catalog.ingredient.domain.FormulaIngredient;
import java.util.List;

public record FormulaDetailDto(
        Formula formula,
        List<FormulaIngredient> ingredients
) {
}

