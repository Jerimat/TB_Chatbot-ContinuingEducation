package hesge.legrand.tb.education.transport;

import com.google.gson.annotations.SerializedName;
import com.ibm.watson.natural_language_understanding.v1.model.CategoriesResult;
import com.ibm.watson.natural_language_understanding.v1.model.ConceptsResult;

import java.util.List;

public class QuestionDTO {

    @SerializedName("theme")
    private String theme;

    @SerializedName("questioning")
    private String questioning;

    @SerializedName("answer")
    private String answer;

    @SerializedName("hint")
    private String hint;

    @SerializedName("lstCategories")
    private List<CategoriesResult> lstCategories;

    @SerializedName("lstConcepts")
    private List<ConceptsResult> lstConcepts;

    public QuestionDTO() {
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getQuestioning() {
        return questioning;
    }

    public void setQuestioning(String questioning) {
        this.questioning = questioning;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public List<CategoriesResult> getLstCategories() {
        return lstCategories;
    }

    public void setLstCategories(List<CategoriesResult> lstCategories) {
        this.lstCategories = lstCategories;
    }

    public List<ConceptsResult> getLstConcepts() {
        return lstConcepts;
    }

    public void setLstConcepts(List<ConceptsResult> lstConcepts) {
        this.lstConcepts = lstConcepts;
    }
}
