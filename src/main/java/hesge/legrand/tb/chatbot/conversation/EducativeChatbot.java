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

/**
 * The core class of the application.
 * This class uses the Singleton to avoid multiplying calls to the Assistant and NLU APIs
 *
 * It gets the instance from the WatsonAssistantModule to manage the interaction
 */
public class EducativeChatbot {

    private static EducativeChatbot instance;
    private WatsonAssistantModule assistant;
    private List<Question> lstQuestions;
    private Scanner scanner;

    public static EducativeChatbot getInstance() {
        if (instance == null) {
            instance = new EducativeChatbot();
        }
        return instance;
    } //getInstance

    private EducativeChatbot() {
        this.assistant = WatsonAssistantModule.getInstance();
        scanner = new Scanner(System.in);
        if (scanner != null) {
            interactWithAssistant();
        }
        else { System.out.println("Pas de console détectée \n"); }
    }

    /**
     * While the user doesn't want to exit conversation, the conversation continues (proceed)
     * Send the utterance from the user to the WatsonAssistantModule to determine the next action to be executed
     *
     * When it gets the response from WatsonAssistantModule,
     * if there is any action requested from the chatbot, it computes the action
     */
    private void interactWithAssistant() {
        LogManager.getLogManager().reset();

        boolean proceed;
        String inputText = "";
        DialogNodeAction currentAction;

        do {
            currentAction = assistant.answerUtterance(inputText);
            if (currentAction != null) {
                computeAction(currentAction);
            }
            proceed = proceed(currentAction);
            /*  next round of input   */
            if (proceed) {
                System.out.print(USER_TALK);
                inputText = scanner.nextLine();
            }
        } while (proceed);
    } //interactWithAssistant

    /**
     * Ensure that the user wants to continues the interaction
     * @param currentAction : action requested by the Watson Assistant API
     *                      the currentAction can either be null if no action has been requested from the Assistant API,
     *                      either not null.
     * @return Assistant API detected #Exit intent ? false : true;
     */
    private boolean proceed(DialogNodeAction currentAction) {
        if (currentAction == null || !currentAction.getName().equals(ACTION_END_CONVERSATION)) {
            return true;
        } else {
            return false;
        }
    } //proceed

    /**
     * Determines the next action to be executed based on the action requested by the Watson Assistant API
     * @param currentAction
     */
    private void computeAction(DialogNodeAction currentAction) {
        String requestedAction = currentAction.getName();
        switch (requestedAction) {
            case ACTION_FILTER_QUESTIONS:
                String requestedTheme = currentAction.getParameters().get(ACTION_FILTER_QUESTIONS_PARAMETER).toString();
                Theme theme = Theme.valueOf(requestedTheme.toUpperCase());
                startQuestioning(theme);
                break;
            case ACTION_END_CONVERSATION:
                /*  Delete session when done */
                assistant.endInteraction();
                scanner.close();
                break;
        }
    } //computeAction

    /**
     * The EducativeChatbot filters the questions by the given Theme
     * and then starts asking the user questions about the theme he choosed
     * @param theme : Theme the user chose the questions to be about
     */
    private void startQuestioning(Theme theme) {
        lstQuestions = Initializer.getInstance().filterQuestions(theme);

        int interactionState = CODE_QUESTIONS_PROCEED;
        Question currentQuestion;
        String userAnswer;
        Random random = new Random();

        while (!lstQuestions.isEmpty() && interactionState == CODE_QUESTIONS_PROCEED) {
            int remainingQuestions = lstQuestions.size();
            System.out.println("Nombre de questions restantes : " + remainingQuestions);
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
                    userAnswer = promptUserAnswer(currentQuestion);
                    assistant.assertAnswer(userAnswer, currentQuestion);
                    interactionState = CODE_QUESTIONS_PROCEED;
                }
                lstQuestions.remove(currentQuestion);
                if (lstQuestions.isEmpty()) {
                    System.out.println(CHATBOT_TALK + "Je n'ai plus de question à te poser sur ce theme");
                }
            }
        } //while (!lstQuestions.isEmpty() && interactionState == CODE_QUESTIONS_PROCEED)
    } //startQuestioning

    /**
     * Ask the user the given question and prompt him to answer
     * @param question
     * @return the user's answer to the given question
     */
    private String promptUserAnswer(Question question) {
        String userAnswer;

        System.out.println(CHATBOT_TALK + question.getQuestioning());
        System.out.print(USER_TALK);
        userAnswer = scanner.nextLine();

        return userAnswer;
    } //promptUserAnswer

}
