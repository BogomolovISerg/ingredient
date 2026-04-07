package catalog.ingredient.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ingredient_identifier", schema = "core")
public class IngredientIdentifier {

    @Id
    @Column(name = "ingredient_identifier_id")
    private Long ingredientIdentifierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(name = "id_type")
    private String idType;

    @Column(name = "id_value")
    private String idValue;

    @Column(name = "id_value_norm")
    private String idValueNorm;

    @Column(name = "is_primary")
    private boolean primary;

    @Column(name = "source_system")
    private String sourceSystem;

    @Column(name = "source_sheet")
    private String sourceSheet;

    @Column(name = "source_row_num")
    private Integer sourceRowNum;

    @Column(name = "raw_value")
    private String rawValue;

    public Long getIngredientIdentifierId() { return ingredientIdentifierId; }
    public void setIngredientIdentifierId(Long ingredientIdentifierId) { this.ingredientIdentifierId = ingredientIdentifierId; }
    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }
    public String getIdType() { return idType; }
    public void setIdType(String idType) { this.idType = idType; }
    public String getIdValue() { return idValue; }
    public void setIdValue(String idValue) { this.idValue = idValue; }
    public String getIdValueNorm() { return idValueNorm; }
    public void setIdValueNorm(String idValueNorm) { this.idValueNorm = idValueNorm; }
    public boolean isPrimary() { return primary; }
    public void setPrimary(boolean primary) { this.primary = primary; }
    public String getSourceSystem() { return sourceSystem; }
    public void setSourceSystem(String sourceSystem) { this.sourceSystem = sourceSystem; }
    public String getSourceSheet() { return sourceSheet; }
    public void setSourceSheet(String sourceSheet) { this.sourceSheet = sourceSheet; }
    public Integer getSourceRowNum() { return sourceRowNum; }
    public void setSourceRowNum(Integer sourceRowNum) { this.sourceRowNum = sourceRowNum; }
    public String getRawValue() { return rawValue; }
    public void setRawValue(String rawValue) { this.rawValue = rawValue; }
}

