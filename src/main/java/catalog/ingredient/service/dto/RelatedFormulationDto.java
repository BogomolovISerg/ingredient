package catalog.ingredient.service.dto;

import java.util.List;

public record RelatedFormulationDto(
        Long ingredientRelatedFormulationId,
        Long relatedFormulationId,
        String formulationName,
        String title,
        String formulationUrl,
        String supplierName,
        String relationType,
        String lastUpdatedRaw,
        String downloadFormulationUrl,
        String note,
        String description,
        String propertiesText,
        String procedureText,
        List<RelatedFormulationPropertyDto> properties,
        List<RelatedFormulationTagDto> tags,
        List<RelatedFormulationIngredientDto> ingredients
) {
}
