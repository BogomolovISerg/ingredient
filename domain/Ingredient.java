package catalog.ingredient.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "ingredient", schema = "core")
public class Ingredient {

    @Id
    @Column(name = "ingredient_id")
    private Long ingredientId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "kind", nullable = false, columnDefinition = "core.ingredient_kind")
    private IngredientKind kind;

    @Column(name = "primary_name", nullable = false)
    private String primaryName;

    @Column(name = "inci_name")
    private String inciName;

    @Column(name = "cas_no")
    private String casNo;

    @Column(name = "ec_no")
    private String ecNo;

    @Column(name = "ci_no")
    private String ciNo;

    @Column(name = "note")
    private String note;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "supplier_code")
    private String supplierCode;

    @Column(name = "sds_url")
    private String sdsUrl;

    @Column(name = "description_en")
    private String descriptionEn;

    @Column(name = "description_ru")
    private String descriptionRu;

    @OneToMany(mappedBy = "ingredient", fetch = FetchType.LAZY)
    private Set<IngredientName> names = new LinkedHashSet<>();

    @OneToMany(mappedBy = "ingredient", fetch = FetchType.LAZY)
    private Set<IngredientIdentifier> identifiers = new LinkedHashSet<>();

    public Long getIngredientId() { return ingredientId; }
    public void setIngredientId(Long ingredientId) { this.ingredientId = ingredientId; }
    public IngredientKind getKind() { return kind; }
    public void setKind(IngredientKind kind) { this.kind = kind; }
    public String getPrimaryName() { return primaryName; }
    public void setPrimaryName(String primaryName) { this.primaryName = primaryName; }
    public String getInciName() { return inciName; }
    public void setInciName(String inciName) { this.inciName = inciName; }
    public String getCasNo() { return casNo; }
    public void setCasNo(String casNo) { this.casNo = casNo; }
    public String getEcNo() { return ecNo; }
    public void setEcNo(String ecNo) { this.ecNo = ecNo; }
    public String getCiNo() { return ciNo; }
    public void setCiNo(String ciNo) { this.ciNo = ciNo; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getSupplierCode() { return supplierCode; }
    public void setSupplierCode(String supplierCode) { this.supplierCode = supplierCode; }
    public String getSdsUrl() { return sdsUrl; }
    public void setSdsUrl(String sdsUrl) { this.sdsUrl = sdsUrl; }
    public String getDescriptionEn() { return descriptionEn; }
    public void setDescriptionEn(String descriptionEn) { this.descriptionEn = descriptionEn; }
    public String getDescriptionRu() { return descriptionRu; }
    public void setDescriptionRu(String descriptionRu) { this.descriptionRu = descriptionRu; }
    public Set<IngredientName> getNames() { return names; }
    public Set<IngredientIdentifier> getIdentifiers() { return identifiers; }

    public boolean isMixture() {
        return IngredientKind.MIXTURE.equals(kind);
    }

    public String getDisplayIdentity() {
        return primaryName + (inciName == null || inciName.isBlank() ? "" : " / " + inciName);
    }
}
