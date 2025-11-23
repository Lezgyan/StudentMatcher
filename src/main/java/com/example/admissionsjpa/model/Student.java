package com.example.admissionsjpa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Student {

    private static final String OTHER = "OTHER";

    private final String id;
    private final String snils;
    private final Map<String, Scores> scores = new HashMap<>();
    private final List<String> priorities = new ArrayList<>();

    private Department assignedDepartment;
    private int currentPos = 0;

    public Student(String id, String snils, Map<String, Scores> scores, Map<Integer, String> priorityToGroup, int maxPriority) {
        this.id = id;
        this.snils = snils;
        this.assignedDepartment = null;
        if (scores != null)
            this.scores.putAll(scores);
        if (priorityToGroup != null) {
            for (int i = 1; i <= maxPriority + 10; i++) {
                this.priorities.add(priorityToGroup.getOrDefault(i, OTHER));
            }
        }
        this.scores.put(OTHER, new Scores(OTHER, 0, 0, 0, 0));
    }

    public Scores getScore(Department d) {
        return scores.getOrDefault(d.getId(), scores.get(OTHER));
    }

    public String getPriority() {
        if (currentPos >= 0 && currentPos < priorities.size()) {
            return priorities.get(currentPos);
        } else {
            return OTHER;
        }
    }

    public boolean isUnassigned() {
        return assignedDepartment == null;
    }

    public void incCurrentPos() {
        this.currentPos++;
    }

    public Map<String, Object> toCsvMap() {
        Scores s = assignedDepartment != null ? getScore(assignedDepartment) : new Scores(OTHER, 0, 0, 0, 0);
        String idGroup = assignedDepartment != null ? assignedDepartment.getId() : OTHER;
        String groupTitle = assignedDepartment != null ? assignedDepartment.getName() : OTHER;
        String faculty = assignedDepartment != null ? assignedDepartment.getFaculty() : OTHER;

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("name", snils);
        m.put("id_group", idGroup);
        m.put("group_priority", currentPos + 1);
        m.put("group_title", groupTitle);
        m.put("faculty", faculty);
        m.put("first_priority", s.getFirstPriority());
        m.put("second_priority", s.getSecondPriority());
        m.put("third_priority", s.getThirdPriority());
        m.put("additional_points", s.getAdditionalPoints());
        return m;
    }
}
