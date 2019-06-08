package hesge.legrand.tb.chatbot.conversation.model;

import com.ibm.cloud.sdk.core.service.exception.ServiceResponseException;
import com.ibm.cloud.sdk.core.service.model.GenericModel;
import com.ibm.cloud.sdk.core.service.security.IamOptions;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.*;
import com.ibm.watson.natural_language_understanding.v1.model.CategoriesResult;
import com.ibm.watson.natural_language_understanding.v1.model.ConceptsResult;
import hesge.legrand.tb.chatbot.helper.Constants;
import hesge.legrand.tb.chatbot.helper.Credentials;
import hesge.legrand.tb.education.model.Question;

import java.util.List;
import java.util.Map;

import static hesge.legrand.tb.chatbot.helper.Constants.*;

/**
 * Singleton pattern so that only 1 instance of chatbot/service is allowed
 *
 * Watson Assistant module that is responsible for receiving an input, classifying the intent,
 * sending utterance to Natural Language Understanding Module and returning the correct chatbot answer
 *
 */
public class WatsonAssistantModule {
    private static WatsonAssistantModule instance;
    private NaturalLanguageUnderstandingModule nlu;
    private Assistant assistant;
    private String sessionId;

    public static WatsonAssistantModule getInstance() {
        if (instance == null) {
            instance = new WatsonAssistantModule();
        }
        return instance;
    }

    private WatsonAssistantModule() {
        setCredentials();
        CreateSessionOptions sessionOptions = new CreateSessionOptions.Builder(
                Credentials.ASSISTANT_ID).build();

        SessionResponse sessionResponse = assistant
                .createSession(sessionOptions)
                .execute()
                .getResult();
        sessionId = sessionResponse.getSessionId();

        nlu = NaturalLanguageUnderstandingModule.getInstance();
    }

    private void setCredentials() {
            IamOptions options = new IamOptions.Builder()
                    .apiKey(Credentials.ASSISTANT_APIKEY)
                    .build();

            assistant = new Assistant(Credentials.ASSISTANT_VERSION, options);
            assistant.setEndPoint(Credentials.ASSISTANT_API_URL);
    } //setCredentials

    /**
     * Determines if the user wants to stop, needs a hint or has just answered the question
     * @param utterance : utterance of the user
     * @return either -1 if user wants to stop the questions sessions, 0 if the user answered the question,
     *                                                                      or 1 if the user asked for a hint
     */
    public int interactionState(String utterance) {
        int state = CODE_QUESTIONS_PROCEED;

        MessageResponse response = responseBuilder(utterance);
        List<DialogNodeAction> responseActions = response.getOutput().getActions();
        if (responseActions != null) {
            if (responseActions.get(0).getActionType().equalsIgnoreCase("client")) {
                String action = responseActions.get(0).getName();
                if (action.equalsIgnoreCase(ACTION_END_QUESTIONS)) {
                    state = CODE_QUESTIONS_END;
                    answer(response.getOutput().getGeneric());
                } else if (action.equalsIgnoreCase(ACTION_HELP_QUESTION)) {
                    answer(response.getOutput().getGeneric());
                    state = CODE_QUESTIONS_HELP;
                }
            }
        }

        return state;
    } //interactionState

    /**
     * This method gets the answer object from the Response
     * and print every utterance the chatbot has to print
     * @param chatbotAnswer
     */
    private void answer(List<DialogRuntimeResponseGeneric> chatbotAnswer){
        if (chatbotAnswer.size() > 0) {
            for (DialogRuntimeResponseGeneric answer : chatbotAnswer) {
                System.out.println(Constants.CHATBOT_TALK + answer.getText());
            }
        }
    } //answer

    /**
     * Utterance is computed by the implemented chatbot, detecting corresponding intent
     * and printing the chatbot's response in the terminal console
     * @param utterance : text input from the user
     */
    public DialogNodeAction answerUtterance(String utterance) {
        MessageResponse response = responseBuilder(utterance);

        /*  Text response from chatbot */
        List<DialogRuntimeResponseGeneric> chatbotResponse = response.getOutput().getGeneric();
        answer(chatbotResponse);

        /*  Check if any action is requested by the chatbot */
        DialogNodeAction currentAction = null;
        List<DialogNodeAction> responseActions = response.getOutput().getActions();
        if (responseActions != null) {
            if (responseActions.get(0).getActionType().equalsIgnoreCase("client")) {
                currentAction = responseActions.get(0);
            }
        }

        return currentAction;
    } //answerUtterance

    /**
     * This method make comparison between results of user's userPerformance and those of typical correct userPerformance
     * After analysis, responds with either perfect, good, partial, or bad feedback
     * @param userPerformance : The userPerformance the user wrote
     * @param question : the question the user answered to
     */
    public <T extends GenericModel> void assertAnswer(String userPerformance, Question question) {
        /*  Get correction from question  */
        List<CategoriesResult> correctCategories = question.getLstCategories();
        List<ConceptsResult> correctConcepts = question.getLstConcepts();

        if (correctCategories.isEmpty()) {
            sendFeedback(UNGRADABLE_ANSWER_LIMIT);
        } else {
            try {
                float correctness;

                /*  Get analysis from user performance  */
                Map<String, List<T>> answerAnalysis = nlu.setGradable(userPerformance);
                List<CategoriesResult> userCategories = (List<CategoriesResult>) answerAnalysis.get(CATEGORIES_ID);
                List<ConceptsResult> userConcepts = (List<ConceptsResult>) answerAnalysis.get(CONCEPTS_ID);

                /* equals method of Features is effective on both label AND relevance analyzed by NLU
                 * Then, if two categories have the same label but not the same relevance, they are not considered equals  */
                /*  categories correspondence percentage  */
                float inField = 0;
                for (CategoriesResult category : correctCategories) {
                    String categoryLabel = category.getLabel();
                    for (CategoriesResult userCategory : userCategories) {
                        String userCategoryLabel = userCategory.getLabel();
                        if (userCategoryLabel.equalsIgnoreCase(categoryLabel)) {
                            inField += 1;
                        }
                    }
                }
                inField /= correctCategories.size();

                if (!correctConcepts.isEmpty()) {
                    /*  concepts correspondence percentage  */
                    float precision = 0;
                    for (ConceptsResult concept : correctConcepts) {
                        String conceptLabel = concept.getText();
                        for (ConceptsResult userConcept : userConcepts) {
                            String userConceptLabel = userConcept.getText();
                            if (userConceptLabel.equalsIgnoreCase(conceptLabel)) {
                                precision += 1;
                            }
                        }
                    }
                    precision /= correctConcepts.size();

                    /*  We make categories correspondence more important than concept correspondence  */
                    correctness = ((2 * inField) + precision) / 3;
                } else {
                    correctness = inField;
                }

                System.out.printf("pourcentage de justesse : %.2f %n \n", correctness);
                sendFeedback(correctness);

                if (correctness < PERFECT_ANSWER_LIMIT) {
                    System.out.println(CHATBOT_TALK + FORMATIVE_FEEDBACK + question.getAnswer());
                }
            } catch (ServiceResponseException e) {
                System.out.println(CHATBOT_TALK + FEEDBACK_ANSWER_NOT_VALID);
            }
        }
    } //assertAnswer

    /**
     * Determines which feedback to give to a given answer based on percentage of correctness
     * @param correctness : correctness of the user's performance
     */
    private void sendFeedback(double correctness) {
        String feedback = CHATBOT_TALK;
        if (correctness != UNGRADABLE_ANSWER_LIMIT) {
            if (correctness > PERFECT_ANSWER_LIMIT) { feedback += FEEDBACK_PERFECT_ANSWER; }
            else if (correctness >= GOOD_ANSWER_LIMIT) { feedback += FEEDBACK_GOOD_ANSWER; }
            else if (correctness > ACCEPTABLE_ANSWER_LIMIT) { feedback += FEEDBACK_ACCEPTABLE_ANSWER; }
            else { feedback += FEEDBACK_BAD_ANSWER; }
        } else { feedback += FEEDBACK_UNGRADABLE; }
        System.out.println(feedback);
    } //sendFeedback

    /**
     * Boilerplate code needed for each utterance sent to chatbot API
     * @param utterance : utterance to be computed
     * @return Response from chatbot API
     */
    private MessageResponse responseBuilder(String utterance) {
        MessageInput input = new MessageInput.Builder()
                .messageType("text")
                .text(utterance)
                .build();

        MessageOptions messageOptions = new MessageOptions.Builder()
                .assistantId(Credentials.ASSISTANT_ID)
                .sessionId(sessionId)
                .input(input)
                .build();

        MessageResponse response = assistant
                .message(messageOptions)
                .execute()
                .getResult();

        return response;
    } //responseBuilder

    /**
     * This method closes the current session, thus ending the interaction with the chatbot
     */
    public void endInteraction() {
        DeleteSessionOptions deleteSessionOptions = new DeleteSessionOptions.Builder(
                Credentials.ASSISTANT_ID, sessionId)
                .build();
        assistant.deleteSession(deleteSessionOptions).execute();
    } //endInteraction
}
