INSERT INTO resumes (user_id, full_name, title, summary, experience_years, contact_email, contact_phone)
VALUES
    (1, 'Ivan Petrov', 'Java Backend Developer', 'Experienced in Spring Boot, Kafka, Docker.', 4, 'ivan.petrov@mail.com', '+79998887766'),
    (2, 'Anna Ivanova', 'Frontend Engineer', 'React/Next.js developer focused on UX.', 3, 'anna.ivanova@mail.com', '+79997776655');

INSERT INTO education (resume_id, institution, degree, field_of_study, start_year, end_year)
VALUES
    (1, 'Moscow State University', 'Bachelor', 'Computer Science', 2015, 2019),
    (2, 'ITMO University', 'Bachelor', 'Software Engineering', 2016, 2020);

INSERT INTO skills (resume_id, name)
VALUES
    (1, 'Java'), (1, 'Spring Boot'), (1, 'Kafka'), (2, 'React'), (2, 'TypeScript'), (2, 'Next.js');
