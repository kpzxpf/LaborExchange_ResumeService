package com.vlz.laborexchange_resumeservice.service;

import com.vlz.laborexchange_resumeservice.dto.EducationDto;
import com.vlz.laborexchange_resumeservice.entity.Education;
import com.vlz.laborexchange_resumeservice.entity.Resume;
import com.vlz.laborexchange_resumeservice.mapper.EducationMapper;
import com.vlz.laborexchange_resumeservice.repository.EducationRepository;
import com.vlz.laborexchange_resumeservice.repository.ResumeRepository;
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
    private final ResumeRepository resumeRepository;
    private final ResumeService resumeService;

    @Transactional(readOnly = true)
    public List<Education> getByResumeId(Long resumeId) {
        return educationRepository.findAllByResumeId(resumeId);
    }

    @Transactional
    public Education create(EducationDto educationDto) {
        Resume resume = resumeService.getById(educationDto.getResumeId());

        Education education = getEducationById(educationDto.getId());
        education.setResume(resume);

        return educationRepository.save(education);
    }

    @Transactional
    public Education update(Long id, EducationDto educationDto) {
        Education existing = getEducationById(id);

        existing.setInstitution(educationDto.getInstitution());
        existing.setDegree(educationDto.getDegree());
        existing.setFieldOfStudy(educationDto.getFieldOfStudy());
        existing.setStartYear(educationDto.getStartDate());
        existing.setEndYear(educationDto.getEndDate());

        return educationRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!educationRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. Education not found: " + id);
        }
        educationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Education getEducationById(Long id) {
        return educationRepository.findById(id).orElseThrow(() -> {
            log.info("education not found id {}", id);
            return new EntityNotFoundException("education not found id " + id);
        });
    }
}