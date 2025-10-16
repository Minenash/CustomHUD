package com.minenash.customhud.data;

import java.util.ArrayList;
import java.util.List;

public class StopWatch {

    private int time = 0;
    private long startTime = 0;
    private boolean running = false;
    private final List<Integer> laps = new ArrayList<>();

    public int query() {
        return running ? time + (int)(System.currentTimeMillis() - startTime) : time;
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {
        if (!running) {
            startTime = System.currentTimeMillis();
            running = true;
        }
    }

    public void restart() {
        startTime = System.currentTimeMillis();
        time = 0;
        running = true;
        laps.clear();
    }

    public void reset() {
        startTime = time = 0;
        running = false;
        laps.clear();
    }

    public void pause() {
        if (running) {
            time += (int) (System.currentTimeMillis() - startTime);
            running = false;
        }
    }

    public void lap() {
        laps.add(running ? time + (int)(System.currentTimeMillis() - startTime) : time);
    }

    public List<Integer> laps() {
        return laps;
    }

}
