package com.vlz.laborexchange_resumeservice.service;

import com.vlz.laborexchange_resumeservice.dto.ResumeDto;
import com.vlz.laborexchange_resumeservice.entity.Resume;
import com.vlz.laborexchange_resumeservice.exception.InsufficientPermissionsException;
import com.vlz.laborexchange_resumeservice.repository.ResumeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeRepository repository;
    private final RoleRetryClient roleRetryClient;

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
                .isPublished(true)
                .build();

        return repository.save(resume);
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

        return repository.save(resume);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Resume with id " + id + " not found");
        }
        repository.deleteById(id);
    }

    @Transactional
    public void updatePublishStatus(Long id, Long userId, boolean status) {
        Resume resume = getById(id);
        validateOwnership(resume.getUserId(), userId);

        resume.setIsPublished(status);
    }

    private void checkForRequiredRole(Long userId) {
        String userRole = roleRetryClient.getUserRoleById(userId);

        if (!needRoleForCreate.equals(userRole)) {
            log.error("User {} tried to create a new Vacancy", userRole);
            throw new InsufficientPermissionsException(
                    "Only users with EMPLOYER role can create vacancies. Current role: " + userRole
            );
        }
    }

    private void validateOwnership(Long resumeUserId, Long userId) {
        if (!resumeUserId.equals(userId)) {
            log.error("Access denied: User {} is not owner of vacancy", userId);
            throw new InsufficientPermissionsException("You can only edit your own vacancies");
        }
    }
}