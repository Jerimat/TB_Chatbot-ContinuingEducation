package hesge.legrand.tb.education;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.cloud.sdk.core.service.model.GenericModel;
import com.ibm.watson.natural_language_understanding.v1.model.CategoriesResult;
import com.ibm.watson.natural_language_understanding.v1.model.ConceptsResult;
import hesge.legrand.tb.chatbot.Constants;
import hesge.legrand.tb.chatbot.conversation.model.NaturalLanguageUnderstandingModule;
import hesge.legrand.tb.education.model.Question;
import hesge.legrand.tb.education.model.Theme;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is used to transform a .csv file created by the course's instructors into a List of Question
 * The List of Question is then serialized to a .json file to be usable in the EducativeChatbot class.
 *
 * A Question is composed of a THEME (which must be present in education.model.Theme), a question, a typical correct answer and an optional hint
 * The typical correct answer is then being analyzed by our NaturalLanguageUnderstandingModule
 * to set the categories and concepts the user's answer must be about
 */
public class Initializer {

    public static void main(String[] args) throws IOException {
        if (args.length == 2) {
            String inputFilePath = args[0];
            String outputFilePath = args[1];

            List<Question> questions = initializeQuestions(inputFilePath);
            serializeQuestions(questions, outputFilePath);
        } else {
            System.console().printf("You should provide only 2 parameters! Path of the input .csv file and path of the output .json file");
            System.console().printf("1st parameter corresponds to the path of the input file, 2nd parameter corresponds to the path of the output file.");
        }
    } //main

    /**
     * Serialize the List of Question created from initializeQuestions
     * and write the result in the user's specified filePath output
     * @param questions
     * @param output : output filePath
     * @throws IOException
     */
    private static void serializeQuestions(List<Question> questions, String output) throws IOException {
        FileWriter jsonFileWriter = new FileWriter(output);
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();
        gson.toJson(questions, jsonFileWriter);

        jsonFileWriter.flush();
        jsonFileWriter.close();
    } //serializeQuestions

    /**
     * Initialize a List of Question from the user's .csv filePath input
     * @param input : path of the file containing THEME, question, typical correct answer
     * @return List of Questions found in input file
     * @throws IOException
     */
    private static <T extends GenericModel> List<Question> initializeQuestions(String input) throws IOException {
        List<Question> lstQuestions = new ArrayList<>();

        String row;
        BufferedReader csvReader = new BufferedReader(new FileReader(input));

        /*  Instantiation of our NaturalLanguageUnderstandingModule to be sure that the categories and concepts analyzed
        in the typical correct answer an the user's answer are the same to the ones analyzed when interacting with the chatbot */
        NaturalLanguageUnderstandingModule nlu = NaturalLanguageUnderstandingModule.getInstance();

        /*  Each row must be as specified in documentation : 'THEME, question, typical correct answer, hint[optional]'   */
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(Constants.DATA_SEPARATOR);

            try {
                Theme theme = Theme.valueOf(data[0]);
                String question = data[1];
                String answer = data[2];

                Map<String, List<T>> lstAnalysis = nlu.setRecognizable(answer);
                List<CategoriesResult> categories = (List<CategoriesResult>) lstAnalysis.get(Constants.CATEGORIES_ID);
                List<ConceptsResult> concepts = (List<ConceptsResult>) lstAnalysis.get(Constants.CONCEPTS_ID);

                if (data.length > 3) {
                    String hint = data[4];
                    lstQuestions.add(new Question(theme, question, answer, hint, categories, concepts));
                } else {
                    lstQuestions.add(new Question(theme, question, answer, categories, concepts));
                }
                System.out.println("question added");
            } catch (IllegalArgumentException e) {
                System.out.println("you have entered a wrong Theme identifier : " + data[0] + " is not a theme handled by the EducativeChatbot (case-sensitive)");
                e.printStackTrace();
            }
        }
        csvReader.close();

        return lstQuestions;
    } //initializeQuestions

}
