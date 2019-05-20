package hesge.legrand.tb.chatbot.conversation;

import hesge.legrand.tb.chatbot.model.NaturalLanguageUnderstandingModule;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EducativeChatbot {
    private static NaturalLanguageUnderstandingModule nlu;

    public static void main(String[] args) {
        initialize();

        nlu.analyzeUtterance();
    } //main


    private static void initialize() {
        nlu = NaturalLanguageUnderstandingModule.getInstance();
    } //initialize

}
