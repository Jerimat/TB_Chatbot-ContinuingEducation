package hesge.legrand.tb.chatbot.model;

import com.ibm.cloud.sdk.core.http.Response;
import com.ibm.cloud.sdk.core.http.ServiceCallback;
import com.ibm.cloud.sdk.core.service.security.IamOptions;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.*;
import hesge.legrand.tb.chatbot.Credentials;

/**
 * Singleton pattern so that only 1 instance of chatbot/service is allowed
 *
 * Watson Assistant module that is responsible for receiving an input, classifying the intent,
 * sending input to Natural Language Understanding Module and returning the correct chatbot answer
 *
 */
class WatsonAssistantModule {
    private static WatsonAssistantModule instance;
    private Assistant assistant;
    private String sessionId;

    protected static WatsonAssistantModule getInstance() {
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
    }

    private void setCredentials() {
        IamOptions options = new IamOptions.Builder()
                .apiKey(Credentials.ASSISTANT_APIKEY)
                .build();

        assistant = new Assistant(Credentials.ASSISTANT_VERSION, options);
        assistant.setEndPoint(Credentials.ASSISTANT_API_URL);
    }

    protected void answerUtterance(String utterance) {
        MessageInput input = new MessageInput.Builder()
                .messageType("text")
                .text(utterance)
                .build();

        MessageOptions messageOptions = new MessageOptions.Builder()
                .assistantId(Credentials.ASSISTANT_ID)
                .sessionId(sessionId)
                .input(input)
                .build();

        assistant.message(messageOptions).enqueue(new ServiceCallback<MessageResponse>() {
            public void onResponse(Response<MessageResponse> response) {
                MessageResponse messageResponse = response.getResult();
                System.out.println("--- Response from Watson Assistant ---");
                System.out.println(messageResponse);
                System.out.println("--- End of Watson Assistant's Response ---");
            }

            public void onFailure(Exception e) {
                // TODO
            }
        });
    }

}
