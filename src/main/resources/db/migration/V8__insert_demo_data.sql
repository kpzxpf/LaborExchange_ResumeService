-- Demo resumes with complete profile fields, education, work experience and skills.

UPDATE resumes
SET summary = COALESCE(summary, 'Опытный специалист, готовый рассматривать релевантные предложения и проектную работу.'),
    contact_email = COALESCE(contact_email, 'candidate' || user_id || '@laborexchange.demo'),
    contact_phone = COALESCE(contact_phone, '+7900' || LPAD(id::text, 7, '0')),
    location = COALESCE(location, CASE WHEN id % 3 = 0 THEN 'Москва' WHEN id % 3 = 1 THEN 'Санкт-Петербург' ELSE 'Екатеринбург' END),
    first_name = COALESCE(first_name, 'Кандидат'),
    last_name = COALESCE(last_name, 'Демо ' || user_id),
    expected_salary = COALESCE(expected_salary, 90000 + experience_years * 25000),
    portfolio_url = COALESCE(portfolio_url, 'https://portfolio.laborexchange.demo/resume-' || id),
    updated_at = NOW()
WHERE id BETWEEN 1 AND 40;

INSERT INTO resumes (id, user_id, first_name, last_name, title, summary, location, experience_years, contact_email, contact_phone, is_published, expected_salary, portfolio_url, created_at, updated_at)
VALUES
    (100, 101, 'Александр', 'Петров', 'Senior Java Backend Developer', '6 лет в backend-разработке: Spring Boot, Kafka, Redis, PostgreSQL, DDD и микросервисная архитектура. Люблю наблюдаемость и понятные API.', 'Екатеринбург / Remote', 6, 'alex.backend@laborexchange.demo', '+79001000101', TRUE, 300000, 'https://github.com/alex-backend-demo', NOW() - INTERVAL '40 days', NOW()),
    (101, 101, 'Александр', 'Петров', 'Kotlin Backend Developer', 'Альтернативное резюме под Kotlin/Spring проекты: coroutines, REST API, PostgreSQL и интеграции.', 'Екатеринбург', 4, 'alex.backend@laborexchange.demo', '+79001000101', FALSE, 260000, 'https://gitlab.com/alex-kotlin-demo', NOW() - INTERVAL '32 days', NOW()),
    (102, 102, 'Мария', 'Соколова', 'Frontend Engineer React/Next.js', 'React, TypeScript, Next.js, дизайн-системы, сложные формы и e2e-тесты. Работала с продуктовой аналитикой и A/B экспериментами.', 'Москва / Remote', 3, 'maria.frontend@laborexchange.demo', '+79001000102', TRUE, 210000, 'https://maria-frontend.demo', NOW() - INTERVAL '36 days', NOW()),
    (103, 103, 'Иван', 'Кузнецов', 'Data Engineer / Product Analyst', 'Строю пайплайны Airflow, витрины ClickHouse, аналитические модели и дашборды для продуктовых команд.', 'Москва', 5, 'ivan.data@laborexchange.demo', '+79001000103', TRUE, 240000, 'https://github.com/ivan-data-demo', NOW() - INTERVAL '34 days', NOW()),
    (104, 104, 'Ольга', 'Морозова', 'QA Automation Engineer', 'Автоматизация UI/API тестов на Playwright, Selenium и PyTest. Настраиваю CI, отчеты и стабильные регрессионные наборы.', 'Санкт-Петербург', 4, 'olga.qa@laborexchange.demo', '+79001000104', TRUE, 170000, 'https://qa-demo.laborexchange.demo/olga', NOW() - INTERVAL '30 days', NOW()),
    (105, 105, 'Никита', 'Волков', 'DevOps/SRE Engineer', 'Kubernetes, Docker, Terraform, Ansible, Prometheus и GitLab CI. Снижаю время восстановления и автоматизирую рутину.', 'Казань / Remote', 6, 'nikita.devops@laborexchange.demo', '+79001000105', TRUE, 260000, 'https://github.com/nikita-sre-demo', NOW() - INTERVAL '29 days', NOW()),
    (106, 106, 'Дарья', 'Лебедева', 'Junior Python Developer', 'Junior-разработчик: Python, Django, SQL, базовая аналитика данных и желание быстро расти в backend-команде.', 'Новосибирск', 1, 'pending.email@laborexchange.demo', '+79001000106', TRUE, 90000, 'https://github.com/daria-python-demo', NOW() - INTERVAL '6 days', NOW()),
    (107, 107, 'Павел', 'Сергеев', 'Support Engineer', 'Опыт поддержки пользователей, диагностика логов, SQL-запросы, Linux и коммуникация с разработкой.', 'Пермь', 2, 'inactive.candidate@laborexchange.demo', '+79001000107', FALSE, 85000, 'https://support-demo.laborexchange.demo/pavel', NOW() - INTERVAL '20 days', NOW()),
    (108, 102, 'Мария', 'Соколова', 'UI/UX Product Designer', 'Дополнительный профиль: UX research, прототипирование, Figma, пользовательские интервью и дизайн-системы.', 'Remote', 4, 'maria.frontend@laborexchange.demo', '+79001000102', TRUE, 180000, 'https://behance.net/maria-demo', NOW() - INTERVAL '18 days', NOW())
ON CONFLICT (id) DO UPDATE SET
    user_id = EXCLUDED.user_id,
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    title = EXCLUDED.title,
    summary = EXCLUDED.summary,
    location = EXCLUDED.location,
    experience_years = EXCLUDED.experience_years,
    contact_email = EXCLUDED.contact_email,
    contact_phone = EXCLUDED.contact_phone,
    is_published = EXCLUDED.is_published,
    expected_salary = EXCLUDED.expected_salary,
    portfolio_url = EXCLUDED.portfolio_url,
    updated_at = NOW();

INSERT INTO resume_skills (resume_id, skill_id)
VALUES
    (100, 1), (100, 2), (100, 3), (100, 18), (100, 100), (100, 101), (100, 109),
    (101, 2), (101, 4), (101, 5), (101, 108),
    (102, 11), (102, 12), (102, 13), (102, 106), (102, 117), (102, 118),
    (103, 16), (103, 19), (103, 33), (103, 111), (103, 112), (103, 120),
    (104, 16), (104, 42), (104, 44), (104, 45), (104, 118),
    (105, 18), (105, 23), (105, 38), (105, 39), (105, 40), (105, 116),
    (106, 16), (106, 17), (106, 33),
    (107, 33), (107, 115), (107, 29),
    (108, 50), (108, 52), (108, 119)
ON CONFLICT DO NOTHING;

INSERT INTO education (id, resume_id, institution, degree, field_of_study, start_year, end_year)
VALUES
    (100, 100, 'УрФУ', 'Магистр', 'Программная инженерия', 2014, 2020),
    (101, 102, 'НИУ ВШЭ', 'Бакалавр', 'Программная инженерия', 2018, 2022),
    (102, 103, 'МФТИ', 'Магистр', 'Прикладная математика и информатика', 2013, 2019),
    (103, 104, 'ИТМО', 'Бакалавр', 'Информационные системы', 2015, 2019),
    (104, 105, 'КФУ', 'Бакалавр', 'Информационная безопасность', 2012, 2016),
    (105, 106, 'Нетология', 'Диплом', 'Python-разработка', 2024, 2025),
    (106, 108, 'Британская высшая школа дизайна', 'Диплом', 'UX/UI Design', 2018, 2020)
ON CONFLICT (id) DO UPDATE SET
    resume_id = EXCLUDED.resume_id,
    institution = EXCLUDED.institution,
    degree = EXCLUDED.degree,
    field_of_study = EXCLUDED.field_of_study,
    start_year = EXCLUDED.start_year,
    end_year = EXCLUDED.end_year;

INSERT INTO work_experience (id, resume_id, company_name, position, description, start_year, start_month, end_year, end_month, is_current)
VALUES
    (100, 100, 'CloudRetail', 'Backend Developer', 'Разработка заказного ядра, интеграции Kafka, Redis-кэш и оптимизация PostgreSQL.', 2018, 4, 2021, 8, FALSE),
    (101, 100, 'Ural Digital Lab', 'Senior Backend Developer', 'Техническое лидерство в микросервисной команде, code review и observability.', 2021, 9, NULL, NULL, TRUE),
    (102, 102, 'MarketKit', 'Frontend Engineer', 'Личный кабинет продавца, дизайн-система, React Query и e2e-тесты.', 2021, 2, NULL, NULL, TRUE),
    (103, 103, 'FinMetrics', 'Data Analyst', 'Витрины ClickHouse, Airflow DAGs, продуктовые метрики и дашборды.', 2019, 6, NULL, NULL, TRUE),
    (104, 104, 'MedSoft QA', 'QA Automation Engineer', 'Автоматизация регресса, API-тесты и интеграция проверок в GitLab CI.', 2020, 1, NULL, NULL, TRUE),
    (105, 105, 'ScaleOps', 'SRE Engineer', 'Kubernetes, Terraform, мониторинг SLO и incident response.', 2019, 3, NULL, NULL, TRUE),
    (106, 106, 'Study Projects', 'Python Developer Intern', 'Учебные проекты на Django, REST API и PostgreSQL.', 2025, 1, NULL, NULL, TRUE),
    (107, 108, 'Product Studio', 'UX/UI Designer', 'Исследования, прототипы, дизайн-система и usability-тесты.', 2020, 5, NULL, NULL, TRUE)
ON CONFLICT (id) DO UPDATE SET
    resume_id = EXCLUDED.resume_id,
    company_name = EXCLUDED.company_name,
    position = EXCLUDED.position,
    description = EXCLUDED.description,
    start_year = EXCLUDED.start_year,
    start_month = EXCLUDED.start_month,
    end_year = EXCLUDED.end_year,
    end_month = EXCLUDED.end_month,
    is_current = EXCLUDED.is_current;

INSERT INTO resume_languages (resume_id, language)
VALUES
    (100, 'Русский C2'), (100, 'English B2'),
    (102, 'Русский C2'), (102, 'English B2'),
    (103, 'Русский C2'), (103, 'English B1'),
    (104, 'Русский C2'), (104, 'English B1'),
    (105, 'Русский C2'), (105, 'English B2'),
    (106, 'Русский C2'), (106, 'English A2'),
    (108, 'Русский C2'), (108, 'English B2');

INSERT INTO resume_certifications (resume_id, certification)
VALUES
    (100, 'Spring Professional Training'),
    (100, 'Kafka Streams Essentials'),
    (102, 'React Advanced Patterns'),
    (103, 'Data Engineering with Airflow'),
    (104, 'ISTQB Foundation Level'),
    (105, 'Kubernetes Administrator Practice'),
    (106, 'Python Backend Developer'),
    (108, 'UX Research Intensive');

SELECT setval(pg_get_serial_sequence('resumes', 'id'), COALESCE((SELECT MAX(id) FROM resumes), 1), TRUE);
SELECT setval(pg_get_serial_sequence('education', 'id'), COALESCE((SELECT MAX(id) FROM education), 1), TRUE);
SELECT setval(pg_get_serial_sequence('work_experience', 'id'), COALESCE((SELECT MAX(id) FROM work_experience), 1), TRUE);
