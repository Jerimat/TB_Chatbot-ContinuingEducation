package hesge.legrand.tb.chatbot.conversation;

import com.ibm.watson.assistant.v2.model.DialogNodeAction;
import hesge.legrand.tb.chatbot.conversation.model.WatsonAssistantModule;
import hesge.legrand.tb.education.Initializer;
import hesge.legrand.tb.education.model.Question;
import hesge.legrand.tb.education.model.Theme;

import java.io.Console;
import java.util.List;
import java.util.Random;
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
        } else {
            System.out.println("Pas de console détectée");
        }
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
                    startQuestionning();
                }
            }
            /*  next round of input   */
            System.out.print(USER_TALK);
            inputText = console.readLine();
        } while (!currentAction.equals(ACTION_END_CONVERSATION)); //NullPointerException here

        /*  Delete session when done */
        assistant.endInteraction();
    } //interactWithAssistant

    private void computeAction(DialogNodeAction currentAction) {
        String requestedAction = currentAction.getName();
        switch (requestedAction) {
            case ACTION_FILTER_QUESTIONS :
                String requestedTheme = currentAction.getParameters().get(ACTION_FILTER_QUESTIONS_PARAMETER).toString();
                console.printf("filtering questions by " + requestedTheme);
                Theme theme = Theme.valueOf(requestedTheme);
                filterQuestions(theme);
                break;
            case ACTION_END_CONVERSATION :
                break;
        }
    } //computeAction

    private void filterQuestions(Theme theme) {
        lstQuestions = Initializer.getInstance().filterQuestions(theme);
    } //filterQuestions

    private void startQuestionning() {
        boolean isStop = false;
        Question currentQuestion;
        String userAnswer;
        Random random = new Random();

        while (!lstQuestions.isEmpty() | !isStop) {
            int remainingQuestions = lstQuestions.size();
            if (remainingQuestions > 0) {
                if (remainingQuestions > 2) {
                    currentQuestion = lstQuestions.get(random.nextInt(remainingQuestions - 1));
                } else {
                    currentQuestion = lstQuestions.get(0);
                }
                /*  EducativeChatbot asks a question */
                console.printf(currentQuestion.getQuestioning());

                /*  User is asked to answer */
                userAnswer = console.readLine();
                isStop = assistant.isStop(userAnswer);
                //TODO: send userAnswer to be analyzed by NLU
            }
            console.printf("Je n'ai plus de question à te poser sur ce thème");
        }
    } //startQuestionning

}
