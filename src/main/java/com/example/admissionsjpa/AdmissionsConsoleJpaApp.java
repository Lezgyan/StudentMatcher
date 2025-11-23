package com.example.admissionsjpa;

import com.example.admissionsjpa.service.data.DataReader;
import com.example.admissionsjpa.service.data.csv.CsvWriterService;
import com.example.admissionsjpa.model.Department;
import com.example.admissionsjpa.model.Student;
import com.example.admissionsjpa.service.MatchStudentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;
import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
public class AdmissionsConsoleJpaApp implements CommandLineRunner {

    private final DataReader dataReader;
    private final CsvWriterService csvWriter;
    private final MatchStudentsService matcher;

    @Value("${global.result-folder}")
    private String resultFolder;

    public static void main(String[] args) {
        SpringApplication.run(AdmissionsConsoleJpaApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        List<Department> departments = dataReader.getDepartments();
        List<Student> students = dataReader.getAllStudents();
        List<Department> result = matcher.match(students, departments);


        for (Department group : result) {
            String folder = Path.of(resultFolder, sanitize(group.getFaculty()), sanitize(group.getGroupTitle())).toString();
            String filename = sanitize(group.getName()) + ".csv";
            csvWriter.saveAssignedStudentsToFile(group.getAssignedStudents(), folder, filename);
        }

        System.out.println("Готово: результаты распределения сохранены в '" + resultFolder + "'");
    }

    private static String sanitize(String s) {
        return s == null ? "" : s
                .replace(" ", "_")
                .replace("/", "_")
                .replace("\\", "_");
    }
}
