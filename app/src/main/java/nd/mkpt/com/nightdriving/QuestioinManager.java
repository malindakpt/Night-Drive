package nd.mkpt.com.nightdriving;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MalindaK on 12/3/2017.
 */

public class QuestioinManager {
    public static int count = 0;
    public static int countA = 0;
    public static int countR = 0;
    public static  List<String> content, contentA, contentR;


    public static  void loadQues() {
        content = new ArrayList<String>();
        content.add("What is your name");
        content.add("Where are You going now");

        contentA = new ArrayList<String>();
        contentA.add("Good");
        contentA.add("Thank You");
        contentA.add("Ok");
        contentA.add("Fine");
        contentA.add("Wow");
        contentA.add("Nice");

        contentR = new ArrayList<String>();
        contentR.add("I did not get you");
        contentR.add("Please tell again");
        contentR.add("Can you repeat it");


    }
    public static String getQuestion() {
        if(count == content.size()){
            count = 0;
        }
        return content.get(count++);
    }

    public static String getAnswer() {
        if(countA == contentA.size()){
            countA = 0;
        }
        return contentA.get(countA++);
    }

    public static String getRepeat() {
        if(countR == contentR.size()){
            countR = 0;
        }
        return contentR.get(countR++);
    }
}



