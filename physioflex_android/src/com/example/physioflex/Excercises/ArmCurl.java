package com.example.physioflex.Excercises;

import com.example.physioflex.PhysioFlexData;
import com.example.physioflex.PhysioFlexTherapist;

public class ArmCurl implements IExercise {

    private int numRepsDone;
    private int numRepsRequired;

    public ArmCurl(int numRepsRequired) {
        this.numRepsRequired = numRepsRequired;
        numRepsDone = 0;
    }

    @Override
    public PhysioFlexTherapist.TherapistThought analyzeNext(PhysioFlexData flexData, int repStart, int currentTimePoint) {
        if ((flexData.gyroRoll.get(currentTimePoint).intValue() % 9 == 0)) {
            return PhysioFlexTherapist.TherapistThought.BAD;
        }

        if (currentTimePoint - repStart > 10) {
            numRepsDone += 1;
            if (numRepsDone >= numRepsRequired ) {
                return PhysioFlexTherapist.TherapistThought.END_OF_EXERCISE;
            }
            return PhysioFlexTherapist.TherapistThought.END_OF_REP;
        }

        return PhysioFlexTherapist.TherapistThought.OK;
    }

    public int getNumDone() {
        return numRepsDone;
    }
}
