package com.example.physioflex.Excercises;

import com.example.physioflex.PhysioFlexData;
import com.example.physioflex.PhysioFlexTherapist;

public class ArmCurl implements IExercise {

    private int numRepsDone;
    private int numRepsRequired;

    private float highThreshold = 45.0f;
    private float lowThreshold = -40.0f;

    private static enum ARM_STATE
    {
        FINISHED_REP,
        FIRST_HALF,
        TOP,
        SECOND_HALF,
//        THIRD_QUARTER,
//        FOURTH_QUARTER
    }

    private ARM_STATE currentState;

    public ArmCurl(int numRepsRequired) {
        this.numRepsRequired = numRepsRequired;
        this.currentState = ARM_STATE.FINISHED_REP;
        numRepsDone = 0;
    }

    public void reset() {
        numRepsDone = 0;
    }

    @Override
    public PhysioFlexTherapist.TherapistThought analyzeNext(PhysioFlexData flexData, int repStart, int currentTimePoint) {
        float currentAngle = flexData.gyroPitch.get(currentTimePoint);

        if ( currentAngle > highThreshold + 5 || currentAngle < lowThreshold - 5){
            return PhysioFlexTherapist.TherapistThought.BAD;
        }

        switch (currentState) {
            case FINISHED_REP:
                if (currentAngle > lowThreshold) {
                    currentState = ARM_STATE.FIRST_HALF;
                }
                break;
            case FIRST_HALF:
                if (currentAngle > highThreshold) {
                    currentState = ARM_STATE.TOP;
                }
                break;
            case TOP:
                if (currentAngle < highThreshold) {
                    currentState = ARM_STATE.SECOND_HALF;
                }
                break;
            case SECOND_HALF:
                if (currentAngle < lowThreshold) {
                    currentState = ARM_STATE.FINISHED_REP;
                    numRepsDone += 1;
//                    if (numRepsDone >= numRepsRequired ) {
//                        return PhysioFlexTherapist.TherapistThought.END_OF_EXERCISE;
//                    }
                    return PhysioFlexTherapist.TherapistThought.END_OF_REP;
                }
                break;
        }

        return PhysioFlexTherapist.TherapistThought.OK;
    }

    public int getNumDone() {
        return numRepsDone;
    }
}
