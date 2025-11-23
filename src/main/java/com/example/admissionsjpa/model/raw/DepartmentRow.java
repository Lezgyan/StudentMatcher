package com.example.admissionsjpa.model.raw;

public record DepartmentRow(
        String idGroup,
        String fullGroupTitle,
        Integer qoutaCount,
        Integer formOfStudy,
        String facultyTitle,
        String specsTitle,
        String groupTitle
) {}