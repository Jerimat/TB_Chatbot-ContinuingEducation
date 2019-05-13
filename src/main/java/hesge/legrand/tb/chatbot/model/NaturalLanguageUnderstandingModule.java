package hesge.legrand.tb.chatbot.model;

import com.ibm.cloud.sdk.core.http.Response;
import com.ibm.cloud.sdk.core.http.ServiceCallback;
import com.ibm.cloud.sdk.core.service.model.GenericModel;
import com.ibm.cloud.sdk.core.service.security.IamOptions;
import com.ibm.watson.assistant.v2.model.MessageInput;
import com.ibm.watson.assistant.v2.model.MessageOptions;
import com.ibm.watson.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.natural_language_understanding.v1.model.*;
import hesge.legrand.tb.chatbot.Credentials;

public class NaturalLanguageUnderstandingModule {
    private static NaturalLanguageUnderstandingModule instance;
    private static NaturalLanguageUnderstanding nlu;
    private Features features;
    private WatsonAssistantModule assistant;

    public static NaturalLanguageUnderstandingModule getInstance() {
        if (instance == null) {
            instance = new NaturalLanguageUnderstandingModule();
        }
        return instance;
    }

    private NaturalLanguageUnderstandingModule() {
        setCredentials();
        setFeatures();
        assistant = WatsonAssistantModule.getInstance();
    }

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
                .keywords(keywords)
                .relations(relations)
                .build();
    }

    public void setFeatures(GenericModel... args) {
        // TODO()
    }

    private void setCredentials() {
        IamOptions options = new IamOptions.Builder()
                .apiKey(Credentials.NLU_APIKEY)
                .build();

        nlu = new NaturalLanguageUnderstanding(Credentials.NLU_VERSION, options);
        nlu.setEndPoint(Credentials.NLU_API_URL);
    }

    public void analyzeUtterance(final String utterance) {
        final AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                .text(utterance)
//                .url("https://www.20min.ch/ro/")
                .features(features)
                .build();

        nlu.analyze(parameters).enqueue(new ServiceCallback<AnalysisResults>() {
            public void onResponse(Response<AnalysisResults> response) {
                AnalysisResults results = response.getResult();

                System.out.println("--- Analyzis from Natural Language Understanding Module ---");
                System.out.println(results);
                System.out.println("--- End of Analyzis ---");

                assistant.answerUtterance(utterance);
            }

            public void onFailure(Exception e) {
                // TODO
            }
        });

    }

}
