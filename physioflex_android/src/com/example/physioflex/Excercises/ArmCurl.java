package com.example.physioflex.Excercises;

import com.example.physioflex.PhysioFlexData;
import com.example.physioflex.PhysioFlexTherapist;

public class ArmCurl implements IExercise {

    private int numRepsDone;
    private int numRepsRequired;

    private float highThreshold = 80.0f;
    private float lowThreshold = 35.0f;

    private static enum ARM_STATE
    {
        FINISHED_REP,
        FIRST_QUARTER,
        SECOND_QUARTER,
        THIRD_QUARTER,
        FOURTH_QUARTER
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
                    currentState = ARM_STATE.FIRST_QUARTER;
                }
                break;
            case FIRST_QUARTER:
                if (currentAngle > highThreshold) {
                    currentState = ARM_STATE.SECOND_QUARTER;
                }
                break;
            case SECOND_QUARTER:
                if (currentAngle < lowThreshold) {
                    currentState = ARM_STATE.THIRD_QUARTER;
                }
                break;
            case THIRD_QUARTER:
                if (currentAngle > highThreshold) {
                    currentState = ARM_STATE.FOURTH_QUARTER;
                }
                break;
            case FOURTH_QUARTER:
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
