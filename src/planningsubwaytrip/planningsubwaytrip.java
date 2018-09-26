package planningsubwaytrip;

import planningsubwaytrip.console.presentationlayer.Console;
import planningsubwaytrip.model.Pair;
import planningsubwaytrip.model.Station;

import java.util.List;



/*Planning planningsubwaytrip trip app
 *
 * authors: mcirja & sscosta
 * */

public class planningsubwaytrip {

    public static void main(String[] args) {


        //To execute the app, the command 'java planningsubwaytrip linhas.txt estaLisboa.txt' has to be executed.

        Loader l = new Loader(args);
        try {
            (new Console()).run();
        } catch (Exception e) {
            System.out.println("Sorry something silly happened!!!");
            System.out.println("Cause: " + e.getMessage());
            System.out.println("Program aborted.");
            e.printStackTrace();
        }

    }





}
