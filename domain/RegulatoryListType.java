package catalog.ingredient.domain;

import jakarta.persistence.EnumeratedValue;

public enum RegulatoryListType {
    PROHIBITED("prohibited"),
    RESTRICTED("restricted"),
    COLORANT("colorant"),
    PRESERVATIVE("preservative"),
    UV_FILTER("uv_filter");

    @EnumeratedValue
    private final String dbValue;

    RegulatoryListType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static RegulatoryListType fromDb(String value) {
        if (value == null) {
            return null;
        }
        for (RegulatoryListType type : values()) {
            if (type.dbValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown regulatory list type: " + value);
    }
}
