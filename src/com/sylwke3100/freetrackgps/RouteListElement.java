package com.sylwke3100.freetrackgps;

import java.text.SimpleDateFormat;
import java.util.HashMap;



public class RouteListElement {
    private static final SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy HH:mm ");
    public int id;
    public long startTime;
    public double distance;
    public String name = "";

    public RouteListElement(int id, long startTime, double distance) {
        this.id = id;
        this.startTime = startTime;
        this.distance = distance;
    }

    public RouteListElement(int id, long startTime, double distance, String name) {
        this.id = id;
        this.startTime = startTime;
        this.distance = distance;
        this.name = name;
    }

    public String getPreparedName() {
        if (name == null)
            return "";
        else if (name.isEmpty())
            return "";
        else
            return " - " + name;
    }

    public HashMap<String, String> getPreparedHashMapToView() {
        HashMap<String, String> singleWorkout = new HashMap<String, String>();
        singleWorkout.put("time", formatDate.format(startTime) + getPreparedName());
        singleWorkout.put("distance", String.format("%.2f km", distance / 1000));
        return singleWorkout;
    }
}
