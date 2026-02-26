package com.vlz.laborexchange_resumeservice.repository;

import com.vlz.laborexchange_resumeservice.entity.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findAllByUserId(Long userId);
    Page<Resume> findAllByIsPublishedTrue(Pageable pageable);
    List<Resume> findAllByIsPublishedTrue();

    @Query("SELECT r.title FROM Resume r WHERE r.id = :resumeId")
    String findResumeTitleByResumeId(@Param("resumeId") Long resumeId);
}
