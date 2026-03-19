# Resume Service

Manages job seeker resumes and education records for LaborExchange.

## Overview

| Property | Value |
|---|---|
| Port | **8084** |
| Base paths | `/api/resumes`, `/api/educations` |
| Database | PostgreSQL (`resumedb`, port 5436) |
| Swagger UI | http://localhost:8084/swagger-ui.html |
| Prometheus metrics | http://localhost:8084/actuator/prometheus |

## API Endpoints

### Resumes (`/api/resumes`)

| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/api/resumes` | No | Get published resumes (paginated) |
| `GET` | `/api/resumes/{id}` | No | Get resume by ID |
| `GET` | `/api/resumes/user/{userId}` | No | Get resumes by user |
| `GET` | `/api/resumes/{id}/title` | No | Get resume title |
| `POST` | `/api/resumes` | JOB_SEEKER only | Create resume |
| `GET` | `/api/resumes/my` | X-User-Id | Get my resumes |
| `PUT` | `/api/resumes/{id}` | X-User-Id (owner) | Update resume |
| `PATCH` | `/api/resumes/{id}/publish` | X-User-Id (owner) | Publish resume |
| `PATCH` | `/api/resumes/{id}/unpublish` | X-User-Id (owner) | Unpublish resume |
| `DELETE` | `/api/resumes/{id}` | X-User-Id (owner) | Delete resume |
| `GET` | `/api/resumes/{id}/skills` | No | Get skill IDs |
| `POST` | `/api/resumes/{id}/skills/{skillId}` | X-User-Id (owner) | Add skill |
| `DELETE` | `/api/resumes/{id}/skills/{skillId}` | X-User-Id (owner) | Remove skill |
| `PUT` | `/api/resumes/{id}/skills` | X-User-Id (owner) | Replace all skills |
| `POST` | `/api/resumes/reindex` | No | Reindex all to Elasticsearch |

### Education (`/api/educations`)

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/educations` | Add education record |
| `PUT` | `/api/educations/{id}` | Update education record |
| `DELETE` | `/api/educations/{id}` | Delete education record |
| `GET` | `/api/educations/resume/{resumeId}` | Get all records for a resume |

## Kafka Events

| Topic | Trigger |
|---|---|
| `indexing-resume` | Resume created, updated, published, or skills changed |

## Access Control

- Only `JOB_SEEKER` role may create resumes (verified via UserService Feign call)
- Only the resume owner may update, delete, or manage skills

## Running locally

```bash
./gradlew bootRun
```

Requires: PostgreSQL (port 5436), Kafka.
