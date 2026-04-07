package catalog.ingredient.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class RegulatoryListTypeConverter implements AttributeConverter<RegulatoryListType, String> {
    @Override
    public String convertToDatabaseColumn(RegulatoryListType attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public RegulatoryListType convertToEntityAttribute(String dbData) {
        return RegulatoryListType.fromDb(dbData);
    }
}

