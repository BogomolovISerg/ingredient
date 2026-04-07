package catalog.ingredient.domain;

public enum IngredientKind {
    SUBSTANCE("substance"),
    MIXTURE("mixture"),
    MATERIAL("material");

    private final String dbValue;

    IngredientKind(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static IngredientKind fromDb(String value) {
        if (value == null) {
            return null;
        }
        for (IngredientKind kind : values()) {
            if (kind.dbValue.equalsIgnoreCase(value)) {
                return kind;
            }
        }
        throw new IllegalArgumentException("Unknown ingredient kind: " + value);
    }
}

