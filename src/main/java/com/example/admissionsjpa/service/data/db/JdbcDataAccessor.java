package com.example.admissionsjpa.service.data.db;

import com.example.admissionsjpa.model.raw.DepartmentRow;
import com.example.admissionsjpa.model.raw.StudentRow;
import com.example.admissionsjpa.service.data.DataAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "global", name = "data-source", havingValue = "DB")
public class JdbcDataAccessor implements DataAccessor {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<DepartmentRow> readDepartments() {
        return jdbc.query(SQL_FETCH_DEPARTMENTS, DEPT_ROW_MAPPER);
    }

    @Override
    public List<StudentRow> readStudents() {
        return jdbc.query(SQL_FETCH_STUDENTS, STUDENT_ROW_MAPPER);
    }

    private static final String SQL_FETCH_DEPARTMENTS = """
            SELECT gr.id_grp                  AS id_group,
                   gr.title                   AS full_group_title,
                   gr.plan                    AS qouta_count,
                   gr.form                    AS form_of_study,
                   dep.title                  AS faculty_title,
                   COALESCE(specs.title, '')  AS specs_title,
                   gr.dir_title               AS group_title
              FROM ssu_abit_dep dep
              LEFT JOIN ssu_abit_groups gr ON dep.id_dep = gr.id_dep
              LEFT JOIN ssu_abit_specs specs ON gr.id_grp = specs.id_grp
             WHERE gr.dir_level in (1, 2, 3)
            """;

    private static final String SQL_FETCH_STUDENTS = """
            SELECT    abit_name.id_pers          AS student_id,
                      abit_name.fio               AS student_name,
                      abit.priority               AS group_priority,
                      gr.id_grp                   AS id_group,
                      gr.title                    AS full_group_title,
                      gr.form                     AS form_of_study,
                      GROUP_CONCAT(CONCAT_WS(':', exam.exam_priority, exam.points) SEPARATOR ',') AS exam_points,
                      dep.title                   AS faculty_title,
                      COALESCE(specs.title, '')   AS specs_title,
                      GROUP_CONCAT(CONCAT_WS(':', exam.exam_priority, exam.subject) SEPARATOR ',') AS exam_name
            FROM ssu_abit_dep dep
                 LEFT JOIN ssu_abit_groups gr ON dep.id_dep = gr.id_dep
                 LEFT JOIN ssu_abit_spisok abit ON gr.id_grp = abit.id_grp
                 LEFT JOIN ssu_abit_exam_points exam ON abit.id_sps = exam.id_sps
                 LEFT JOIN ssu_abit_pers abit_name on abit.id_pers = abit_name.id_pers
                 LEFT JOIN ssu_abit_specs specs on gr.id_grp = specs.id_grp
            WHERE exam.exam_priority IS NOT NULL
              AND gr.dir_level in (1, 2, 3)
            GROUP BY abit_name.id_pers, gr.id_grp, COALESCE(specs.id_spec, -1)
            ORDER BY abit_name.id_pers
               """;

    private static final RowMapper<DepartmentRow> DEPT_ROW_MAPPER = (rs, rowNum) -> new DepartmentRow(
            rs.getString("id_group"),
            rs.getString("full_group_title"),
            getInteger(rs, "qouta_count"),
            rs.getInt("form_of_study"),
            rs.getString("faculty_title"),
            rs.getString("specs_title"),
            rs.getString("group_title")
    );

    private static final RowMapper<StudentRow> STUDENT_ROW_MAPPER = (rs, rowNum) -> new StudentRow(
            rs.getString("student_id"),
            rs.getString("student_name"),
            getInteger(rs, "group_priority"),
            rs.getString("id_group"),
            rs.getString("full_group_title"),
            rs.getString("form_of_study"),
            rs.getString("exam_points"),
            rs.getString("faculty_title"),
            rs.getString("specs_title"),
            rs.getString("exam_name")
    );

    private static Integer getInteger(ResultSet rs, String column) throws SQLException {
        int v = rs.getInt(column);
        return rs.wasNull() ? null : v;
    }
}
