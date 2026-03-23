package com.vlz.laborexchange_resumeservice.repository;

import com.vlz.laborexchange_resumeservice.entity.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {
    List<WorkExperience> findAllByResumeIdOrderByStartYearDescStartMonthDesc(Long resumeId);
}
