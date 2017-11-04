package com.lucriment.lucriment;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Comparator;

/**
 * Created by christopher.behan on 2017-10-27.
 */

public class ChatTimeComparator implements Comparator<Chat> {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int compare(Chat o1, Chat o2) {
        return Long.compare(o2.timestamp,o1.timestamp);
    }


}

