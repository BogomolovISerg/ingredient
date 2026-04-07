package catalog.ingredient.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ingredient_test_log", schema = "core")
public class IngredientTestLog {

    @Id
    @Column(name = "test_log_id")
    private Long testLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(name = "test_result")
    private String testResult;

    @Column(name = "test_note")
    private String testNote;

    @Column(name = "source_system")
    private String sourceSystem;

    @Column(name = "source_sheet")
    private String sourceSheet;

    @Column(name = "source_row_num")
    private Integer sourceRowNum;

    public Long getTestLogId() { return testLogId; }
    public void setTestLogId(Long testLogId) { this.testLogId = testLogId; }
    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }
    public String getTestResult() { return testResult; }
    public void setTestResult(String testResult) { this.testResult = testResult; }
    public String getTestNote() { return testNote; }
    public void setTestNote(String testNote) { this.testNote = testNote; }
    public String getSourceSystem() { return sourceSystem; }
    public void setSourceSystem(String sourceSystem) { this.sourceSystem = sourceSystem; }
    public String getSourceSheet() { return sourceSheet; }
    public void setSourceSheet(String sourceSheet) { this.sourceSheet = sourceSheet; }
    public Integer getSourceRowNum() { return sourceRowNum; }
    public void setSourceRowNum(Integer sourceRowNum) { this.sourceRowNum = sourceRowNum; }
}

