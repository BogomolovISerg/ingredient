package catalog.ingredient.service.dto;

public record RelatedFormulationIngredientMatchDto(
        Integer matchOrd,
        Long ingredientId,
        String ingredientPrimaryName,
        String matchedName,
        String sourceRef
) {
}
