package hesge.legrand.tb.chatbot;

import hesge.legrand.tb.chatbot.conversation.EducativeChatbot;
import hesge.legrand.tb.education.Initializer;

import java.io.IOException;

public class StartService {

    public static void main(String[] args) {
        if (args.length == 1) {
            String inputFilePath = args[0];
//            String outputFilePath = args[1];

            try {
                Initializer.getInstance().initialize(inputFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                System.console().printf("Problem occured with initialisation");
                return;
            }
            EducativeChatbot.getInstance();
        }
        else {
            System.console().printf("You should provide 2 parameters! Path of the input .csv file and path of the output .json file");
            System.console().printf("1st parameter corresponds to the path of the input file, 2nd parameter corresponds to the path of the output file.");
        }
    } //main

}
