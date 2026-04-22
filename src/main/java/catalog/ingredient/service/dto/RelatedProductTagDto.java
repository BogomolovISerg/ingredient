package catalog.ingredient.service.dto;

public record RelatedProductTagDto(
        String tagType,
        Integer tagOrd,
        String tagName,
        String tagUrl,
        Integer tagCount
) {
}
