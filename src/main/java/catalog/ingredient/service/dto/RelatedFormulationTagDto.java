package catalog.ingredient.service.dto;

public record RelatedFormulationTagDto(
        String tagType,
        Integer tagOrd,
        String tagName,
        String tagUrl,
        Integer tagCount
) {
}
