package com.example.admissionsjpa.service;

import com.example.admissionsjpa.model.Department;
import com.example.admissionsjpa.model.Student;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class MatchStudentsService {

    private static final String OTHER = "OTHER";
    private static final Integer MAX_QUOTA = 1_000_000;

    public List<Department> match(List<Student> students, List<Department> departments) {
        Map<String, Department> deptMap = initDepartments(departments);

        List<Student> unassignedStudents = new ArrayList<>(students);
        while (!unassignedStudents.isEmpty()) {
            for (Student student : unassignedStudents) {
                Department department = deptMap.get(student.getPriority());
                department.addStudent(student);
                student.setAssignedDepartment(department);
            }
            for (Department department : deptMap.values()) {
                department.sortStudents();
                if (department.isGreaterThanQuota()) {
                    List<Student> rejectedStudents = department.rejectOverflow();
                    for (Student rejected : rejectedStudents) {
                        rejected.setAssignedDepartment(null);
                        rejected.incCurrentPos();
                    }
                }
            }
            unassignedStudents = deptMap.values().stream()
                    .flatMap(d -> d.getAssignedStudents().stream())
                    .filter(Student::isUnassigned)
                    .toList();
        }
        return new ArrayList<>(deptMap.values());
    }

    private Map<String, Department> initDepartments(List<Department> departments) {
        LinkedHashMap<String, Department> deptMap = new LinkedHashMap<>();
        for (Department d : departments)
            deptMap.put(d.getId(), d);

        if (departments.stream().noneMatch(d -> d.getName().equals(OTHER))) {
            deptMap.put(OTHER, new Department(OTHER, MAX_QUOTA, true));
        }
        return deptMap;
    }
}
