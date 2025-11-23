package com.example.admissionsjpa.service.data.csv;

import com.example.admissionsjpa.model.Student;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CsvWriterService {

    public void saveAssignedStudentsToFile(List<Student> students, String folder, String fileName) throws Exception {
        File dir = new File(folder);
        if (!dir.exists())
            dir.mkdirs();

        File f = new File(dir, fileName);

        List<Map<String, Object>> rows = students
                .stream()
                .map(Student::toCsvMap)
                .collect(Collectors.toList());

        writeMaps(f.toPath(), rows);
    }

    private void writeMaps(Path path, List<Map<String, Object>> rows) throws Exception {
        if (rows.isEmpty()) {
            File parent = path.toFile().getParentFile();
            if (parent != null && !parent.exists())
                parent.mkdirs();
            return;
        }
        List<String> header = new ArrayList<>(rows.get(0).keySet());
        File parent = path.toFile().getParentFile();

        if (parent != null && !parent.exists())
            parent.mkdirs();

        try (CSVWriter w = new CSVWriter(new FileWriter(path.toFile()))) {
            w.writeNext(header.toArray(new String[0]));
            for (Map<String, Object> row : rows) {
                String[] line = new String[header.size()];
                for (int i = 0; i < header.size(); i++) {
                    Object v = row.get(header.get(i));
                    line[i] = v == null ? "" : String.valueOf(v);
                }
                w.writeNext(line);
            }
        }
    }
}
