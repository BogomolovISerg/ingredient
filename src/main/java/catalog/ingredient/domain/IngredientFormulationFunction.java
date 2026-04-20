package catalog.ingredient.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ingredient_formulation_function", schema = "core")
public class IngredientFormulationFunction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_formulation_function_id")
    private Long ingredientFormulationFunctionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "function_name", nullable = false)
    private String functionName;

    @Column(name = "source_system", nullable = false)
    private String sourceSystem;

    @Column(name = "source_table", nullable = false)
    private String sourceTable;

    @Column(name = "source_row_id", nullable = false)
    private Long sourceRowId;

    @Column(name = "note")
    private String note;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public Long getIngredientFormulationFunctionId() {
        return ingredientFormulationFunctionId;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public String getFunctionName() {
        return functionName;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public Long getSourceRowId() {
        return sourceRowId;
    }

    public String getNote() {
        return note;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
