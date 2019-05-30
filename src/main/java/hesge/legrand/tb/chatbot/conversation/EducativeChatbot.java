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
    private Console console;

    public static EducativeChatbot getInstance() {
        if (instance == null) {
            instance = new EducativeChatbot();
        }
        return instance;
    } //getInstance

    private EducativeChatbot() {
        this.assistant = WatsonAssistantModule.getInstance();
        console = System.console();
        if (console != null) {
            interactWithAssistant();
        }
        else { System.out.println("Pas de console détectée \n"); }
    }

    private void interactWithAssistant() {
        LogManager.getLogManager().reset();

        boolean proceed;
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
            proceed = proceed(currentAction);
            /*  next round of input   */
            if (proceed) {
                console.printf(USER_TALK);
                inputText = console.readLine();
            }
        } while (proceed);

        /*  Delete session when done */
        assistant.endInteraction();
    } //interactWithAssistant

    private boolean proceed(DialogNodeAction currentAction) {
        if (currentAction == null || !currentAction.getName().equals(ACTION_END_CONVERSATION)) {
            return true;
        } else {
            return false;
        }
    } //proceed

    private void computeAction(DialogNodeAction currentAction) {
        String requestedAction = currentAction.getName();
        switch (requestedAction) {
            case ACTION_FILTER_QUESTIONS:
                String requestedTheme = currentAction.getParameters().get(ACTION_FILTER_QUESTIONS_PARAMETER).toString();
                console.printf("filtering questions by " + requestedTheme + "\n");
                Theme theme = Theme.valueOf(requestedTheme.toUpperCase());
                filterQuestions(theme);
                break;
            case ACTION_END_CONVERSATION:
                break;
        }
    } //computeAction

    private void filterQuestions(Theme theme) {
        lstQuestions = Initializer.getInstance().filterQuestions(theme);
    } //filterQuestions

    private void startQuestioning() {
        int interactionState = CODE_QUESTIONS_PROCEED;
        Question currentQuestion;
        String userAnswer;
        Random random = new Random();

        while (!lstQuestions.isEmpty() && interactionState == CODE_QUESTIONS_PROCEED) {
            int remainingQuestions = lstQuestions.size();
            console.printf("Nombre de questions restantes : " + remainingQuestions + "\n");
            if (remainingQuestions > 0) {
                if (remainingQuestions >= 2) {
                    currentQuestion = lstQuestions.get(random.nextInt(remainingQuestions - 1));
                } else {
                    currentQuestion = lstQuestions.get(0);
                }
                /*  Prompt user to answer given question    */
                userAnswer = promptUserAnswer(currentQuestion);

                interactionState = assistant.interactionState(userAnswer);
                if (interactionState == CODE_QUESTIONS_PROCEED) {
                    assistant.assertAnswer(userAnswer, currentQuestion);
                } else if (interactionState == CODE_QUESTIONS_HELP) {
                    promptUserAnswer(currentQuestion);
                }
                lstQuestions.remove(currentQuestion);
            }
        }
        if (lstQuestions.isEmpty()) {
            console.printf(CHATBOT_TALK + "Je n'ai plus de question à te poser sur ce theme \n");
        }
    } //startQuestioning

    private String promptUserAnswer(Question question) {
        String userAnswer;

        console.printf(CHATBOT_TALK + question.getQuestioning() + "\n");
        console.printf(USER_TALK);
        userAnswer = console.readLine();

        return userAnswer;
    } //promptUserAnswer

}
