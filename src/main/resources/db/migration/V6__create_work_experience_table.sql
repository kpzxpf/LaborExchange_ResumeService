CREATE TABLE work_experience
(
    id           BIGSERIAL PRIMARY KEY,
    resume_id    BIGINT       NOT NULL REFERENCES resumes (id) ON DELETE CASCADE,
    company_name VARCHAR(255) NOT NULL,
    position     VARCHAR(255) NOT NULL,
    description  TEXT,
    start_year   INT          NOT NULL,
    start_month  INT,
    end_year     INT,
    end_month    INT,
    is_current   BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT chk_we_months CHECK (
        (start_month IS NULL OR (start_month >= 1 AND start_month <= 12)) AND
        (end_month IS NULL OR (end_month >= 1 AND end_month <= 12))
    ),
    CONSTRAINT chk_we_years CHECK (
        end_year IS NULL OR end_year >= start_year
    )
);

CREATE INDEX idx_work_experience_resume_id ON work_experience (resume_id);
