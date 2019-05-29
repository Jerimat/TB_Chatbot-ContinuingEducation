package hesge.legrand.tb.chatbot.conversation;

import com.ibm.watson.assistant.v2.model.DialogNodeAction;
import hesge.legrand.tb.chatbot.conversation.model.WatsonAssistantModule;
import hesge.legrand.tb.education.Initializer;
import hesge.legrand.tb.education.model.Question;
import hesge.legrand.tb.education.model.Theme;

import java.io.Console;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.LogManager;

import static hesge.legrand.tb.chatbot.helper.Constants.*;

public class EducativeChatbot {

    private static EducativeChatbot instance;
    private WatsonAssistantModule assistant;
    private List<Question> lstQuestions;
    private Scanner scanner;
//    private Console console;

    public static EducativeChatbot getInstance() {
        if (instance == null) {
            instance = new EducativeChatbot();
        }
        return instance;
    } //getInstance

    private EducativeChatbot() {
        this.assistant = WatsonAssistantModule.getInstance();
//        console = System.console();
        this.scanner = new Scanner(System.in);
        if (scanner != null) {
            interactWithAssistant();
        }
        else { System.out.println("Pas de scanner détecté"); }
    }

    private void interactWithAssistant() {
        LogManager.getLogManager().reset();

        String inputText = "";
        DialogNodeAction currentAction;
        do {
            currentAction = assistant.answerUtterance(inputText);
            if (currentAction != null) {
                computeAction(currentAction);
                if (currentAction.getName().equalsIgnoreCase(ACTION_FILTER_QUESTIONS)) {
                    startQuestioning();
                }
            }
            /*  next round of input   */
            System.out.print(USER_TALK);
            inputText = scanner.nextLine();
//            inputText = console.readLine();
        } while (currentAction == null || !currentAction.getName().equals(ACTION_END_CONVERSATION)); //TODO: pouvoir quitter la conversation

        /*  Delete session when done */
        assistant.endInteraction();
    } //interactWithAssistant

    private void computeAction(DialogNodeAction currentAction) {
        String requestedAction = currentAction.getName();
        switch (requestedAction) {
            case ACTION_FILTER_QUESTIONS :
                String requestedTheme = currentAction.getParameters().get(ACTION_FILTER_QUESTIONS_PARAMETER).toString();
                System.out.println("filtering questions by " + requestedTheme);
//                console.printf("filtering questions by " + requestedTheme);
                Theme theme = Theme.valueOf(requestedTheme.toUpperCase());
                filterQuestions(theme);
                break;
            case ACTION_END_CONVERSATION :
                break;
        }
    } //computeAction

    private void filterQuestions(Theme theme) {
        lstQuestions = Initializer.getInstance().filterQuestions(theme);
    } //filterQuestions

    private void startQuestioning() {
        boolean isStop = false;
        Question currentQuestion;
        String userAnswer;
        Random random = new Random();

        while (!lstQuestions.isEmpty() || !isStop) {
            int remainingQuestions = lstQuestions.size();
            System.out.println("Nombre de questions restantes : " + remainingQuestions);
            if (remainingQuestions > 0) {
                if (remainingQuestions > 2) {
                    currentQuestion = lstQuestions.get(random.nextInt(remainingQuestions - 1));
                } else {
                    currentQuestion = lstQuestions.get(0);
                }
                /*  EducativeChatbot asks a question */
                System.out.println(CHATBOT_TALK + currentQuestion.getQuestioning());
//                console.printf(currentQuestion.getQuestioning());

                /*  User is asked to answer */
                System.out.print(USER_TALK);
                userAnswer = scanner.nextLine();
//                userAnswer = console.readLine();
                isStop = assistant.isStop(userAnswer);
                //TODO: send userAnswer to be analyzed by NLU
                lstQuestions.remove(currentQuestion);
            }
            System.out.println(CHATBOT_TALK + "Je n'ai plus de question à te poser sur ce theme");
//            console.printf("Je n'ai plus de question à te poser sur ce theme");
        }
    } //startQuestioning

}
