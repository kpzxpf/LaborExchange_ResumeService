package com.vlz.laborexchange_resumeservice.ai;

import com.vlz.laborexchange_resumeservice.dto.AiResumeRequestDto;

public final class AiPromptBuilder {

    private AiPromptBuilder() {}

    public static final String SYSTEM_PROMPT =
            "Ты — ведущий карьерный коуч и HR-эксперт с 15-летним опытом в России. " +
            "Специализируешься на написании резюме, которые проходят ATS-системы и вызывают интерес у рекрутеров ведущих российских компаний. " +
            "Твои резюме конкретны, измеримы и убедительны. " +
            "Всегда отвечай строго валидным JSON без markdown-блоков, комментариев и посторонних пояснений.";

    public static String buildUserPrompt(AiResumeRequestDto req) {
        return String.format("""
                Создай профессиональное резюме для кандидата на должность "%s".

                Данные кандидата:
                - Опыт работы: %d %s
                - Город: %s
                - Описание от кандидата: %s

                Требования (строго соблюдай):

                SUMMARY (поле "summary"):
                - 3–4 предложения на русском языке
                - Первое предложение: кто ты (специалист + специализация + лет опыта)
                - Второе: ключевые компетенции и технологии (конкретно)
                - Третье: главные достижения или подход к работе
                - Четвёртое (опционально): что ищешь / ценность для работодателя
                - Без воды, конкретно, в третьем лице ("Опытный разработчик...", "Специалист с...")

                ОПЫТ РАБОТЫ (поле "workExperiences"), 2–3 записи:
                - Реалистичные названия компаний (можно использовать: Яндекс, Сбер, VK, Тинькофф, EPAM, Газпром нефть, Ростелеком, МТС, Авито, Ozon, Lamoda, Альфа-Банк, X5 Group, и похожие реальные компании)
                - Position соответствует карьерному росту (от Junior к Senior или смена компаний)
                - Даты реалистичны: последнее место работы может быть текущим (isCurrent: true, endYear: null)
                - description — 3–5 конкретных достижений или обязанностей с МЕТРИКАМИ:
                  * Используй числа: "увеличил производительность на 40%%", "сократил время загрузки с 3с до 0.8с"
                  * Команда: "руководил командой из 5 разработчиков", "работал в кросс-функциональной команде 12 человек"
                  * Масштаб: "система обрабатывала 2 млн запросов в сутки", "реализовал функционал для 500 тыс. пользователей"
                  * Каждое достижение — отдельная строка, начинается с глагола действия
                - startMonth и endMonth — реалистичные числа от 1 до 12

                НАВЫКИ (поле "suggestedSkillNames"), 10–15 штук:
                - Конкретные технологии, фреймворки, инструменты (не абстрактные "Командная работа")
                - Соответствуют должности и описанию кандидата
                - Смесь технических (70%%) и профессиональных (30%%) навыков
                - Примеры технических: Java, Spring Boot, PostgreSQL, Docker, Kubernetes, React, TypeScript
                - Примеры профессиональных: Agile/Scrum, Code Review, Технические интервью

                ОБРАЗОВАНИЕ (поле "educations"), 1–2 записи:
                - Реалистичные российские вузы (МГУ, МГТУ им. Баумана, СПбГУ, НГУ, ВШЭ, ИТМО и похожие)
                - degree: "Бакалавр", "Магистр" или "Специалист"
                - Даты соответствуют опыту работы

                ВАЖНО: поле "title" — конкретное название должности (не общее "Разработчик", а "Senior Java Backend Developer" или "Ведущий Python-разработчик").

                Верни ТОЛЬКО валидный JSON:
                {
                  "title": "строка",
                  "summary": "строка",
                  "location": "строка",
                  "experienceYears": число,
                  "suggestedSkillNames": ["навык1", "навык2", "..."],
                  "workExperiences": [
                    {
                      "companyName": "строка",
                      "position": "строка",
                      "description": "строка с переносами строк \\n для разделения достижений",
                      "startYear": число,
                      "startMonth": число,
                      "endYear": число или null,
                      "endMonth": число или null,
                      "isCurrent": true или false
                    }
                  ],
                  "educations": [
                    {
                      "institution": "строка",
                      "degree": "Бакалавр",
                      "fieldOfStudy": "строка",
                      "startYear": число,
                      "endYear": число или null
                    }
                  ]
                }
                """,
                req.getJobTitle(),
                req.getYearsOfExperience() != null ? req.getYearsOfExperience() : 0,
                experienceWordForm(req.getYearsOfExperience()),
                req.getLocation() != null && !req.getLocation().isBlank() ? req.getLocation() : "не указан",
                req.getDescription()
        );
    }

    private static String experienceWordForm(Integer years) {
        if (years == null || years == 0) return "лет (начинающий специалист)";
        if (years == 1) return "год";
        if (years < 5) return "года";
        return "лет";
    }
}
