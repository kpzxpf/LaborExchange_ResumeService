package com.vlz.laborexchange_resumeservice.service;

import com.vlz.laborexchange_resumeservice.dto.ResumeDto;
import com.vlz.laborexchange_resumeservice.service.ContentModerationService;
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
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private final TransactionTemplate transactionTemplate;
    private final ContentModerationService contentModerationService;

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
            log.error("Resume not found: id={}", id);
            return new EntityNotFoundException("Resume not found: " + id);
        });
    }

    @Transactional
    public Resume create(ResumeDto resumeDto) {
        checkForRequiredRole(resumeDto.getUserId());
        contentModerationService.check(resumeDto.getTitle(), resumeDto.getSummary());

        Resume resume = Resume.builder()
                .title(resumeDto.getTitle())
                .userId(resumeDto.getUserId())
                .firstName(resumeDto.getFirstName())
                .lastName(resumeDto.getLastName())
                .summary(resumeDto.getSummary())
                .experienceYears(resumeDto.getExperienceYears())
                .location(resumeDto.getLocation())
                .contactEmail(resumeDto.getContactEmail())
                .contactPhone(resumeDto.getContactPhone())
                .isPublished(resumeDto.getIsPublished() != null ? resumeDto.getIsPublished() : true)
                .expectedSalary(resumeDto.getExpectedSalary())
                .portfolioUrl(resumeDto.getPortfolioUrl())
                .languages(resumeDto.getLanguages())
                .certifications(resumeDto.getCertifications())
                .build();

        Resume savedResume = repository.save(resume);

        resumeIndexProducer.send(ResumeIndexEvent.builder()
                .id(savedResume.getId())
                .title(savedResume.getTitle())
                .summary(savedResume.getSummary())
                .experienceYears(savedResume.getExperienceYears())
                .expectedSalary(savedResume.getExpectedSalary())
                .location(savedResume.getLocation())
                .skills(skillRetryClient.getNameSkillsByIds(
                        savedResume.getSkillIds() != null ? List.copyOf(savedResume.getSkillIds()) : List.of()))
                .build());

        log.info("Resume created: id={} userId={}", savedResume.getId(), savedResume.getUserId());

        return savedResume;
    }

    public Resume update(ResumeDto resumeDto, Long userId) {
        contentModerationService.check(resumeDto.getTitle(), resumeDto.getSummary());

        // Fetch skills before the transaction to avoid holding a DB connection during HTTP retries
        List<Long> skillIdsList = resumeDto.getSkillIds() != null
                ? List.copyOf(resumeDto.getSkillIds()) : List.of();
        Set<String> skillNames = skillRetryClient.getNameSkillsByIds(skillIdsList);

        // TransactionTemplate keeps the transaction short — avoids Spring self-invocation proxy bypass
        Resume saved = transactionTemplate.execute(status -> {
            Resume resume = repository.findById(resumeDto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Resume not found: " + resumeDto.getId()));
            validateOwnership(resume.getUserId(), userId);

            resume.setTitle(resumeDto.getTitle());
            resume.setFirstName(resumeDto.getFirstName());
            resume.setLastName(resumeDto.getLastName());
            resume.setContactEmail(resumeDto.getContactEmail());
            resume.setContactPhone(resumeDto.getContactPhone());
            resume.setExperienceYears(resumeDto.getExperienceYears());
            resume.setSummary(resumeDto.getSummary());
            resume.setLocation(resumeDto.getLocation());
            resume.setExpectedSalary(resumeDto.getExpectedSalary());
            resume.setPortfolioUrl(resumeDto.getPortfolioUrl());
            resume.setLanguages(resumeDto.getLanguages());
            resume.setCertifications(resumeDto.getCertifications());

            return repository.save(resume);
        });

        // Publish after transaction commit — ensures index stays in sync with persisted state
        Set<String> institutions = saved.getEducation() != null
                ? saved.getEducation().stream().map(e -> e.getInstitution()).collect(Collectors.toSet())
                : Set.of();

        resumeIndexProducer.send(ResumeIndexEvent.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .summary(saved.getSummary())
                .experienceYears(saved.getExperienceYears())
                .expectedSalary(saved.getExpectedSalary())
                .location(saved.getLocation())
                .skills(skillNames)
                .institutions(institutions)
                .build());

        log.info("Resume updated: id={} userId={}", saved.getId(), userId);

        return saved;
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Resume resume = getById(id);
        validateOwnership(resume.getUserId(), userId);

        repository.deleteById(id);

        log.info("Resume deleted: id={} userId={}", id, userId);
    }

    @Transactional
    public void updatePublishStatus(Long id, Long userId, boolean status) {
        Resume resume = getById(id);
        validateOwnership(resume.getUserId(), userId);

        resume.setIsPublished(status);
        repository.save(resume);

        log.info("Resume id={} publish status set to {} by userId={}", id, status, userId);
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
                .expectedSalary(saved.getExpectedSalary())
                .location(saved.getLocation())
                .skills(skillRetryClient.getNameSkillsByIds(
                        List.copyOf(saved.getSkillIds() != null ? saved.getSkillIds() : new HashSet<>())))
                .institutions(institutions)
                .build());
    }

    @Transactional(readOnly = true)
    public void reindexAll() {
        List<Resume> resumes = repository.findAllByIsPublishedTrue();
        log.info("Reindexing {} resumes", resumes.size());

        // Collect all unique skill IDs in one batch call instead of N per-resume calls
        Set<Long> allSkillIds = resumes.stream()
                .filter(r -> r.getSkillIds() != null)
                .flatMap(r -> r.getSkillIds().stream())
                .collect(Collectors.toSet());

        Map<Long, String> skillIdToName = allSkillIds.isEmpty()
                ? Collections.emptyMap()
                : skillRetryClient.getSkillMapByIds(List.copyOf(allSkillIds));

        resumes.forEach(resume -> {
            Set<String> institutions = resume.getEducation() != null
                    ? resume.getEducation().stream().map(e -> e.getInstitution()).collect(Collectors.toSet())
                    : Set.of();

            Set<String> resumeSkillNames = resume.getSkillIds() != null
                    ? resume.getSkillIds().stream()
                            .map(id -> skillIdToName.getOrDefault(id, "Unknown"))
                            .collect(Collectors.toSet())
                    : Collections.emptySet();

            resumeIndexProducer.send(ResumeIndexEvent.builder()
                    .id(resume.getId())
                    .title(resume.getTitle())
                    .summary(resume.getSummary())
                    .experienceYears(resume.getExperienceYears())
                    .expectedSalary(resume.getExpectedSalary())
                    .location(resume.getLocation())
                    .skills(resumeSkillNames)
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
            log.warn("User {} with role {} attempted to create a resume without JOB_SEEKER role", userId, userRole);
            throw new InsufficientPermissionsException(
                    "Only users with JOBSEEKER role can create resumes. Current role: " + userRole);
        }
    }

    private void validateOwnership(Long resumeUserId, Long userId) {
        if (!resumeUserId.equals(userId)) {
            log.warn("Access denied: user {} is not the owner of resume owned by {}", userId, resumeUserId);
            throw new InsufficientPermissionsException("You can only edit your own resumes");
        }
    }
}
