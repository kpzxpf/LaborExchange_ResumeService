ALTER TABLE resumes
    ADD COLUMN expected_salary INTEGER,
    ADD COLUMN portfolio_url   VARCHAR(512);

CREATE TABLE resume_languages (
    resume_id  BIGINT      NOT NULL REFERENCES resumes (id) ON DELETE CASCADE,
    language   VARCHAR(100) NOT NULL
);

CREATE TABLE resume_certifications (
    resume_id     BIGINT      NOT NULL REFERENCES resumes (id) ON DELETE CASCADE,
    certification VARCHAR(255) NOT NULL
);

CREATE INDEX idx_resume_languages_resume_id ON resume_languages (resume_id);
CREATE INDEX idx_resume_certifications_resume_id ON resume_certifications (resume_id);
