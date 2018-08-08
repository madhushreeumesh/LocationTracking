package com.squareandcube.locationtracking;

public class TrackConfig {
    public static String FLAG="It the start";

    public static String[] taskNo;
    public static String[] empId;
    public static String[] deslat;
    public static String[] desLong;
    public static String[] desAddress;
    public static String[] curLat;
    public static String[] curLong;
    public static String[] curAddress;

    public TrackConfig(int i) {

        taskNo = new String[i];
        empId = new String[i];
        deslat = new String[i];
        desLong = new String[i];
        desAddress = new String[i];
        curLat = new String[i];
        curLong = new String[i];
        curAddress = new String[i];
    }
}
