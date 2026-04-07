package catalog.ingredient.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IngredientNameId implements Serializable {

    @Column(name = "ingredient_id")
    private Long ingredientId;

    @Column(name = "name")
    private String name;

    @Column(name = "lang")
    private String lang;

    @Column(name = "name_type")
    private String nameType;

    public IngredientNameId() {}

    public IngredientNameId(Long ingredientId, String name, String lang, String nameType) {
        this.ingredientId = ingredientId;
        this.name = name;
        this.lang = lang;
        this.nameType = nameType;
    }

    public Long getIngredientId() { return ingredientId; }
    public void setIngredientId(Long ingredientId) { this.ingredientId = ingredientId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLang() { return lang; }
    public void setLang(String lang) { this.lang = lang; }
    public String getNameType() { return nameType; }
    public void setNameType(String nameType) { this.nameType = nameType; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IngredientNameId that)) return false;
        return Objects.equals(ingredientId, that.ingredientId) && Objects.equals(name, that.name)
                && Objects.equals(lang, that.lang) && Objects.equals(nameType, that.nameType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredientId, name, lang, nameType);
    }
}

