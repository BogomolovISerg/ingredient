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
@Table(name = "ingredient_solubility", schema = "core")
public class IngredientSolubility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "solubility_id")
    private Long solubilityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "medium_type", nullable = false)
    private String mediumType;

    @Column(name = "solubility_class")
    private String solubilityClass;

    @Column(name = "solubility_text")
    private String solubilityText;

    @Column(name = "concentration_text")
    private String concentrationText;

    @Column(name = "temperature_c", precision = 10, scale = 2)
    private BigDecimal temperatureC;

    @Column(name = "source_system")
    private String sourceSystem;

    @Column(name = "source_sheet")
    private String sourceSheet;

    @Column(name = "source_row_num")
    private Integer sourceRowNum;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public IngredientSolubility createEditableCopy() {
        IngredientSolubility copy = new IngredientSolubility();
        copy.setSolubilityId(solubilityId);
        copy.applyEditableChangesFrom(this);
        return copy;
    }

    public void applyEditableChangesFrom(IngredientSolubility source) {
        Objects.requireNonNull(source, "source");

        setMediumType(source.getMediumType());
        setSolubilityClass(source.getSolubilityClass());
        setSolubilityText(source.getSolubilityText());
        setConcentrationText(source.getConcentrationText());
        setTemperatureC(source.getTemperatureC());
        setSourceSystem(source.getSourceSystem());
        setSourceSheet(source.getSourceSheet());
        setSourceRowNum(source.getSourceRowNum());
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    public Long getSolubilityId() {
        return solubilityId;
    }

    public void setSolubilityId(Long solubilityId) {
        this.solubilityId = solubilityId;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public String getMediumType() {
        return mediumType;
    }

    public void setMediumType(String mediumType) {
        this.mediumType = mediumType;
    }

    public String getSolubilityClass() {
        return solubilityClass;
    }

    public void setSolubilityClass(String solubilityClass) {
        this.solubilityClass = solubilityClass;
    }

    public String getSolubilityText() {
        return solubilityText;
    }

    public void setSolubilityText(String solubilityText) {
        this.solubilityText = solubilityText;
    }

    public String getConcentrationText() {
        return concentrationText;
    }

    public void setConcentrationText(String concentrationText) {
        this.concentrationText = concentrationText;
    }

    public BigDecimal getTemperatureC() {
        return temperatureC;
    }

    public void setTemperatureC(BigDecimal temperatureC) {
        this.temperatureC = temperatureC;
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
