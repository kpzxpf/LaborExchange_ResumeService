CREATE TABLE resumes (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         title VARCHAR(255),
                         summary TEXT,
                         experience_years INT,
                         contact_email VARCHAR(255),
                         contact_phone VARCHAR(50),
                         created_at TIMESTAMP DEFAULT NOW(),
                         updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE education (
                           id BIGSERIAL PRIMARY KEY,
                           resume_id BIGINT REFERENCES resumes(id) ON DELETE CASCADE,
                           institution VARCHAR(255),
                           degree VARCHAR(255),
                           field_of_study VARCHAR(255),
                           start_year INT,
                           end_year INT
);

CREATE TABLE skills (
                        id BIGSERIAL PRIMARY KEY,
                        resume_id BIGINT REFERENCES resumes(id) ON DELETE CASCADE,
                        name VARCHAR(100)
);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_updated_at_resume
    BEFORE UPDATE ON resumes
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
