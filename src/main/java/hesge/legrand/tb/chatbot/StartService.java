package hesge.legrand.tb.chatbot;

import hesge.legrand.tb.chatbot.conversation.EducativeChatbot;
import hesge.legrand.tb.education.Initializer;
import io.reactivex.annotations.Experimental;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import static hesge.legrand.tb.chatbot.helper.Constants.USER_TALK;

public class StartService {

    @Experimental
    private static String inputFilePath = "C:/Users/mathi/Desktop/TB_Questions_MathieuLegrand.json";

    public static void main(String[] args) throws FileNotFoundException {
        System.out.print(USER_TALK);
//        String inputFilePath = new Scanner(System.in).nextLine();
        if (inputFilePath != null /*args.length == 1*/) {
//            String inputFilePath = args[0];

//            try {
//                Initializer.getInstance().initialize(inputFilePath);
                Initializer.getInstance().deserialize(inputFilePath);
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.out.println("Problem occurred with initialisation");
//                System.console().printf("Problem occured with initialisation");
//                return;
//            }
            EducativeChatbot.getInstance();
        }
        else {
            System.out.println("You should provide 2 parameters! Path of the input .csv file and path of the output .json file");
            System.out.println("1st parameter corresponds to the path of the input file, 2nd parameter corresponds to the path of the output file.");
//            System.console().printf("You should provide 2 parameters! Path of the input .csv file and path of the output .json file");
//            System.console().printf("1st parameter corresponds to the path of the input file, 2nd parameter corresponds to the path of the output file.");
        }
    } //main

}
