package com.example.admissionsjpa.service.data;

import com.example.admissionsjpa.model.raw.DepartmentRow;
import com.example.admissionsjpa.model.raw.StudentRow;

import java.util.List;
import java.util.Map;

public interface DataAccessor {

    Map<Integer, String> FORM_TO_NAME = Map.of(
            0, "ОЧНАЯ",
            1, "ОЧНО_ЗАОЧНАЯ",
            2, "ЗАОЧНАЯ"
    );

    List<DepartmentRow> readDepartments();
    List<StudentRow> readStudents();

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
}