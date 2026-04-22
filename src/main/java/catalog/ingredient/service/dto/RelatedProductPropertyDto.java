package catalog.ingredient.service.dto;

public record RelatedProductPropertyDto(
        String propertyType,
        Integer propertyOrd,
        String propertyGroup,
        String propertyName,
        String propertyValue,
        String siValue,
        String imperialValue,
        String testCondition,
        String testMethod
) {
}
