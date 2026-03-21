# Resume Service

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.6-brightgreen?logo=springboot)
![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Port](https://img.shields.io/badge/port-8084-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-resumedb-336791?logo=postgresql)
![Kafka](https://img.shields.io/badge/Kafka-indexing--resume-231F20?logo=apachekafka)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

Microservice for managing job seeker resumes, education records, and skills, with Elasticsearch indexing via Kafka.

## Table of Contents

- [Overview](#overview)
- [API Endpoints](#api-endpoints)
- [Data Models](#data-models)
- [Kafka Events](#kafka-events)
- [Authorization](#authorization)
- [Configuration](#configuration)
- [Running Locally](#running-locally)

## Overview

| Property | Value |
|---|---|
| Port | **8084** |
| Base paths | `/api/resumes`, `/api/educations` |
| Database | PostgreSQL — `resumedb` (port 5436) |
| Migrations | Flyway |
| Swagger UI | `http://localhost:8084/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8084/v3/api-docs` |
| Prometheus | `http://localhost:8084/actuator/prometheus` |

## API Endpoints

### Resumes — `/api/resumes`

| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/` | No | Get all published resumes (paginated) |
| `GET` | `/{id}` | No | Get resume by ID |
| `GET` | `/user/{userId}` | No | Get resumes by user ID |
| `GET` | `/{id}/title` | No | Get resume title (internal) |
| `GET` | `/my` | JWT | Get my resumes |
| `POST` | `/` | `JOB_SEEKER` | Create resume |
| `PUT` | `/{id}` | JWT | Update resume (owner only) |
| `PATCH` | `/{id}/publish` | JWT | Publish resume |
| `PATCH` | `/{id}/unpublish` | JWT | Unpublish resume |
| `DELETE` | `/{id}` | JWT | Delete resume (owner only) |
| `GET` | `/{id}/skills` | No | Get skill IDs |
| `POST` | `/{id}/skills/{skillId}` | JWT | Add skill |
| `DELETE` | `/{id}/skills/{skillId}` | JWT | Remove skill |
| `PUT` | `/{id}/skills` | JWT | Replace all skills |
| `POST` | `/reindex` | No | Reindex all resumes (maintenance) |

### Education — `/api/educations`

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/` | JWT | Add education record |
| `PUT` | `/{id}` | JWT | Update education record |
| `DELETE` | `/{id}` | JWT | Delete education record |
| `GET` | `/resume/{resumeId}` | No | Get education for resume |

## Data Models

### ResumeDto

| Field | Type | Constraints |
|---|---|---|
| `id` | Long | Auto-generated |
| `userId` | Long | Required |
| `title` | String | Max 255 chars, required |
| `summary` | String | Max 5000 chars |
| `experienceYears` | Integer | ≥ 0 |
| `contactEmail` | String | Valid email, max 320 chars |
| `contactPhone` | String | 7–20 chars |
| `isPublished` | Boolean | Default false |

### EducationDto

| Field | Type | Constraints |
|---|---|---|
| `id` | Long | Auto-generated |
| `resumeId` | Long | Required |
| `institution` | String | Max 255 chars, required |
| `degree` | String | Max 255 chars, required |
| `fieldOfStudy` | String | Max 255 chars, required |
| `startYear` | Integer | 1900–2100, required |
| `endYear` | Integer | 1900–2100, null if currently studying |

## Kafka Events

| Topic | Event | Trigger |
|---|---|---|
| `indexing-resume` | `ResumeIndexEvent` | Create, update, skill change, publish |

`ResumeIndexEvent` carries: id, userId, title, summary, experienceYears, skills (names), institutions.

## Authorization

Write operations require authentication. The API Gateway injects `X-User-Id` from the JWT. The service enforces ownership — only the resume owner may modify it.

## Configuration

| Property | Default | Description |
|---|---|---|
| `server.port` | `8084` | HTTP port |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5436/resumedb` | Database URL |
| `spring.kafka.bootstrap-servers` | `localhost:9092` | Kafka brokers |

## Running Locally

```bash
./gradlew bootRun
```

Requires PostgreSQL on port 5436 (`resumedb`) and Kafka.
