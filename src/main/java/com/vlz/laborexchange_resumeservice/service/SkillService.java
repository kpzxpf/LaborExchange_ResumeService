package com.vlz.laborexchange_resumeservice.service;

import com.vlz.laborexchange_resumeservice.dto.SkillDto;
import com.vlz.laborexchange_resumeservice.entity.Skill;
import com.vlz.laborexchange_resumeservice.repository.SkillRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final ResumeService resumeService;

    @Transactional
    public Skill create(SkillDto dto) {
        Skill skill = Skill.builder()
                .name(dto.getName())
                .resume(resumeService.getById(dto.getResumeId()))
                .build();

        return skillRepository.save(skill);
    }

    @Transactional
    public Skill update(Long id, SkillDto dto) {
        log.info("Updating skill with id: {}", id);

        Skill existingSkill = findSkillById(id);

        existingSkill.setName(dto.getName());


        return skillRepository.save(existingSkill);
    }

    @Transactional(readOnly = true)
    public List<Skill> getByResumeId(Long resumeId) {
        log.info("Fetching skills for resume id: {}", resumeId);
        return skillRepository.findByResumeId(resumeId);
    }

    @Transactional(readOnly = true)
    public Skill findSkillById(Long id) {
        return skillRepository.findById(id).orElseThrow(() ->  {
                    log.info("Skill with id {} not found", id);
                    return new EntityNotFoundException("Skill not found with id: " + id);
                });
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting skill with id: {}", id);
        if (!skillRepository.existsById(id)) {
            throw new EntityNotFoundException("Skill not found with id: " + id);
        }
        skillRepository.deleteById(id);
    }
}