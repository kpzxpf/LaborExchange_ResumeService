# 📝 LaborExchange Resume Service

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?style=for-the-badge&logo=spring)
![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)

**Resume/CV Management Service**

</div>

---

## 📋 Overview

Resume Service manages job seeker resumes, including education history, skills, and work experience. Only job seekers can create and manage resumes.

### Key Features

✅ **Resume CRUD** - Create, read, update, delete resumes  
✅ **Education Management** - Add/update education entries  
✅ **Skills Tracking** - Manage skill sets  
✅ **Job Seeker Only** - Role-based access control  
✅ **PostgreSQL Storage** - Persistent data with Flyway migrations  

## 🏗️ Architecture

**Service:** Port 8084  
**Database:** PostgreSQL on port 5436  

### System Flow

```
Client → API Gateway → Resume Service → Database
                     ↓
              Role Validation (User Service)
```

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| Framework | Spring Boot | 3.2.x |
| Language | Java | 17 |
| Database | PostgreSQL | 16 |
| ORM | Spring Data JPA | 3.2.x |
| Migrations | Flyway | 9.22.x |
| HTTP Client | OpenFeign | 4.1.x |

## 📡 API Endpoints

### Resume Endpoints

#### Create Resume

```http
POST /api/resumes
Content-Type: application/json
X-User-Id: 123
X-User-Role: JOB_SEEKER

{
  "title": "Senior Java Developer",
  "summary": "Experienced developer with 10 years...",
  "userId": 123
}
```

**Response:**
```json
{
  "id": 1,
  "title": "Senior Java Developer",
  "summary": "Experienced developer...",
  "userId": 123,
  "createdAt": "2024-02-05T10:30:00Z"
}
```

#### Get Resume by ID

```http
GET /api/resumes/{id}
```

#### Get All Resumes

```http
GET /api/resumes?page=0&size=20
```

#### Update Resume

```http
PUT /api/resumes/{id}
X-User-Id: 123

{
  "title": "Lead Java Developer",
  "summary": "Updated summary..."
}
```

#### Delete Resume

```http
DELETE /api/resumes/{id}
X-User-Id: 123
```

### Education Endpoints

#### Add Education

```http
POST /api/resumes/{resumeId}/education

{
  "institution": "MIT",
  "degree": "Bachelor of Science",
  "fieldOfStudy": "Computer Science",
  "startDate": "2010-09-01",
  "endDate": "2014-06-01"
}
```

#### Get Education

```http
GET /api/resumes/{resumeId}/education
```

#### Update Education

```http
PUT /api/education/{id}

{
  "degree": "Master of Science"
}
```

#### Delete Education

```http
DELETE /api/education/{id}
```

### Skills Endpoints

#### Add Skill

```http
POST /api/resumes/{resumeId}/skills

{
  "name": "Java",
  "level": "EXPERT",
  "yearsOfExperience": 10
}
```

#### Get Skills

```http
GET /api/resumes/{resumeId}/skills
```

#### Update Skill

```http
PUT /api/skills/{id}

{
  "level": "MASTER",
  "yearsOfExperience": 12
}
```

#### Delete Skill

```http
DELETE /api/skills/{id}
```

## 🗄️ Database Schema

### Tables

```sql
-- Resumes
CREATE TABLE resumes (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    summary TEXT,
    user_id BIGINT NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Education
CREATE TABLE education (
    id BIGSERIAL PRIMARY KEY,
    resume_id BIGINT NOT NULL,
    institution VARCHAR(255) NOT NULL,
    degree VARCHAR(100),
    field_of_study VARCHAR(100),
    start_date DATE,
    end_date DATE,
    description TEXT,
    CONSTRAINT fk_resume FOREIGN KEY (resume_id) 
        REFERENCES resumes(id) ON DELETE CASCADE
);

-- Skills
CREATE TABLE skills (
    id BIGSERIAL PRIMARY KEY,
    resume_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    level VARCHAR(50),
    years_of_experience INTEGER,
    CONSTRAINT fk_resume_skill FOREIGN KEY (resume_id) 
        REFERENCES resumes(id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_resumes_user ON resumes(user_id);
CREATE INDEX idx_education_resume ON education(resume_id);
CREATE INDEX idx_skills_resume ON skills(resume_id);
```

### Entity Relationships

```
┌──────────────┐
│   resumes    │ 1
│──────────────│───┐
│ id (PK)      │   │
│ user_id      │   │ N
└──────────────┘   │
                   ├──── education (N)
                   └──── skills (N)
```

## 🔐 Security

### Role Validation

```java
@Service
public class ResumeService {
    
    public Resume createResume(CreateResumeRequest request, Long userId) {
        // Validate role
        String role = roleClient.getUserRoleByUserId(userId);
        
        if (!"JOB_SEEKER".equals(role)) {
            throw new ForbiddenException("Only job seekers can create resumes");
        }
        
        // Check if user already has resume
        if (resumeRepository.existsByUserId(userId)) {
            throw new ConflictException("User already has a resume");
        }
        
        Resume resume = Resume.builder()
            .title(request.getTitle())
            .summary(request.getSummary())
            .userId(userId)
            .build();
        
        return resumeRepository.save(resume);
    }
}
```

### Ownership Validation

```java
public Resume updateResume(Long resumeId, UpdateRequest request, Long userId) {
    Resume resume = resumeRepository.findById(resumeId)
        .orElseThrow(() -> new ResumeNotFoundException(resumeId));
    
    if (!resume.getUserId().equals(userId)) {
        throw new ForbiddenException("You can only edit your own resume");
    }
    
    // Update fields
    if (request.getTitle() != null) {
        resume.setTitle(request.getTitle());
    }
    
    return resumeRepository.save(resume);
}
```

## ⚙️ Configuration

```yaml
server:
  port: 8084

spring:
  application:
    name: resume-service
  
  datasource:
    url: jdbc:postgresql://localhost:5436/resumeservice_db
    username: postgres
    password: postgres
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  
  flyway:
    enabled: true
    locations: classpath:db/migration
  
  clients:
    user-service:
      url: http://localhost:8082

logging:
  level:
    com.vlz.laborexchange_resumeservice: INFO
```

## 🧪 Testing

### Unit Tests

```java
@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {
    
    @Mock private ResumeRepository resumeRepository;
    @Mock private RoleServiceClient roleClient;
    
    @InjectMocks
    private ResumeService resumeService;
    
    @Test
    void createResume_JobSeeker_Success() {
        // Arrange
        Long userId = 123L;
        CreateResumeRequest request = new CreateResumeRequest("Developer", "Summary");
        
        when(roleClient.getUserRoleByUserId(userId)).thenReturn("JOB_SEEKER");
        when(resumeRepository.existsByUserId(userId)).thenReturn(false);
        when(resumeRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        
        // Act
        Resume result = resumeService.createResume(request, userId);
        
        // Assert
        assertNotNull(result);
        assertEquals("Developer", result.getTitle());
    }
    
    @Test
    void createResume_Employer_Forbidden() {
        // Arrange
        Long userId = 123L;
        when(roleClient.getUserRoleByUserId(userId)).thenReturn("EMPLOYER");
        
        // Act & Assert
        assertThrows(ForbiddenException.class, 
            () -> resumeService.createResume(new CreateResumeRequest(), userId));
    }
}
```

### Integration Tests

```bash
# Run tests
./gradlew test

# With coverage
./gradlew test jacocoTestReport
```

## 📊 Monitoring

```bash
# Health check
curl http://localhost:8084/actuator/health

# Metrics
curl http://localhost:8084/actuator/metrics

# Resume count
curl http://localhost:8084/actuator/metrics/resume.count
```

## 🐛 Troubleshooting

### Common Issues

**1. Database Connection**
```bash
docker-compose ps resume-db
docker-compose logs resume-db
```

**2. User Service Unavailable**
```bash
curl http://localhost:8082/actuator/health
```

**3. Role Validation Failed**
- Check user role in User Service
- Verify JWT contains correct role

## 🚀 Quick Start

```bash
# Clone repository
git clone https://github.com/yourusername/laborexchange.git
cd laborexchange/laborexchange-resumeservice

# Start database
docker-compose up -d resume-db

# Build and run
./gradlew clean build
./gradlew bootRun

# Verify
curl http://localhost:8084/actuator/health
```

## 🤝 Contributing

See [Contributing Guide](../CONTRIBUTING.md).

## 📄 License

MIT License - see [LICENSE](../LICENSE).

---

<div align="center">

**Made with ❤️ by the LaborExchange Team**

</div>
