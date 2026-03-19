package com.vlz.laborexchange_resumeservice.service;

import com.vlz.laborexchange_resumeservice.dto.ResumeDto;
import com.vlz.laborexchange_resumeservice.dto.ResumeIndexEvent;
import com.vlz.laborexchange_resumeservice.entity.Resume;
import com.vlz.laborexchange_resumeservice.exception.InsufficientPermissionsException;
import com.vlz.laborexchange_resumeservice.producer.ResumeIndexProducer;
import com.vlz.laborexchange_resumeservice.repository.ResumeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeRepository repository;
    private final RoleRetryClient roleRetryClient;
    private final ResumeIndexProducer resumeIndexProducer;
    private final SkillRetryClient skillRetryClient;

    @Value("${spring.resume-create.role}")
    private String needRoleForCreate;

    @Transactional(readOnly = true)
    public Page<Resume> getAll(Pageable pageable) {
        return repository.findAllByIsPublishedTrue(pageable);
    }

    @Transactional(readOnly = true)
    public List<Resume> getByUserId(Long userId) {
        return repository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Resume getById(Long id) {
        return repository.findById(id).orElseThrow(() -> {
            log.error("Resume not found id {}", id);
            return new EntityNotFoundException("Resume not found id " + id);
        });
    }

    @Transactional
    public Resume create(ResumeDto resumeDto) {
        checkForRequiredRole(resumeDto.getUserId());
        Resume resume = Resume.builder()
                .title(resumeDto.getTitle())
                .userId(resumeDto.getUserId())
                .summary(resumeDto.getSummary())
                .experienceYears(resumeDto.getExperienceYears())
                .contactEmail(resumeDto.getContactEmail())
                .contactPhone(resumeDto.getContactPhone())
                .isPublished(resumeDto.getIsPublished() != null ? resumeDto.getIsPublished() : true)
                .build();

        Resume savedResume = repository.save(resume);
        resumeIndexProducer.send(ResumeIndexEvent.builder()
                .id(savedResume.getId())
                .title(savedResume.getTitle())
                .summary(savedResume.getSummary())
                .experienceYears(savedResume.getExperienceYears())
                .skills(skillRetryClient.getNameSkillsByIds(
                        savedResume.getSkillIds() != null ? List.copyOf(savedResume.getSkillIds()) : List.of()))
                .build());

        return savedResume;
    }

    @Transactional
    public Resume update(ResumeDto resumeDto, Long userId) {
        Resume resume = getById(resumeDto.getId());

        validateOwnership(resume.getUserId(), userId);

        resume.setTitle(resumeDto.getTitle());
        resume.setContactEmail(resumeDto.getContactEmail());
        resume.setContactPhone(resumeDto.getContactPhone());
        resume.setExperienceYears(resumeDto.getExperienceYears());
        resume.setSummary(resumeDto.getSummary());

        Resume saved = repository.save(resume);

        Set<String> institutions = saved.getEducation() != null
                ? saved.getEducation().stream().map(e -> e.getInstitution()).collect(Collectors.toSet())
                : Set.of();

        resumeIndexProducer.send(ResumeIndexEvent.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .summary(saved.getSummary())
                .experienceYears(saved.getExperienceYears())
                .skills(skillRetryClient.getNameSkillsByIds(
                        List.copyOf(saved.getSkillIds() != null ? saved.getSkillIds() : new HashSet<>())))
                .institutions(institutions)
                .build());

        return saved;
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Resume resume = getById(id);
        validateOwnership(resume.getUserId(), userId);
        repository.deleteById(id);
    }

    @Transactional
    public void updatePublishStatus(Long id, Long userId, boolean status) {
        Resume resume = getById(id);
        validateOwnership(resume.getUserId(), userId);

        resume.setIsPublished(status);
        repository.save(resume);
    }

    @Transactional(readOnly = true)
    public Set<Long> getSkillIds(Long resumeId) {
        Resume resume = getById(resumeId);
        return resume.getSkillIds() != null ? resume.getSkillIds() : new HashSet<>();
    }

    @Transactional
    public void addSkill(Long resumeId, Long skillId, Long userId) {
        Resume resume = getById(resumeId);
        validateOwnership(resume.getUserId(), userId);

        if (resume.getSkillIds() == null) {
            resume.setSkillIds(new HashSet<>());
        }
        resume.getSkillIds().add(skillId);
        repository.save(resume);
    }

    @Transactional
    public void removeSkill(Long resumeId, Long skillId, Long userId) {
        Resume resume = getById(resumeId);
        validateOwnership(resume.getUserId(), userId);
        if (resume.getSkillIds() != null) {
            resume.getSkillIds().remove(skillId);
        }
        repository.save(resume);
    }

    @Transactional
    public void updateSkills(Long resumeId, Set<Long> skillIds, Long userId) {
        Resume resume = getById(resumeId);
        validateOwnership(resume.getUserId(), userId);
        resume.setSkillIds(skillIds != null ? skillIds : new HashSet<>());
        Resume saved = repository.save(resume);

        Set<String> institutions = saved.getEducation() != null
                ? saved.getEducation().stream().map(e -> e.getInstitution()).collect(Collectors.toSet())
                : Set.of();

        resumeIndexProducer.send(ResumeIndexEvent.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .summary(saved.getSummary())
                .experienceYears(saved.getExperienceYears())
                .skills(skillRetryClient.getNameSkillsByIds(List.copyOf(saved.getSkillIds() != null ? saved.getSkillIds() : new HashSet<>())))
                .institutions(institutions)
                .build());
    }

    @Transactional(readOnly = true)
    public void reindexAll() {
        List<Resume> resumes = repository.findAllByIsPublishedTrue();
        log.info("Reindexing {} resumes", resumes.size());

        resumes.forEach(resume -> {
            Set<String> institutions = resume.getEducation() != null
                    ? resume.getEducation().stream().map(e -> e.getInstitution()).collect(Collectors.toSet())
                    : Set.of();

            resumeIndexProducer.send(ResumeIndexEvent.builder()
                    .id(resume.getId())
                    .title(resume.getTitle())
                    .summary(resume.getSummary())
                    .experienceYears(resume.getExperienceYears())
                    .skills(skillRetryClient.getNameSkillsByIds(
                            List.copyOf(resume.getSkillIds() != null ? resume.getSkillIds() : new HashSet<>())))
                    .institutions(institutions)
                    .build());
        });
    }

    @Transactional(readOnly = true)
    public String getResumeTitle(Long id) {
        return repository.findResumeTitleByResumeId(id);
    }

    private void checkForRequiredRole(Long userId) {
        String userRole = roleRetryClient.getUserRoleById(userId);

        if (!needRoleForCreate.equals(userRole)) {
            log.error("User {} tried to create a new Resume", userRole);
            throw new InsufficientPermissionsException(
                    "Only users with JOBSEEKER role can create resumes. Current role: " + userRole
            );
        }
    }

    private void validateOwnership(Long resumeUserId, Long userId) {
        if (!resumeUserId.equals(userId)) {
            log.error("Access denied: User {} is not owner of resume", userId);
            throw new InsufficientPermissionsException("You can only edit your own resumes");
        }
    }
}