package catalog.ingredient.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "substance_entry", schema = "reg")
public class RegulatoryEntry {

    @Id
    @Column(name = "entry_id")
    private Long entryId;

    @Column(name = "document_id")
    private Long documentId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "list_type", columnDefinition = "reg.substance_list_type")
    private RegulatoryListType listType;

    @Column(name = "eu_ref_no")
    private String euRefNo;

    @Column(name = "ru_name")
    private String ruName;

    @Column(name = "inci_inn_name")
    private String inciInnName;

    @Column(name = "chemical_name")
    private String chemicalName;

    @Column(name = "glossary_name")
    private String glossaryName;

    @Column(name = "ci_no")
    private String ciNo;

    @Column(name = "cas_no")
    private String casNo;

    @Column(name = "ec_no")
    private String ecNo;

    @Column(name = "color")
    private String color;

    @Column(name = "product_scope")
    private String productScope;

    @Column(name = "max_concentration")
    private String maxConcentration;

    @Column(name = "other_restrictions")
    private String otherRestrictions;

    @Column(name = "consumer_warnings")
    private String consumerWarnings;

    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }
    public Long getDocumentId() { return documentId; }
    public void setDocumentId(Long documentId) { this.documentId = documentId; }
    public RegulatoryListType getListType() { return listType; }
    public void setListType(RegulatoryListType listType) { this.listType = listType; }
    public String getEuRefNo() { return euRefNo; }
    public void setEuRefNo(String euRefNo) { this.euRefNo = euRefNo; }
    public String getRuName() { return ruName; }
    public void setRuName(String ruName) { this.ruName = ruName; }
    public String getInciInnName() { return inciInnName; }
    public void setInciInnName(String inciInnName) { this.inciInnName = inciInnName; }
    public String getChemicalName() { return chemicalName; }
    public void setChemicalName(String chemicalName) { this.chemicalName = chemicalName; }
    public String getGlossaryName() { return glossaryName; }
    public void setGlossaryName(String glossaryName) { this.glossaryName = glossaryName; }
    public String getCiNo() { return ciNo; }
    public void setCiNo(String ciNo) { this.ciNo = ciNo; }
    public String getCasNo() { return casNo; }
    public void setCasNo(String casNo) { this.casNo = casNo; }
    public String getEcNo() { return ecNo; }
    public void setEcNo(String ecNo) { this.ecNo = ecNo; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getProductScope() { return productScope; }
    public void setProductScope(String productScope) { this.productScope = productScope; }
    public String getMaxConcentration() { return maxConcentration; }
    public void setMaxConcentration(String maxConcentration) { this.maxConcentration = maxConcentration; }
    public String getOtherRestrictions() { return otherRestrictions; }
    public void setOtherRestrictions(String otherRestrictions) { this.otherRestrictions = otherRestrictions; }
    public String getConsumerWarnings() { return consumerWarnings; }
    public void setConsumerWarnings(String consumerWarnings) { this.consumerWarnings = consumerWarnings; }

    public String getDisplayName() {
        if (ruName != null && !ruName.isBlank()) {
            return ruName;
        }
        if (chemicalName != null && !chemicalName.isBlank()) {
            return chemicalName;
        }
        if (glossaryName != null && !glossaryName.isBlank()) {
            return glossaryName;
        }
        return inciInnName;
    }
}
