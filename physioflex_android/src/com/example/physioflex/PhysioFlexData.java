package com.example.physioflex;

import java.util.ArrayList;

/**
 * User: dmitriy
 * Date: 11/29/13
 * Time: 5:31 PM
 */
public class PhysioFlexData {

    public ArrayList<Float> gyroRoll;
    public ArrayList<Float> gyroYaw;
    public ArrayList<Float> gyroPitch;

    public ArrayList<Float> accelX;
    public ArrayList<Float> accelY;
    public ArrayList<Float> accelZ;

    public PhysioFlexData() {
        gyroPitch = new ArrayList<Float>();
        gyroRoll = new ArrayList<Float>();
        gyroYaw = new ArrayList<Float>();

        accelX = new ArrayList<Float>();
        accelY = new ArrayList<Float>();
        accelZ = new ArrayList<Float>();
    }



}
