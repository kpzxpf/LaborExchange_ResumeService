## 📄 Resume Service

### Overview
Manages candidate resumes, education history, and skills.

**Port:** 8084  
**Database:** PostgreSQL (resumedb - port 5436)

### Key Endpoints

```http
# Get all published resumes
GET /api/resumes?page=0&size=10

# Get resume by ID
GET /api/resumes/1

# Get user's resumes
GET /api/resumes/user/5

# Create resume (Job Seeker only)
POST /api/resumes
Content-Type: application/json
{
  "userId": 5,
  "title": "Senior Software Engineer",
  "summary": "Experienced developer...",
  "experienceYears": 10,
  "contactEmail": "john@example.com",
  "contactPhone": "+79001234567"
}

# Add education
POST /api/educations
{
  "resumeId": 1,
  "institution": "MIT",
  "degree": "Bachelor of Science",
  "fieldOfStudy": "Computer Science",
  "startYear": 2010,
  "endYear": 2014
}

# Add skills
POST /api/skills
{
  "resumeId": 1,
  "name": "Java"
}

# Publish/Unpublish
PATCH /api/resumes/1/publish
PATCH /api/resumes/1/unpublish
```

### Database Schema
```sql
CREATE TABLE resumes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    summary TEXT,
    experience_years INT,
    contact_email VARCHAR(320),
    contact_phone VARCHAR(20),
    is_published BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE education (
    id BIGSERIAL PRIMARY KEY,
    resume_id BIGINT REFERENCES resumes(id),
    institution VARCHAR(255),
    degree VARCHAR(255),
    field_of_study VARCHAR(255),
    start_year INT,
    end_year INT
);

CREATE TABLE skills (
    id BIGSERIAL PRIMARY KEY,
    resume_id BIGINT REFERENCES resumes(id),
    name VARCHAR(255)
);
```

---
