package hesge.legrand.tb.chatbot.assistant;

import com.ibm.cloud.sdk.core.service.security.IamOptions;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.*;
import hesge.legrand.tb.chatbot.Credentials;

public class WatsonAssistantModule {

    public static void main(String[] args) {
        initializeAssistant();
    }

    private static void initializeAssistant() {

        IamOptions options = new IamOptions.Builder()
                .apiKey(Credentials.ASSISTANT_APIKEY)
                .build();

        Assistant assistant = new Assistant("2019-03-31", options);
        assistant.setEndPoint(Credentials.ASSISTANT_API_URL);

        CreateSessionOptions sessionOptions = new CreateSessionOptions.Builder(
                Credentials.ASSISTANT_ID).build();

        SessionResponse sessionResponse = assistant
                .createSession(sessionOptions)
                .execute()
                .getResult();
        String sessionId = sessionResponse.getSessionId();

        MessageInput input = new MessageInput.Builder()
                .text("salut")
                .build();
        MessageOptions messageOptions = new MessageOptions.Builder()
                .assistantId(Credentials.ASSISTANT_ID)
                .sessionId(sessionId)
                .input(input)
                .build();
        MessageResponse messageResponse = assistant
                .message(messageOptions)
                .execute()
                .getResult();

        System.out.println(messageResponse);

    }

}
