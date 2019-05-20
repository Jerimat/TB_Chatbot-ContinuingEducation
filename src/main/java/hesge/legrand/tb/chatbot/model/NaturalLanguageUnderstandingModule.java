package hesge.legrand.tb.chatbot.model;

import com.ibm.cloud.sdk.core.http.Response;
import com.ibm.cloud.sdk.core.http.ServiceCallback;
import com.ibm.cloud.sdk.core.service.model.GenericModel;
import com.ibm.cloud.sdk.core.service.security.IamOptions;
import com.ibm.watson.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.natural_language_understanding.v1.model.*;
import hesge.legrand.tb.chatbot.Credentials;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

public class NaturalLanguageUnderstandingModule {
    private static NaturalLanguageUnderstandingModule instance;
    private static NaturalLanguageUnderstanding nlu;
    private Features features;
    private WatsonAssistantModule assistant;

    public static NaturalLanguageUnderstandingModule getInstance() {
        boolean assistantActivation = false; //choose if you want only NLU module or combination w/ Watson Assistant
        if (instance == null) {
            instance = new NaturalLanguageUnderstandingModule(assistantActivation);
        }
        return instance;
    }

    private NaturalLanguageUnderstandingModule(boolean assistantActivation) {
        setCredentials();
        setFeatures();
        if (assistantActivation) {
            assistant = WatsonAssistantModule.getInstance();
        }
    }

    private void setCredentials() {
        IamOptions options = new IamOptions.Builder()
                .apiKey(Credentials.NLU_APIKEY)
                .build();

        nlu = new NaturalLanguageUnderstanding(Credentials.NLU_VERSION, options);
        nlu.setEndPoint(Credentials.NLU_API_URL);
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
                .entities(entities)
                .keywords(keywords)
                .build();
    }

    public void setFeatures(GenericModel... args) {
        // TODO()
    }

    public void analyzeUtterance() {

        /**************************************************************/
        String filePath = "C:/Users/mathi/Dropbox/Travail/HEG/Semestre 8/Travail de bachelor - Mathieu Legrand/Modifiables/Conception/TB_UtterancesTypes_MathieuLegrand.csv";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));

            String row;
            System.out.println("--- Analyzis of 'entities' from Natural Language Understanding Module ---");
            System.out.println("[{");
            while ((row = bufferedReader.readLine()) != null) {
                String utterance = row.replace(",", "");

                final AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                        .text(utterance)
        //                .url("https://www.20min.ch/ro/")
                        .features(features)
                        .build();

                nlu.analyze(parameters).enqueue(new ServiceCallback<AnalysisResults>() {
                    public void onResponse(Response<AnalysisResults> response) {
                        AnalysisResults results = response.getResult();

                        System.out.println("  ==>  Utterance : " + utterance + "  <==  ");
                        /*  Analysis of entities recognized by NLU  */
                        List<EntitiesResult> entitiesResultList = results.getEntities();
                        System.out.println("----    entities    ----");
                        for (EntitiesResult entitiesResult : entitiesResultList) {
                            System.out.println("entity : " + entitiesResult.getType());
                            System.out.println("value : " + entitiesResult.getText());
                            System.out.println("confidence : " + entitiesResult.getRelevance());
                        }

                        /*  Analysis of categories recognized by NLU */
                        List<CategoriesResult> categoriesResults = results.getCategories();
                        System.out.println("----    categories    ----");
                        for (CategoriesResult categoriesResult : categoriesResults) {
                            System.out.println("category : " + categoriesResult.getLabel());
                            System.out.println("confidence : " + categoriesResult.getScore());
                        }

                        List<ConceptsResult> conceptsResults = results.getConcepts();
                        System.out.println("----    concepts    ----");
                        for (ConceptsResult conceptsResult : conceptsResults) {
                            System.out.println("concept : " + conceptsResult.getText());
                            System.out.println("confidence : " + conceptsResult.getRelevance());
                        }

                        List<KeywordsResult> keywordsResults = results.getKeywords();
                        System.out.println("----    keywords    ----");
                        for (KeywordsResult keywordsResult : keywordsResults) {
                            System.out.println("keyword : " + keywordsResult.getText());
                            System.out.println("relevance : " + keywordsResult.getRelevance());
                        }
                        System.out.println("---------------");
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
            }
            System.out.println("}]");
            System.out.println("--- End of Analyzis ---");
        } catch (java.io.IOException e) { e.printStackTrace(); }
        /**************************************************************/

        if (assistant != null) { //if WatsonAssistantModule has been activated
//            String waAnalysis = assistant.answerUtterance(utterance);
        } //WatsonAssistantModule computing
    }

}
