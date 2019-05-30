package hesge.legrand.tb.chatbot;

import hesge.legrand.tb.chatbot.conversation.EducativeChatbot;
import hesge.legrand.tb.education.Initializer;
import io.reactivex.annotations.Experimental;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import static hesge.legrand.tb.chatbot.helper.Constants.USER_TALK;

public class StartService {

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 2) {
            String inputFilePath = args[0];
            String outputFilePath = args[1];

            try {
                Initializer.getInstance().initialize(inputFilePath, outputFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                System.console().printf("Problem occured with initialisation \n");
                return;
            }
                EducativeChatbot.getInstance();
        }
        else {
            System.console().printf("You should provide 2 parameters! Path of the input .csv file and path of the output .json file \n");
            System.console().printf("1st parameter corresponds to the path of the input file, 2nd parameter corresponds to the path of the output file. \n");
        }
    } //main

}
