package com.example.physioflex.Excercises;


import com.example.physioflex.PhysioFlexData;
import com.example.physioflex.PhysioFlexTherapist;

public interface IExercise {
    public PhysioFlexTherapist.TherapistThought analyzeNext(PhysioFlexData flexData, int repStart, int currentTimePoint);
}
