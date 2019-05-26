package hesge.legrand.tb.chatbot.conversation.model;

import com.ibm.cloud.sdk.core.service.model.GenericModel;
import com.ibm.cloud.sdk.core.service.security.IamOptions;
import com.ibm.watson.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.natural_language_understanding.v1.model.*;
import hesge.legrand.tb.chatbot.helper.Constants;
import hesge.legrand.tb.chatbot.helper.Credentials;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NaturalLanguageUnderstandingModule {
    private static NaturalLanguageUnderstandingModule instance;
    private static NaturalLanguageUnderstanding nlu;
    private Features features;

    public static NaturalLanguageUnderstandingModule getInstance() {
        if (instance == null) {
            instance = new NaturalLanguageUnderstandingModule();
        }
        return instance;
    }

    /* Constructor */
    private NaturalLanguageUnderstandingModule() {
        setCredentials();
        setFeatures();
    }

    private void setCredentials() {
        IamOptions options = new IamOptions.Builder()
                .apiKey(Credentials.NLU_APIKEY)
                .build();

        nlu = new NaturalLanguageUnderstanding(Credentials.NLU_VERSION, options);
        nlu.setEndPoint(Credentials.NLU_API_URL);
    } //setCredentials

    private void setFeatures() {
        CategoriesOptions categories = new CategoriesOptions.Builder()
                .limit(3)
                .build();

        ConceptsOptions concepts = new ConceptsOptions.Builder()
                .limit(3)
                .build();

        EntitiesOptions entities = new EntitiesOptions.Builder()
                .limit(5)
                .build();

        KeywordsOptions keywords = new KeywordsOptions.Builder()
                .limit(3)
                .build();

        RelationsOptions relations = new RelationsOptions.Builder()
                .build();

        features = new Features.Builder()
                .categories(categories)
                .concepts(concepts)
                .entities(entities)
                .keywords(keywords)
                .build();
    } //setFeatures

    public void setFeatures(GenericModel... args) {
        // TODO()
    }

    public void analyzeUtterance(String utterance) {
        AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                .text(utterance)
                .features(features)
                .build();

        AnalysisResults response = nlu
                .analyze(parameters)
                .execute()
                .getResult();

    } //analyzeUtterance

    /**
     * This method analyze a typical correct answer  to a question
     * to detect which categories and concepts should stand out from the analysis of the user's answer to the given question
     *
     * @param typicalAnswer : the kind of answer the user should answer the corresponding question with
     * @param <T> : Since this method returns 2 different types of Analysis we must specify that
     *           both CategoriesResult and ConcceptResult inherit from GenericModel
     *
     * @return a Map of 2 List =>
     * 1 List<CategoriesResult> containing the categories concerned by the typical correct answer
     * 1 List<ConceptResult> containing the concepts concerned by the typical correct answer
     */
    public <T extends GenericModel> Map<String, List<T>> setRecognizable(String typicalAnswer) {
        Map<String, List<T>> questionAnalysis = new HashMap<>();

        AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                .text(typicalAnswer)
                .features(features)
                .build();

        AnalysisResults results = nlu
                .analyze(parameters)
                .execute()
                .getResult();

        /*  Get the categories & concepts result of the typicalAnswer analysis */
        List<CategoriesResult> categories = results.getCategories();
        List<ConceptsResult> concepts = results.getConcepts();

        questionAnalysis.put(Constants.CATEGORIES_ID, (List<T>) categories);
        questionAnalysis.put(Constants.CONCEPTS_ID, (List<T>) concepts);

        return questionAnalysis;
    } //setRecognizable

}
