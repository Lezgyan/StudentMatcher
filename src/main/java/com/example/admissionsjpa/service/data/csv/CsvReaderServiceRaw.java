package com.example.admissionsjpa.service.data.csv;

import com.example.admissionsjpa.model.raw.DepartmentRow;
import com.example.admissionsjpa.model.raw.StudentRow;
import com.example.admissionsjpa.service.data.DataAccessor;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiFunction;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "global", name = "data-source", havingValue = "CSV")
public class CsvReaderServiceRaw implements DataAccessor {

    @Value("${csv.data-folder}")
    private String dataFolder;

    @Value("${csv.departments-file}")
    private String departmentsFile;

    @Value("${csv.students-file}")
    private String studentsFile;

    @Override
    public List<DepartmentRow> readDepartments() {
        Path path = Path.of(dataFolder, departmentsFile);
        return readCsv(path, this::mapRowToDepartment);
    }

    @Override
    public List<StudentRow> readStudents() {
        Path path = Path.of(dataFolder, studentsFile);
        return readCsv(path, this::mapRowToStudent);
    }

    private <T> List<T> readCsv(Path filePath, BiFunction<Map<String, Integer>, String[], T> mapper) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
            String[] header = reader.readNext();
            Map<String, Integer> idx = index(header);
            List<T> result = new ArrayList<>();
            String[] row;
            while ((row = reader.readNext()) != null) {
                result.add(mapper.apply(idx, row));
            }
            return result;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private DepartmentRow mapRowToDepartment(Map<String, Integer> idx, String[] row) {
        return new DepartmentRow(
                get(row, idx, "id_group"),
                get(row, idx, "group_title_full"),
                parseInt(get(row, idx, "qouta_count")),
                parseInt(get(row, idx, "form_of_study")),
                get(row, idx, "faculty_title"),
                get(row, idx, "specs_title"),
                get(row, idx, "group_title")
        );
    }

    private StudentRow mapRowToStudent(Map<String, Integer> idx, String[] row) {
        return new StudentRow(
                get(row, idx, "student_id"),
                get(row, idx, "student_name"),
                parseInt(get(row, idx, "group_priority")),
                get(row, idx, "id_group"),
                get(row, idx, "group_title_full"),
                get(row, idx, "form_of_study"),
                get(row, idx, "exam_points"),
                get(row, idx, "faculty_title"),
                get(row, idx, "specs_title"),
                get(row, idx, "exam_name")
        );
    }

    private static Map<String, Integer> index(String[] header) {
        Map<String, Integer> idx = new HashMap<>();
        for (int i = 0; i < header.length; i++)
            idx.put(header[i], i);
        return idx;
    }

    private static String get(String[] row, Map<String, Integer> idx, String key) {
        Integer i = idx.get(key);
        return (i == null || i >= row.length) ? "" : row[i];
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }
}
