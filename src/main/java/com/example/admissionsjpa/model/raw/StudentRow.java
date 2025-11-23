package com.example.admissionsjpa.model.raw;

public record StudentRow(
        String studentId,
        String studentName,
        Integer groupPriority,
        String idGroup,
        String fullGroupTitle,
        String formOfStudy,
        String examPoints,   // пример: "1:85,2:92"
        String facultyTitle,
        String specsTitle,
        String examName      // пример: "1:Математика,2:Физика"
) {

}
