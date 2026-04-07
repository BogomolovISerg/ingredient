package catalog.ingredient.service.dto;

import catalog.ingredient.domain.FormulaIngredient;
import catalog.ingredient.domain.Ingredient;
import catalog.ingredient.domain.IngredientComponent;
import catalog.ingredient.domain.IngredientEntryLink;
import catalog.ingredient.domain.IngredientIdentifier;
import catalog.ingredient.domain.IngredientName;
import catalog.ingredient.domain.IngredientRequirement;
import catalog.ingredient.domain.IngredientTestLog;
import java.util.List;

public record IngredientDetailDto(
        Ingredient ingredient,
        List<IngredientName> names,
        List<IngredientIdentifier> identifiers,
        List<IngredientRequirement> requirements,
        List<IngredientTestLog> testLogs,
        List<IngredientComponent> components,
        List<IngredientEntryLink> regulatoryLinks,
        List<FormulaIngredient> formulaUsages,
        List<SpecialchemKeyValueRow> technicalProfile,
        List<SpecialchemValueRow> products,
        List<SpecialchemValueRow> formulations,
        List<SpecialchemValueRow> alternatives,
        List<SpecialchemValueRow> potentialUse
) {
}
