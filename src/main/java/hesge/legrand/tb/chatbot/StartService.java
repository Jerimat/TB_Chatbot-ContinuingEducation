package hesge.legrand.tb.chatbot;

import hesge.legrand.tb.chatbot.conversation.EducativeChatbot;
import hesge.legrand.tb.education.Initializer;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.LogManager;


/**
 * Main class to be launched to start EducativeChatbot
 * To launch the application, you need to specify the path of the .csv file containing the questions you want the bot to use.
 * You also need to provide a destination path for a .json file to be created.
 *
 * This file will contain a json representation of the questions with their attributes
 * so that you can later modify the implementation of this class to economize NLU API calls and reuse the resulting NLU analyzis from the .json file
 */
public class StartService {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Path du fichier .csv contenant les questions à intégrer au chatbot : ");
        String inputFilePath = scanner.nextLine();

        System.out.print("Path de destination du fichier .json contenant les questions et leurs attributs : ");
        String outputFilePath = scanner.nextLine();
        try {
            Initializer.getInstance().initialize(inputFilePath, outputFilePath);
            EducativeChatbot.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Problem occured with initialisation");
            scanner.close();
        }
        scanner.close();
    } //main

}
