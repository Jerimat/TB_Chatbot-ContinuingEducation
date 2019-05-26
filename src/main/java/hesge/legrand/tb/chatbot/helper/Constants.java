package hesge.legrand.tb.chatbot.helper;

public final class Constants {

    public static final String CATEGORIES_ID = "Categories"; //This ID is set to retrieve the List of analyzed categories in a Map
    public static final String CONCEPTS_ID = "Concepts"; //This ID is set to retrieve the List of analyzed concepts in a Map
    public static final String DATA_SEPARATOR = ","; //Relevant informations for instantiating a new Question in the input .csv file are separated by this char

    public static final String ACTION_FILTER_QUESTIONS = "filter_questions";
    public static final String ACTION_FILTER_QUESTIONS_PARAMETER = "theme";
    public static final String ACTION_END_QUESTIONS = "stop_questions";
    public static final String ACTION_END_CONVERSATION = "end_conversation";

    public static final String CHATBOT_TALK = "Mr.Watson: ";
    public static final String USER_TALK = ">> ";

}
