package catalog.ingredient.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ingredient_component", schema = "core")
public class IngredientComponent {

    @Id
    @Column(name = "ingredient_component_id")
    private Long ingredientComponentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_ingredient_id")
    private Ingredient parentIngredient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_ingredient_id")
    private Ingredient componentIngredient;

    @Column(name = "component_name_raw")
    private String componentNameRaw;

    @Column(name = "inci_raw")
    private String inciRaw;

    @Column(name = "chemical_name_raw")
    private String chemicalNameRaw;

    @Column(name = "function_raw")
    private String functionRaw;

    @Column(name = "purpose_raw")
    private String purposeRaw;

    @Column(name = "input_pct_raw")
    private String inputPctRaw;

    @Column(name = "cas_raw")
    private String casRaw;

    @Column(name = "ec_raw")
    private String ecRaw;

    @Column(name = "spec_text")
    private String specText;

    @Column(name = "calc_value_raw")
    private String calcValueRaw;

    @Column(name = "result_raw")
    private String resultRaw;

    @Column(name = "source_system")
    private String sourceSystem;

    @Column(name = "source_sheet")
    private String sourceSheet;

    @Column(name = "source_row_num")
    private Integer sourceRowNum;

    public Long getIngredientComponentId() { return ingredientComponentId; }
    public void setIngredientComponentId(Long ingredientComponentId) { this.ingredientComponentId = ingredientComponentId; }
    public Ingredient getParentIngredient() { return parentIngredient; }
    public void setParentIngredient(Ingredient parentIngredient) { this.parentIngredient = parentIngredient; }
    public Ingredient getComponentIngredient() { return componentIngredient; }
    public void setComponentIngredient(Ingredient componentIngredient) { this.componentIngredient = componentIngredient; }
    public String getComponentNameRaw() { return componentNameRaw; }
    public void setComponentNameRaw(String componentNameRaw) { this.componentNameRaw = componentNameRaw; }
    public String getInciRaw() { return inciRaw; }
    public void setInciRaw(String inciRaw) { this.inciRaw = inciRaw; }
    public String getChemicalNameRaw() { return chemicalNameRaw; }
    public void setChemicalNameRaw(String chemicalNameRaw) { this.chemicalNameRaw = chemicalNameRaw; }
    public String getFunctionRaw() { return functionRaw; }
    public void setFunctionRaw(String functionRaw) { this.functionRaw = functionRaw; }
    public String getPurposeRaw() { return purposeRaw; }
    public void setPurposeRaw(String purposeRaw) { this.purposeRaw = purposeRaw; }
    public String getInputPctRaw() { return inputPctRaw; }
    public void setInputPctRaw(String inputPctRaw) { this.inputPctRaw = inputPctRaw; }
    public String getCasRaw() { return casRaw; }
    public void setCasRaw(String casRaw) { this.casRaw = casRaw; }
    public String getEcRaw() { return ecRaw; }
    public void setEcRaw(String ecRaw) { this.ecRaw = ecRaw; }
    public String getSpecText() { return specText; }
    public void setSpecText(String specText) { this.specText = specText; }
    public String getCalcValueRaw() { return calcValueRaw; }
    public void setCalcValueRaw(String calcValueRaw) { this.calcValueRaw = calcValueRaw; }
    public String getResultRaw() { return resultRaw; }
    public void setResultRaw(String resultRaw) { this.resultRaw = resultRaw; }
    public String getSourceSystem() { return sourceSystem; }
    public void setSourceSystem(String sourceSystem) { this.sourceSystem = sourceSystem; }
    public String getSourceSheet() { return sourceSheet; }
    public void setSourceSheet(String sourceSheet) { this.sourceSheet = sourceSheet; }
    public Integer getSourceRowNum() { return sourceRowNum; }
    public void setSourceRowNum(Integer sourceRowNum) { this.sourceRowNum = sourceRowNum; }
}

