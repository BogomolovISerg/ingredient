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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "ingredient_solvent", schema = "core")
public class IngredientSolvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_solvent_id")
    private Long ingredientSolventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "solvent_name", nullable = false)
    private String solventName;

    @Column(name = "solvent_name_norm")
    private String solventNameNorm;

    @Column(name = "note")
    private String note;

    @Column(name = "source_system")
    private String sourceSystem;

    @Column(name = "source_sheet")
    private String sourceSheet;

    @Column(name = "source_row_num")
    private Integer sourceRowNum;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public IngredientSolvent createEditableCopy() {
        IngredientSolvent copy = new IngredientSolvent();
        copy.setIngredientSolventId(ingredientSolventId);
        copy.applyEditableChangesFrom(this);
        return copy;
    }

    public void applyEditableChangesFrom(IngredientSolvent source) {
        Objects.requireNonNull(source, "source");

        setSolventName(source.getSolventName());
        setNote(source.getNote());
        setSourceSystem(source.getSourceSystem());
        setSourceSheet(source.getSourceSheet());
        setSourceRowNum(source.getSourceRowNum());
    }

    @PrePersist
    @PreUpdate
    void normalize() {
        solventNameNorm = normalizeSolventName(solventName);
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    private String normalizeSolventName(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim().replaceAll("\\s+", " ");
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.toLowerCase(Locale.ROOT);
    }

    public Long getIngredientSolventId() {
        return ingredientSolventId;
    }

    public void setIngredientSolventId(Long ingredientSolventId) {
        this.ingredientSolventId = ingredientSolventId;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public String getSolventName() {
        return solventName;
    }

    public void setSolventName(String solventName) {
        this.solventName = solventName;
    }

    public String getSolventNameNorm() {
        return solventNameNorm;
    }

    public void setSolventNameNorm(String solventNameNorm) {
        this.solventNameNorm = solventNameNorm;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
