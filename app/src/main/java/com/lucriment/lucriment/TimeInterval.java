package com.lucriment.lucriment;

/**
 * Created by ChrisBehan on 5/30/2017.
 */

public class TimeInterval {
    private long from;
    private long to;

    public TimeInterval(){

    }

    public TimeInterval(long from, long to) {
        this.from = from;
        this.to = to;
    }

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }
}
