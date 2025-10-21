package com.vlz.laborexchange_resumeservice.repository;

import com.vlz.laborexchange_resumeservice.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findAllByUserId(Long userId);
}
