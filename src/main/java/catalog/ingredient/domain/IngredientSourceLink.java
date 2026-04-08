package catalog.ingredient.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ingredient_source_link", schema = "core")
public class IngredientSourceLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_source_link_id")
    private Long ingredientSourceLinkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(name = "source_system", nullable = false)
    private String sourceSystem;

    @Column(name = "source_table", nullable = false)
    private String sourceTable;

    @Column(name = "source_row_id", nullable = false)
    private Long sourceRowId;

    @Column(name = "source_business_key")
    private String sourceBusinessKey;

    @Column(name = "match_method", nullable = false)
    private String matchMethod;

    @Column(name = "confidence")
    private BigDecimal confidence;

    @Column(name = "note")
    private String note;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public Long getIngredientSourceLinkId() {
        return ingredientSourceLinkId;
    }

    public void setIngredientSourceLinkId(Long ingredientSourceLinkId) {
        this.ingredientSourceLinkId = ingredientSourceLinkId;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    public Long getSourceRowId() {
        return sourceRowId;
    }

    public void setSourceRowId(Long sourceRowId) {
        this.sourceRowId = sourceRowId;
    }

    public String getSourceBusinessKey() {
        return sourceBusinessKey;
    }

    public void setSourceBusinessKey(String sourceBusinessKey) {
        this.sourceBusinessKey = sourceBusinessKey;
    }

    public String getMatchMethod() {
        return matchMethod;
    }

    public void setMatchMethod(String matchMethod) {
        this.matchMethod = matchMethod;
    }

    public BigDecimal getConfidence() {
        return confidence;
    }

    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}