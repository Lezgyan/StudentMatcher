package com.example.admissionsjpa.service.data;

import com.example.admissionsjpa.model.Department;
import com.example.admissionsjpa.model.Scores;
import com.example.admissionsjpa.model.Student;
import com.example.admissionsjpa.model.raw.DepartmentRow;
import com.example.admissionsjpa.model.raw.StudentRow;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class DataReader {

    private final DataAccessor dataAccessor;

    private static final Map<Integer, String> FORM_TO_NAME = Map.of(
            0, "ОЧНАЯ",
            1, "ОЧНО_ЗАОЧНАЯ",
            2, "ЗАОЧНАЯ"
    );

    public List<Department> getDepartments() {
        List<DepartmentRow> departments = dataAccessor.readDepartments();
        List<Department> result = new ArrayList<>();
        for (DepartmentRow department : departments) {
            String idGroup = department.idGroup();
            String finalName = createGroupName(idGroup, department);
            String facTitle = department.facultyTitle().replace(" ", "_");
            result.add(
                    Department.builder()
                            .id(idGroup)
                            .name(finalName)
                            .quota(department.qoutaCount())
                            .faculty(facTitle)
                            .groupTitle(department.groupTitle())
                            .isDefaultFaculty(false)
                            .build()
            );
        }
        return result;
    }

    public List<Student> getAllStudents() {
        List<StudentRow> rows = dataAccessor.readStudents();
        int maxPriority = maxPriority(rows);

        List<Student> result = new ArrayList<>();
        Map<Integer, String> priorityToGroup = new HashMap<>();
        Map<String, Scores> groupToScores = new HashMap<>();

        String currentId = null;
        String currentName = null;

        for (StudentRow studentRow : rows) {
            String studentId = studentRow.studentId();
            String studentName = studentRow.studentName();
            if (currentId == null) {
                currentId = studentId;
                currentName = studentName;
            }
            if (!Objects.equals(currentId, studentId)) {
                result.add(new Student(currentId, currentName, groupToScores, priorityToGroup, maxPriority));
                currentId = studentId;
                currentName = studentName;
                priorityToGroup = new HashMap<>();
                groupToScores = new HashMap<>();
            }
            priorityToGroup.put(studentRow.groupPriority(), studentRow.idGroup());
            groupToScores.put(studentRow.idGroup(), makeScores(studentRow));
        }
        if (currentId != null) {
            result.add(new Student(currentId, currentName, groupToScores, priorityToGroup, maxPriority));
        }
        return result;
    }

    private int maxPriority(List<StudentRow> rows) {
        return rows.stream()
                .mapToInt(r -> r.groupPriority() == null ? 0 : r.groupPriority())
                .max()
                .orElse(0);
    }

    private String createGroupName(String idGroup, DepartmentRow department) {
        String name = idGroup
                + "_"
                + department.fullGroupTitle()
                + "_"
                + FORM_TO_NAME.getOrDefault(department.formOfStudy(), String.valueOf(department.formOfStudy()))
                + "_"
                + (department.specsTitle() == null ? "" : department.specsTitle());
        return name.replace(" ", "_");
    }

    private Scores makeScores(StudentRow studentRow) {
        Map<String, Integer> points = splitPoints(studentRow.examPoints());
        return new Scores(
                studentRow.idGroup(),
                points.getOrDefault("1", 0),
                points.getOrDefault("2", 0),
                points.getOrDefault("3", 0),
                points.getOrDefault("4", 0)
        );
    }

    private Map<String, Integer> splitPoints(String examPoints) {
        Map<String, Integer> result = new HashMap<>();
        if (isBlank(examPoints))
            return result;
        String[] parts = examPoints.trim().split(",");
        for (String p : parts) {
            String[] kv = p.split(":");
            if (kv.length == 2) {
                result.put(kv[0], Integer.parseInt(kv[1]));
            }
        }
        return result;
    }
}
