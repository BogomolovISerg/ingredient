package catalog.ingredient.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "ingredient_name", schema = "core")
public class IngredientName {

    @EmbeddedId
    private IngredientNameId id;

    @MapsId("ingredientId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @jakarta.persistence.Column(name = "is_primary")
    private boolean primary;

    public IngredientNameId getId() { return id; }
    public void setId(IngredientNameId id) { this.id = id; }
    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }
    public boolean isPrimary() { return primary; }
    public void setPrimary(boolean primary) { this.primary = primary; }
    public String getName() { return id == null ? null : id.getName(); }
    public String getLang() { return id == null ? null : id.getLang(); }
    public String getNameType() { return id == null ? null : id.getNameType(); }
}

