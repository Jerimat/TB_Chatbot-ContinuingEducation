package hesge.legrand.tb.chatbot.conversation;

import hesge.legrand.tb.chatbot.model.NaturalLanguageUnderstandingModule;

public class EducativeChatbot {
    private static NaturalLanguageUnderstandingModule nlu;

    public static void main(String[] args) {
        initialize();
        testConversation();
    }

    private static void testConversation() {
        String utterance = "Aide moi en informatique";
        nlu.analyzeUtterance(utterance);
    }

    private static void initialize() {
        nlu = NaturalLanguageUnderstandingModule.getInstance();
    }

}
