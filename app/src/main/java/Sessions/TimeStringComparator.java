package Sessions;

import java.util.Comparator;

/**
 * Created by ChrisBehan on 8/28/2017.
 */

public class TimeStringComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        int one = hoursAndMinutesToMinutes(o1);
        int two = hoursAndMinutesToMinutes(o2);
        return one-two;
    }

    private int hoursAndMinutesToMinutes(String timeString){
        int value1 = Integer.valueOf(timeString.substring(0,timeString.indexOf(':')));
        int value2 = Integer.valueOf(timeString.substring(timeString.indexOf(':')+1,timeString.length()));
        value1 = value1*60;
        return value1+value2;
    }
}
