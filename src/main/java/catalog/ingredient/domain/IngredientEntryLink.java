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
@Table(name = "ingredient_entry_link", schema = "reg")
public class IngredientEntryLink {

    @EmbeddedId
    private IngredientEntryLinkId id;

    @MapsId("ingredientId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @MapsId("entryId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_id")
    private RegulatoryEntry entry;

    @Column(name = "match_method")
    private String matchMethod;

    @Column(name = "confidence")
    private BigDecimal confidence;

    @Column(name = "note")
    private String note;

    public IngredientEntryLinkId getId() { return id; }
    public void setId(IngredientEntryLinkId id) { this.id = id; }
    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }
    public RegulatoryEntry getEntry() { return entry; }
    public void setEntry(RegulatoryEntry entry) { this.entry = entry; }
    public String getMatchMethod() { return matchMethod; }
    public void setMatchMethod(String matchMethod) { this.matchMethod = matchMethod; }
    public BigDecimal getConfidence() { return confidence; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}

