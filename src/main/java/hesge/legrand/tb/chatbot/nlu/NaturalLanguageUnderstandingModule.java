package hesge.legrand.tb.chatbot.nlu;

import com.ibm.cloud.sdk.core.service.security.IamOptions;
import com.ibm.watson.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.natural_language_understanding.v1.model.*;
import hesge.legrand.tb.chatbot.Credentials;

public class NaturalLanguageUnderstandingModule {

    public static void main(String[] args) {
        initializeNlu();
    }

    private static void initializeNlu() {

        IamOptions options = new IamOptions.Builder()
                .apiKey(Credentials.NLU_APIKEY)
                .build();

        NaturalLanguageUnderstanding naturalLanguageUnderstanding = new
                NaturalLanguageUnderstanding("2019-05-01", options);
        naturalLanguageUnderstanding.setEndPoint(Credentials.NLU_API_URL);

        String textAnalysis = "Le Natural Language Understanding utilise le traitement automatique du langage naturel pour analyse un texte";
        String textAnalysis2 = "En 1921, le prix Nobel de physique a été attribué à Albert Einstein";

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

        SentimentOptions sentiment = new SentimentOptions.Builder()
                .addTargets("Albert Einstein")
                .build();

        RelationsOptions relations = new RelationsOptions.Builder()
                .build();

        Features features = new Features.Builder()
                .categories(categories)
                .concepts(concepts)
                .keywords(keywords)
                .sentiment(sentiment)
                .relations(relations)
                .build();
        AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                .text(textAnalysis2)
//                .url("https://www.20min.ch/ro/")
                .features(features)
                .build();

        AnalysisResults results = naturalLanguageUnderstanding
                .analyze(parameters)
                .execute()
                .getResult();

        System.out.println(results);

    }

}
