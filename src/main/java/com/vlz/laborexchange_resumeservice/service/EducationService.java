package com.vlz.laborexchange_resumeservice.service;

import com.vlz.laborexchange_resumeservice.dto.EducationDto;
import com.vlz.laborexchange_resumeservice.entity.Education;
import com.vlz.laborexchange_resumeservice.repository.EducationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EducationService {

    private final EducationRepository educationRepository;
    private final ResumeService resumeService;

    @Transactional(readOnly = true)
    public List<Education> getByResumeId(Long resumeId) {
        return educationRepository.findAllByResumeId(resumeId);
    }

    @Transactional
    public Education create(EducationDto educationDto) {
        Education education = Education.builder()
                .degree(educationDto.getDegree())
                .institution(educationDto.getInstitution())
                .fieldOfStudy(educationDto.getFieldOfStudy())
                .startYear(educationDto.getStartYear())
                .endYear(educationDto.getEndYear())
                .resume(resumeService.getById(educationDto.getResumeId()))
                .build();

        Education saved = educationRepository.save(education);

        log.info("Education created: id={} resumeId={}", saved.getId(), educationDto.getResumeId());

        return saved;
    }

    @Transactional
    public Education update(Long id, EducationDto educationDto) {
        Education existing = getEducationById(id);

        existing.setInstitution(educationDto.getInstitution());
        existing.setDegree(educationDto.getDegree());
        existing.setFieldOfStudy(educationDto.getFieldOfStudy());
        existing.setStartYear(educationDto.getStartYear());
        existing.setEndYear(educationDto.getEndYear());

        Education saved = educationRepository.save(existing);

        log.info("Education updated: id={}", id);

        return saved;
    }

    @Transactional
    public void delete(Long id) {
        if (!educationRepository.existsById(id)) {
            throw new EntityNotFoundException("Education not found: " + id);
        }

        educationRepository.deleteById(id);

        log.info("Education deleted: id={}", id);
    }

    @Transactional(readOnly = true)
    public Education getEducationById(Long id) {
        return educationRepository.findById(id).orElseThrow(() -> {
            log.error("Education not found: id={}", id);
            return new EntityNotFoundException("Education not found: " + id);
        });
    }
}
