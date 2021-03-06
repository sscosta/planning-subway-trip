package planningsubwaytrip.console.presentationlayer;

import java.util.Scanner;

public class Utilities {

    public static  String GetString(Scanner input, String caption)
    {
        System.out.print(caption);
        return input.nextLine();
    }


    public static  boolean YesOrNoQuestion(Scanner input, String questionCaption)
    {
        String question = null;
        while(true)
        {
            question = GetString(input, questionCaption + "[(Y)es/(numberOfStations)o]:");
            if(question.toUpperCase().equals("Y"))
            {
                return true;
            }
            else if (question.toUpperCase().equals("numberOfStations"))
            {
                return false;
            }
        }
    }
}
