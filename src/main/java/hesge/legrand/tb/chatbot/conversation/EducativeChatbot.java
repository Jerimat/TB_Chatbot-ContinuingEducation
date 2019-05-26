package hesge.legrand.tb.chatbot.conversation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import hesge.legrand.tb.chatbot.conversation.model.WatsonAssistantModule;
import hesge.legrand.tb.education.model.Question;
import hesge.legrand.tb.education.model.Theme;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;

public class EducativeChatbot {
    private static WatsonAssistantModule assistant;
    private static List<Question> lstQuestions;
    private static List<Question> targetTheme;

    public static void main(String[] args) {
        if (args.length == 1) {
            String filePath = args[0];

            initialize();
            deserializeQuestions(filePath);
//            filterQuestions();
            interactWithAssistant();
        } else {
            System.console().printf("You must provide 1 argument : path of the .json file resulting from hesge.legrand.tb.education.Initializer execution");
        }
    } //main

    private static void interactWithAssistant() {
        Console console = System.console();
        LogManager.getLogManager().reset();

        String inputText = "";
        do {
            assistant.answerUtterance(inputText);
            /*  next round of input   */
            System.out.print(">> ");
            inputText = console.readLine();
        } while (!inputText.equals("quit"));

        /*  Delete session when done */
        assistant.endInteraction();
    } //interactWithAssistant

    private static void filterQuestions(Theme theme) {
        for (Question question : lstQuestions) {
        }
    } //filterQuestions

    private static void deserializeQuestions(String filePath) {
        Type listType = new TypeToken<ArrayList<Question>>() {}.getType();
        Gson gson = new Gson();
        try {
            lstQuestions = gson.fromJson(new FileReader(filePath), listType);
        }
        catch (FileNotFoundException e) { e.printStackTrace(); }
    } //deserializeQuestions


    private static void initialize() {
        assistant = WatsonAssistantModule.getInstance();
    } //initialize

}
