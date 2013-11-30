package com.example.physioflex;

import com.example.physioflex.Excercises.IExercise;

/**
 * Created with IntelliJ IDEA.
 */
public class PhysioFlexTherapist {
    private PhysioFlexData physioData;
    private int repStart;
    private int currentTimePoint;

    public static enum TherapistThought {
        OK,
        BAD,
        END_OF_REP,
        END_OF_EXERCISE
    }

    public PhysioFlexTherapist() {
        physioData = new PhysioFlexData();

        repStart = 0;
        currentTimePoint = 0;
    }

    public void readNext(String data) {
        float angle = parseData(data);
        physioData.gyroRoll.add(angle);
    }

    public float parseData(String data) {
        return Float.parseFloat(data);
    }

    public TherapistThought considerData (IExercise exercise){
        TherapistThought thought = exercise.analyzeNext(physioData, repStart, currentTimePoint);
        if (thought == TherapistThought.END_OF_REP) {
            repStart = currentTimePoint + 1;
        }

        currentTimePoint += 1;

        return thought;
    }
}
