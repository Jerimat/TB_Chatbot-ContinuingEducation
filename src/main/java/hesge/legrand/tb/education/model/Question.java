package hesge.legrand.tb.education.model;

import com.ibm.watson.natural_language_understanding.v1.model.CategoriesResult;
import com.ibm.watson.natural_language_understanding.v1.model.ConceptsResult;

import java.util.List;

public class Question {

    private Theme theme;
    private String questioning;
    private String answer;
    private String hint;
    private List<CategoriesResult> lstCategories;
    private List<ConceptsResult> lstConcepts;

    public Question(Theme theme, String questioning, String answer, List<CategoriesResult> lstCategories, List<ConceptsResult> lstConcepts) {
        this.theme = theme;
        this.questioning = questioning;
        this.answer = answer;
        this.lstCategories = lstCategories;
        this.lstConcepts = lstConcepts;
    }

    public Question(Theme theme, String questioning, String answer, String hint, List<CategoriesResult> lstCategories, List<ConceptsResult> lstConcepts) {
        this.theme = theme;
        this.questioning = questioning;
        this.answer = answer;
        this.hint = hint;
        this.lstCategories = lstCategories;
        this.lstConcepts = lstConcepts;
    }

    public Theme getTheme() {
        return theme;
    }

    public String getQuestioning() {
        return questioning;
    }

    public String getAnswer() {
        return answer;
    }

    public String getHint() {
        return hint;
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

    @Override
    public String toString() {
        return "Question{" +
                "theme='" + theme + '\'' +
                ", questioning='" + questioning + '\'' +
                ", answer='" + answer + '\'' +
                ", hint='" + hint + '\'' +
                ", lstCategories=" + lstCategories +
                ", lstConcepts=" + lstConcepts +
                '}';
    }
}
