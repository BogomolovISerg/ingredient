package catalog.ingredient.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "formula_ingredient", schema = "form")
public class FormulaIngredient {

    @EmbeddedId
    private FormulaIngredientId id;

    @MapsId("formulaId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formula_id")
    private Formula formula;

    @MapsId("ingredientId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(name = "percent_w_w")
    private BigDecimal percentWw;

    @Column(name = "function_role")
    private String functionRole;

    @Column(name = "note")
    private String note;

    public FormulaIngredientId getId() { return id; }
    public void setId(FormulaIngredientId id) { this.id = id; }
    public Formula getFormula() { return formula; }
    public void setFormula(Formula formula) { this.formula = formula; }
    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }
    public BigDecimal getPercentWw() { return percentWw; }
    public void setPercentWw(BigDecimal percentWw) { this.percentWw = percentWw; }
    public String getFunctionRole() { return functionRole; }
    public void setFunctionRole(String functionRole) { this.functionRole = functionRole; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}

