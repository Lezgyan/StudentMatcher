# Admissions Spring

Консольное Java Spring Boot приложение для чтения, преобразования и анализа данных абитуриентов и учебных направлений.  
Позволяет работать с двумя источниками данных: CSV и база данных (MySQL/MariaDB).

## Ограничение Windows: запускать ближе к корню диска

Windows ограничивает длину пути к файлу: 255 символов.  
Запускайте JAR-файл и храните CSV как можно ближе к корню, например:

```
D:\admissions-console\
```

Иначе возможны ошибки чтения файлов.

## Запуск

1. Настройте `src/main/resources/application.yml`.
2. Запуск:
   ```bash
   mvn -q -DskipTests spring-boot:run
   ```
   или
   ```bash
   mvn -q -DskipTests package
   java -jar target/admissions-console-spring-jpa-1.0.0.jar
   ```

Настройки производятся в application.yml файле

```yaml
global:
  # Папка, куда приложение будет выгружать результаты обработки
  result-folder: answer

  # Источник данных:
  #   DB  – читать из базы данных
  #   CSV – читать из CSV файлов
  data-source: CSV

# Если в качестве источника данных используется CSV файл
csv:
  # Папка с CSV файлами входных данных
  data-folder: resource
  departments-file: departments.csv
  students-file: students.csv

# Если в качестве источника данных используется база данных
spring:
  datasource:
    url: jdbc:mariadb://localhost:3308/ssubase
    username: root
    password:
    driver-class-name: org.mariadb.jdbc.Driver
```

# Форматы CSV-файлов `departments.csv` и `students.csv`

### 1. `departments.csv`

| Поле               | Тип        | Описание                              |
|--------------------|------------|---------------------------------------|
| `id_group`         | string/int | ID направления / группы               |
| `group_title_full` | string     | Полное название направления (с кодом) |
| `qouta_count`      | int        | Количество бюджетных мест / квота     |
| `form_of_study`    | int        | Форма обучения (0, 1, 2 и т.п.)       |
| `faculty_title`    | string     | Название факультета                   |
| `specs_title`      | string     | Название специальности / профиля      |
| `group_title`      | string     | Короткое название направления         |

Пример строки:

```
id_group,group_title_full,qouta_count,form_of_study,faculty_title,specs_title,group_title
1,1_44.03.01_Педагогическое_образование/Бюджетная_основа/Общие_места_ОЧНАЯ_Биология,7,0,Биологический_факультет,Биология,Педагогическое образование
```

### 2. `students.csv`

| Поле               | Тип    | Описание                                              |
|--------------------|--------|-------------------------------------------------------|
| `student_id`       | string | Внутренний ID абитуриента                             |
| `student_name`     | string | Имя/шифр абитуриента                                  |
| `group_priority`   | int    | Приоритет этого направления для данного студента      |
| `id_group`         | string | ID направления (должен совпадать с `departments.csv`) |
| `group_title_full` | string | Полное название направления                           |
| `form_of_study`    | string | Форма обучения (как строка, например `"0"`)           |
| `exam_points`      | string | Баллы по экзаменам в формате `priority:points,...`    |
| `faculty_title`    | string | Название факультета                                   |
| `specs_title`      | string | Название специальности                                |
| `exam_name`        | string | Названия экзаменов в формате `priority:name,...`      |

Пример строки:

```
student_id,student_name,group_priority,id_group,group_title_full,form_of_study,exam_points,faculty_title,specs_title,exam_name
84466,118-443-139 46,1,492,492_44.03.03_Специальное-дефектологическое_образование/...,0,"1:90,2:88,3:75","Факультет психолого-педагогического и специального образования","Логопедия","1:Математика,2:Русский язык,3:Обществознание"
```

Поля `exam_points` и `exam_name`:

* строка вида: 1:90,2:88,3:75 разбирается на пары приоритет : значение (например, приоритет 1 → 90 баллов). Такая же
  логика применяется и для exam_name.