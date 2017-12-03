package nd.mkpt.com.nightdriving;

/**
 * Created by MalindaK on 12/3/2017.
 */

public class QuestioinManager {
    public static int count = 0;
    public static String getQuestion() {
        return "Quiz   "+count++;
    }
}
