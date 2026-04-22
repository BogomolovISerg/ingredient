package catalog.ingredient.service.dto;

import java.util.List;

public record RelatedProductDto(
        Long ingredientRelatedProductId,
        Long relatedProductId,
        String productName,
        String productUrl,
        String title,
        String supplierName,
        String brandName,
        String gradeName,
        String relationType,
        String lastUpdatedRaw,
        String useLevel,
        String productLifeCycleStage,
        String appearance,
        String physicalForm,
        String odor,
        String color,
        String bioBased,
        String bioBasedContent,
        String chemicalComposition,
        String casNo,
        String ecNo,
        String note,
        String description,
        List<RelatedProductTagDto> tags,
        List<RelatedProductPropertyDto> properties,
        List<RelatedProductInciRowDto> inciRows
) {
}
