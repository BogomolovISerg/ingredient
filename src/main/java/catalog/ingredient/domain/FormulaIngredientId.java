package catalog.ingredient.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FormulaIngredientId implements Serializable {

    @Column(name = "formula_id")
    private Long formulaId;

    @Column(name = "ingredient_id")
    private Long ingredientId;

    public FormulaIngredientId() {}

    public FormulaIngredientId(Long formulaId, Long ingredientId) {
        this.formulaId = formulaId;
        this.ingredientId = ingredientId;
    }

    public Long getFormulaId() { return formulaId; }
    public void setFormulaId(Long formulaId) { this.formulaId = formulaId; }
    public Long getIngredientId() { return ingredientId; }
    public void setIngredientId(Long ingredientId) { this.ingredientId = ingredientId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FormulaIngredientId that)) return false;
        return Objects.equals(formulaId, that.formulaId) && Objects.equals(ingredientId, that.ingredientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formulaId, ingredientId);
    }
}

