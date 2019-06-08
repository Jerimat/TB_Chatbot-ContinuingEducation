package hesge.legrand.tb.chatbot.helper;

public final class Constants {

    public static final String CATEGORIES_ID = "Categories"; //This ID is set to retrieve the List of analyzed categories in a Map
    public static final String CONCEPTS_ID = "Concepts"; //This ID is set to retrieve the List of analyzed concepts in a Map
    public static final String DATA_SEPARATOR = ","; //Relevant informations for instantiating a new Question in the input .csv file are separated by this char

    public static final String ACTION_FILTER_QUESTIONS = "filter_questions";
    public static final String ACTION_FILTER_QUESTIONS_PARAMETER = "theme";
    public static final String ACTION_HELP_QUESTION = "find_hint";
    public static final String ACTION_END_QUESTIONS = "stop_questions";
    public static final String ACTION_END_CONVERSATION = "end_conversation";

    public static final String CHATBOT_TALK = "Mr.Watson: ";
    public static final String USER_TALK = ">> ";

    public static final int CODE_QUESTIONS_END = -1;
    public static final int CODE_QUESTIONS_PROCEED = 0;
    public static final int CODE_QUESTIONS_HELP = 1;

    /*  Feedback correctness percentage  */
    public static final double UNGRADABLE_ANSWER_LIMIT = 150;
    public static final double PERFECT_ANSWER_LIMIT = 0.85;
    public static final double GOOD_ANSWER_LIMIT = 0.75;
    public static final double ACCEPTABLE_ANSWER_LIMIT = 0.50;

    /* Feedbacks */
    public static final String FEEDBACK_PERFECT_ANSWER = "Parfait!";
    public static final String FEEDBACK_GOOD_ANSWER = "Bien joué!";
    public static final String FEEDBACK_ACCEPTABLE_ANSWER = "Pas mal, mais pas parfait";
    public static final String FEEDBACK_BAD_ANSWER = "Je pense que tu as besoin de revoir encore ce sujet";
    public static final String FEEDBACK_UNGRADABLE = "Je n'ai pas assez de données pour te donner de feedback";
    public static final String FEEDBACK_ANSWER_NOT_VALID = "Essaye de répondre avec une explication de 5 mots à 2 phrases";
    public static final String FORMATIVE_FEEDBACK = "Une réponse presque parfaite aurait été par exemple...";


}
