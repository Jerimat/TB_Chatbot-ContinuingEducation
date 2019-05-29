package hesge.legrand.tb.chatbot.conversation.model;

import com.ibm.cloud.sdk.core.service.security.IamOptions;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.*;
import hesge.legrand.tb.chatbot.helper.Constants;
import hesge.legrand.tb.chatbot.helper.Credentials;

import java.util.List;

import static hesge.legrand.tb.chatbot.helper.Constants.ACTION_END_QUESTIONS;

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

    public boolean isStop(String answer) {
        boolean isStop = false;

        MessageResponse response = responseBuilder(answer);
        List<DialogNodeAction> responseActions = response.getOutput().getActions();
        if (responseActions != null) {
            if (responseActions.get(0).getActionType().equalsIgnoreCase("client")) {
                if (responseActions.get(0).getName().equalsIgnoreCase(ACTION_END_QUESTIONS)) {
                    isStop = true;
                }
            }
        }

        return isStop;
    } //isStop

    /**
     * Utterance is computed by the implemented chatbot, detecting corresponding intent
     * and printing the chatbot's response in the terminal console
     * @param utterance : text input from the user
     */
    public DialogNodeAction answerUtterance(String utterance) {
        MessageResponse response = responseBuilder(utterance);

        System.out.println("--- Response from Watson Assistant ---");
        /*  Detection of intents  */
        List<RuntimeIntent> responseIntents = response.getOutput().getIntents();
        if (responseIntents.size() > 0) {
            System.out.println("Detected intent : #" + responseIntents.get(0).getIntent());
//            System.console().printf("Detected intent : #" + responseIntents.get(0).getIntent());
        }

        /*  Text response from chatbot */
        List<DialogRuntimeResponseGeneric> chatbotResponse = response.getOutput().getGeneric();
        if (chatbotResponse.size() > 0) {
            for (DialogRuntimeResponseGeneric answer : chatbotResponse) {
                System.out.println(Constants.CHATBOT_TALK + answer.getText());
//                System.console().printf(Constants.CHATBOT_TALK + answer.getText());
            }
        }

        /*  Check if any action is requested by the chatbot */
        DialogNodeAction currentAction = null;
        List<DialogNodeAction> responseActions = response.getOutput().getActions();
        if (responseActions != null) {
            if (responseActions.get(0).getActionType().equalsIgnoreCase("client")) {
                currentAction = responseActions.get(0);
            }
        }

        System.out.println("--- End of Watson Assistant's Response ---");
        return currentAction;
    } //answerUtterance

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
