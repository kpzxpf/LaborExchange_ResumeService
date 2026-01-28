package com.vlz.laborexchange_resumeservice.service;

import com.vlz.laborexchange_resumeservice.dto.ResumeDto;
import com.vlz.laborexchange_resumeservice.entity.Resume;
import com.vlz.laborexchange_resumeservice.mapper.ResumeMapper;
import com.vlz.laborexchange_resumeservice.repository.ResumeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeRepository repository;

    @Transactional(readOnly = true)
    public List<Resume> getAll() {
        return repository.findAll();
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
    public Resume create(Resume resume) {
        return repository.save(resume);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Resume with id " + id + " not found");
        }
        repository.deleteById(id);
    }
}