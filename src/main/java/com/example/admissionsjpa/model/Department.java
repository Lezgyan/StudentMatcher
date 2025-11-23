package com.example.admissionsjpa.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class Department {

    private final String id;
    private final String name;
    private final int quota;
    private final String faculty;
    private final String groupTitle;
    private final boolean isDefaultFaculty;

    private final List<Student> assignedStudents = new ArrayList<>();

    public Department(String defaultPlaceholder, int quota, boolean isDefaultFaculty) {
        this(defaultPlaceholder, defaultPlaceholder, quota, defaultPlaceholder, defaultPlaceholder, isDefaultFaculty);
    }

    public void addStudent(Student s) {
        assignedStudents.add(s);
    }

    public boolean isGreaterThanQuota() {
        return assignedStudents.size() > quota;
    }

    public void sortStudents() {
        if (isDefaultFaculty)
            return;
        Comparator<Student> cmp = Comparator
                .comparingInt((Student s) -> s.getScore(this).getTotalScore()).reversed()
                .thenComparingInt(s -> s.getScore(this).getFirstPriority()).reversed()
                .thenComparingInt(s -> s.getScore(this).getSecondPriority()).reversed()
                .thenComparingInt(s -> s.getScore(this).getAdditionalPoints()).reversed()
                .thenComparing(Student::getSnils);
        assignedStudents.sort(cmp);
    }

    public List<Student> rejectOverflow() {
        if (isDefaultFaculty)
            return List.of();
        if (assignedStudents.size() <= quota)
            return List.of();

        List<Student> rejected = new ArrayList<>(assignedStudents.subList(quota, assignedStudents.size()));

        assignedStudents.subList(quota, assignedStudents.size()).clear();

        return rejected;
    }
}
