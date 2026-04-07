package catalog.ingredient.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class IngredientKindConverter implements AttributeConverter<IngredientKind, String> {
    @Override
    public String convertToDatabaseColumn(IngredientKind attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public IngredientKind convertToEntityAttribute(String dbData) {
        return IngredientKind.fromDb(dbData);
    }
}

