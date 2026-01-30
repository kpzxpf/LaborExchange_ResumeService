package com.vlz.laborexchange_resumeservice.repository;

import com.vlz.laborexchange_resumeservice.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByResumeId(Long resumeId);
}