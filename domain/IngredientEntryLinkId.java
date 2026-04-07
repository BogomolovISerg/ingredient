package catalog.ingredient.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IngredientEntryLinkId implements Serializable {
    @Column(name = "ingredient_id")
    private Long ingredientId;

    @Column(name = "entry_id")
    private Long entryId;

    public IngredientEntryLinkId() {}

    public IngredientEntryLinkId(Long ingredientId, Long entryId) {
        this.ingredientId = ingredientId;
        this.entryId = entryId;
    }

    public Long getIngredientId() { return ingredientId; }
    public void setIngredientId(Long ingredientId) { this.ingredientId = ingredientId; }
    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IngredientEntryLinkId that)) return false;
        return Objects.equals(ingredientId, that.ingredientId) && Objects.equals(entryId, that.entryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredientId, entryId);
    }
}

