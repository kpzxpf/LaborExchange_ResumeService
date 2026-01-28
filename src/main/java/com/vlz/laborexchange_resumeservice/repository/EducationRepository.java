package com.vlz.laborexchange_resumeservice.repository;

import com.vlz.laborexchange_resumeservice.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findAllByResumeId(Long id);

}
