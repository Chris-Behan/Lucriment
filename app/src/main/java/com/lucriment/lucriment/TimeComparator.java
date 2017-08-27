package com.lucriment.lucriment;

import java.util.Comparator;



/**
 * Created by ChrisBehan on 8/27/2017.
 */

public class TimeComparator implements Comparator<TimeInterval> {


    @Override
    public int compare(TimeInterval o1, TimeInterval o2) {
        return (int) (o1.getFrom()-o2.getFrom());
    }

}
