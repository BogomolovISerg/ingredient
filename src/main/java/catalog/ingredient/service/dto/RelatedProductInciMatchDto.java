package catalog.ingredient.service.dto;

public record RelatedProductInciMatchDto(
        Integer matchOrd,
        Long ingredientId,
        String ingredientPrimaryName,
        String matchedName,
        String sourceRef
) {
}
