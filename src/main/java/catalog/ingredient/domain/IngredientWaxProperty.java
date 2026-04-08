package catalog.ingredient.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "ingredient_wax_property", schema = "core")
public class IngredientWaxProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wax_property_id")
    private Long waxPropertyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "property_type", nullable = false)
    private String propertyType;

    @Column(name = "value_num", precision = 10, scale = 2)
    private BigDecimal valueNum;

    @Column(name = "unit_name", nullable = false)
    private String unitName = "degC";

    @Column(name = "value_text")
    private String valueText;

    @Column(name = "method_text")
    private String methodText;

    @Column(name = "source_system")
    private String sourceSystem;

    @Column(name = "source_sheet")
    private String sourceSheet;

    @Column(name = "source_row_num")
    private Integer sourceRowNum;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public IngredientWaxProperty createEditableCopy() {
        IngredientWaxProperty copy = new IngredientWaxProperty();
        copy.setWaxPropertyId(waxPropertyId);
        copy.applyEditableChangesFrom(this);
        return copy;
    }

    public void applyEditableChangesFrom(IngredientWaxProperty source) {
        Objects.requireNonNull(source, "source");

        setPropertyType(source.getPropertyType());
        setValueNum(source.getValueNum());
        setUnitName(source.getUnitName());
        setValueText(source.getValueText());
        setMethodText(source.getMethodText());
        setSourceSystem(source.getSourceSystem());
        setSourceSheet(source.getSourceSheet());
        setSourceRowNum(source.getSourceRowNum());
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (unitName == null || unitName.isBlank()) {
            unitName = "degC";
        }
    }

    public Long getWaxPropertyId() {
        return waxPropertyId;
    }

    public void setWaxPropertyId(Long waxPropertyId) {
        this.waxPropertyId = waxPropertyId;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public BigDecimal getValueNum() {
        return valueNum;
    }

    public void setValueNum(BigDecimal valueNum) {
        this.valueNum = valueNum;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getValueText() {
        return valueText;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }

    public String getMethodText() {
        return methodText;
    }

    public void setMethodText(String methodText) {
        this.methodText = methodText;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public String getSourceSheet() {
        return sourceSheet;
    }

    public void setSourceSheet(String sourceSheet) {
        this.sourceSheet = sourceSheet;
    }

    public Integer getSourceRowNum() {
        return sourceRowNum;
    }

    public void setSourceRowNum(Integer sourceRowNum) {
        this.sourceRowNum = sourceRowNum;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
