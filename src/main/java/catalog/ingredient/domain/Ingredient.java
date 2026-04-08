package catalog.ingredient.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
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
    @Column(name = "kind", nullable = false)
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

    @Column(name = "description_ru")
    private String descriptionRu;

    @Column(name = "description_en")
    private String descriptionEn;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = Boolean.FALSE;

    @Transient
    private String functionDisplay;

    @OneToMany(mappedBy = "ingredient", fetch = FetchType.LAZY)
    private Set<IngredientName> names = new LinkedHashSet<>();

    @OneToMany(mappedBy = "ingredient", fetch = FetchType.LAZY)
    private Set<IngredientIdentifier> identifiers = new LinkedHashSet<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> payload = new LinkedHashMap<>();

    @Column(name = "specialchem_url")
    private String specialchemUrl;

    @Column(name = "specialchem_origin_en")
    private String specialchemOriginEn;

    @Column(name = "specialchem_origin_ru")
    private String specialchemOriginRu;

    @Column(name = "specialchem_safety_profile_en")
    private String specialchemSafetyProfileEn;

    @Column(name = "specialchem_safety_profile_ru")
    private String specialchemSafetyProfileRu;

    @Column(name = "specialchem_chem_iupac_name_en")
    private String specialchemChemIupacNameEn;

    @Column(name = "specialchem_chem_iupac_name_ru")
    private String specialchemChemIupacNameRu;

    @Column(name = "specialchem_usage_text_en")
    private String specialchemUsageTextEn;

    @Column(name = "specialchem_usage_text_ru")
    private String specialchemUsageTextRu;

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public IngredientKind getKind() {
        return kind;
    }

    public void setKind(IngredientKind kind) {
        this.kind = kind;
    }

    public String getPrimaryName() {
        return primaryName;
    }

    public void setPrimaryName(String primaryName) {
        this.primaryName = primaryName;
    }

    public String getInciName() {
        return inciName;
    }

    public void setInciName(String inciName) {
        this.inciName = inciName;
    }

    public String getCasNo() {
        return casNo;
    }

    public void setCasNo(String casNo) {
        this.casNo = casNo;
    }

    public String getEcNo() {
        return ecNo;
    }

    public void setEcNo(String ecNo) {
        this.ecNo = ecNo;
    }

    public String getCiNo() {
        return ciNo;
    }

    public void setCiNo(String ciNo) {
        this.ciNo = ciNo;
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

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSdsUrl() {
        return sdsUrl;
    }

    public void setSdsUrl(String sdsUrl) {
        this.sdsUrl = sdsUrl;
    }

    public String getDescriptionRu() {
        return descriptionRu;
    }

    public void setDescriptionRu(String descriptionRu) {
        this.descriptionRu = descriptionRu;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted == null ? Boolean.FALSE : deleted;
    }

    public boolean isDeleted() {
        return Boolean.TRUE.equals(deleted);
    }

    public String getFunctionDisplay() {
        return functionDisplay;
    }

    public void setFunctionDisplay(String functionDisplay) {
        this.functionDisplay = functionDisplay;
    }

    public Set<IngredientName> getNames() {
        return names;
    }

    public Set<IngredientIdentifier> getIdentifiers() {
        return identifiers;
    }

    public boolean isMixture() {
        return IngredientKind.MIXTURE.equals(kind);
    }

    public String getDisplayIdentity() {
        return primaryName + (inciName == null || inciName.isBlank() ? "" : " / " + inciName);
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload == null ? new LinkedHashMap<>() : payload;
    }

    public String getSpecialchemUrl() {
        return specialchemUrl;
    }

    public void setSpecialchemUrl(String specialchemUrl) {
        this.specialchemUrl = specialchemUrl;
    }

    public String getSpecialchemOriginEn() {
        return specialchemOriginEn;
    }

    public void setSpecialchemOriginEn(String specialchemOriginEn) {
        this.specialchemOriginEn = specialchemOriginEn;
    }

    public String getSpecialchemOriginRu() {
        return specialchemOriginRu;
    }

    public void setSpecialchemOriginRu(String specialchemOriginRu) {
        this.specialchemOriginRu = specialchemOriginRu;
    }

    public String getSpecialchemSafetyProfileEn() {
        return specialchemSafetyProfileEn;
    }

    public void setSpecialchemSafetyProfileEn(String specialchemSafetyProfileEn) {
        this.specialchemSafetyProfileEn = specialchemSafetyProfileEn;
    }

    public String getSpecialchemSafetyProfileRu() {
        return specialchemSafetyProfileRu;
    }

    public void setSpecialchemSafetyProfileRu(String specialchemSafetyProfileRu) {
        this.specialchemSafetyProfileRu = specialchemSafetyProfileRu;
    }

    public String getSpecialchemChemIupacNameEn() {
        return specialchemChemIupacNameEn;
    }

    public void setSpecialchemChemIupacNameEn(String specialchemChemIupacNameEn) {
        this.specialchemChemIupacNameEn = specialchemChemIupacNameEn;
    }

    public String getSpecialchemChemIupacNameRu() {
        return specialchemChemIupacNameRu;
    }

    public void setSpecialchemChemIupacNameRu(String specialchemChemIupacNameRu) {
        this.specialchemChemIupacNameRu = specialchemChemIupacNameRu;
    }

    public String getSpecialchemUsageTextEn() {
        return specialchemUsageTextEn;
    }

    public void setSpecialchemUsageTextEn(String specialchemUsageTextEn) {
        this.specialchemUsageTextEn = specialchemUsageTextEn;
    }

    public String getSpecialchemUsageTextRu() {
        return specialchemUsageTextRu;
    }

    public void setSpecialchemUsageTextRu(String specialchemUsageTextRu) {
        this.specialchemUsageTextRu = specialchemUsageTextRu;
    }
}
