# use ssubase;

(SELECT 'id_group',
        'group_title_full',
        'qouta_count',
        'form_of_study',
        'faculty_title',
        'specs_title',
        'group_title')
UNION
(SELECT gr.id_grp                 as 'id_group'
      , gr.title                  as 'group_title_full'
      , gr.plan                   as 'qouta_count'
      , gr.form                   as 'form_of_study'
      , dep.title                 as 'faculty_title'
      , coalesce(specs.title, '') as 'specs_title'
      , gr.dir_title              as 'group_title'
 FROM ssu_abit_dep dep
          LEFT JOIN ssu_abit_groups gr ON dep.id_dep = gr.id_dep
          LEFT JOIN ssu_abit_specs specs ON gr.id_grp = specs.id_grp
 WHERE gr.dir_level in (1, 2, 3))
INTO OUTFILE '/departments.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';


(SELECT 'student_id',
        'student_name',
        'group_priority',
        'id_group',
        'group_title_full',
        'form_of_study',
        'exam_points',
        'faculty_title',
        'specs_title',
        'exam_name')
UNION
(SELECT abit_name.id_pers          as 'student_id'
     , abit_name.fio               as 'student_name'
     , abit.priority               as 'group_priority'
     , gr.id_grp                   as 'id_group'
     , gr.title                    as 'group_title_full'
     , gr.form                     as 'form_of_study'
     , GROUP_CONCAT(CONCAT_WS(':', exam.exam_priority, exam.points)
                    SEPARATOR ',') as 'exam_points'
     , dep.title                   as 'faculty_title'
     , coalesce(specs.title, '')   as 'specs_title'
     , GROUP_CONCAT(CONCAT_WS(':', exam.exam_priority, exam.subject)
                    SEPARATOR ',') as 'exam_name'
FROM ssu_abit_dep dep
         LEFT JOIN ssu_abit_groups gr ON dep.id_dep = gr.id_dep
         LEFT JOIN ssu_abit_spisok abit ON gr.id_grp = abit.id_grp
         LEFT JOIN ssu_abit_exam_points exam ON abit.id_sps = exam.id_sps
         LEFT JOIN ssu_abit_pers abit_name on abit.id_pers = abit_name.id_pers
         LEFT JOIN ssu_abit_specs specs on gr.id_grp = specs.id_grp
WHERE exam.exam_priority IS NOT NULL
  AND gr.dir_level in (1, 2, 3)
GROUP BY abit_name.id_pers, gr.id_grp, coalesce(specs.id_spec, -1)
ORDER BY abit_name.id_pers)
INTO OUTFILE '/students.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
