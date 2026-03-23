-- Published resumes (employer search)
CREATE INDEX IF NOT EXISTS idx_resumes_published ON resumes(is_published) WHERE is_published = true;

-- Resume-skill join table indexes
CREATE INDEX IF NOT EXISTS idx_resume_skills_resume_id ON resume_skills(resume_id);
CREATE INDEX IF NOT EXISTS idx_resume_skills_skill_id ON resume_skills(skill_id);
