package com.vlz.laborexchange_resumeservice.service;

import com.vlz.laborexchange_resumeservice.dto.WorkExperienceDto;
import com.vlz.laborexchange_resumeservice.entity.Resume;
import com.vlz.laborexchange_resumeservice.entity.WorkExperience;
import com.vlz.laborexchange_resumeservice.exception.InsufficientPermissionsException;
import com.vlz.laborexchange_resumeservice.mapper.WorkExperienceMapper;
import com.vlz.laborexchange_resumeservice.repository.ResumeRepository;
import com.vlz.laborexchange_resumeservice.repository.WorkExperienceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkExperienceService {

    private final WorkExperienceRepository repository;
    private final ResumeRepository resumeRepository;
    private final WorkExperienceMapper mapper;

    @Transactional(readOnly = true)
    public List<WorkExperienceDto> getByResume(Long resumeId) {
        return repository.findAllByResumeIdOrderByStartYearDescStartMonthDesc(resumeId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public WorkExperienceDto create(WorkExperienceDto dto, Long userId) {
        Resume resume = getResumeAndValidateOwner(dto.getResumeId(), userId);

        WorkExperience entity = mapper.toEntity(dto);
        entity.setResume(resume);
        entity.setIsCurrent(dto.getIsCurrent() != null ? dto.getIsCurrent() : false);

        WorkExperienceDto result = mapper.toDto(repository.save(entity));

        log.info("WorkExperience created: resumeId={} userId={}", dto.getResumeId(), userId);

        return result;
    }

    @Transactional
    public WorkExperienceDto update(Long id, WorkExperienceDto dto, Long userId) {
        WorkExperience existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkExperience not found: " + id));
        validateOwnership(existing.getResume().getUserId(), userId);

        existing.setCompanyName(dto.getCompanyName());
        existing.setPosition(dto.getPosition());
        existing.setDescription(dto.getDescription());
        existing.setStartYear(dto.getStartYear());
        existing.setStartMonth(dto.getStartMonth());
        existing.setEndYear(dto.getEndYear());
        existing.setEndMonth(dto.getEndMonth());
        existing.setIsCurrent(dto.getIsCurrent() != null ? dto.getIsCurrent() : false);

        WorkExperienceDto result = mapper.toDto(repository.save(existing));

        log.info("WorkExperience updated: id={} userId={}", id, userId);

        return result;
    }

    @Transactional
    public void delete(Long id, Long userId) {
        WorkExperience existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkExperience not found: " + id));
        validateOwnership(existing.getResume().getUserId(), userId);

        repository.deleteById(id);

        log.info("WorkExperience deleted: id={} userId={}", id, userId);
    }

    private Resume getResumeAndValidateOwner(Long resumeId, Long userId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found: " + resumeId));
        validateOwnership(resume.getUserId(), userId);
        return resume;
    }

    private void validateOwnership(Long resumeUserId, Long userId) {
        if (!resumeUserId.equals(userId)) {
            throw new InsufficientPermissionsException("You can only edit your own resumes");
        }
    }
}
