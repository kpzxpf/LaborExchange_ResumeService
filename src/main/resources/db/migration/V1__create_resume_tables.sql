CREATE TABLE resumes
(
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT       NOT NULL,
    title            VARCHAR(255) NOT NULL,
    summary          TEXT,
    experience_years INT                   DEFAULT 0,
    contact_email    VARCHAR(255),
    contact_phone    VARCHAR(50),
    is_published     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP             DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_resumes_user_id ON resumes (user_id);

CREATE TABLE education
(
    id             BIGSERIAL PRIMARY KEY,
    resume_id      BIGINT       NOT NULL REFERENCES resumes (id) ON DELETE CASCADE,
    institution    VARCHAR(255) NOT NULL,
    degree         VARCHAR(255),
    field_of_study VARCHAR(255),
    start_year     INT          NOT NULL,
    end_year       INT
);

CREATE INDEX idx_education_resume_id ON education (resume_id);

CREATE TABLE resume_skills
(
    resume_id BIGINT NOT NULL REFERENCES resumes (id) ON DELETE CASCADE,
    skill_id  BIGINT NOT NULL,
    PRIMARY KEY (resume_id, skill_id)
);

CREATE INDEX idx_skills_resume_id ON skills (resume_id);

ALTER TABLE education
    ADD CONSTRAINT chk_education_years CHECK (end_year IS NULL OR end_year >= start_year);

ALTER TABLE resumes
    ADD CONSTRAINT chk_experience_positive CHECK (experience_years >= 0);