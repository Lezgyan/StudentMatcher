package com.example.admissionsjpa.model;

import lombok.Getter;

@Getter
public class Scores {
    private final String idGroup;
    private final int firstPriority;
    private final int secondPriority;
    private final int thirdPriority;
    private final int additionalPoints;
    private final int totalScore;

    public Scores(String idGroup, int firstPriority, int secondPriority, int thirdPriority, int additionalPoints) {
        this.idGroup = idGroup;
        this.firstPriority = firstPriority;
        this.secondPriority = secondPriority;
        this.thirdPriority = thirdPriority;
        this.additionalPoints = additionalPoints;
        this.totalScore = firstPriority + secondPriority + thirdPriority + additionalPoints;
    }
}
