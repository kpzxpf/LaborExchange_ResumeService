package com.vlz.laborexchange_resumeservice.service;

import com.vlz.laborexchange_resumeservice.dto.ResumeDto;
import com.vlz.laborexchange_resumeservice.entity.Resume;
import com.vlz.laborexchange_resumeservice.mapper.ResumeMapper;
import com.vlz.laborexchange_resumeservice.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeRepository repository;
    private final ResumeMapper mapper;

    public List<ResumeDto> getAll() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public List<ResumeDto> getByUserId(Long userId) {
        return repository.findAllByUserId(userId).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public ResumeDto create(ResumeDto dto) {
        Resume saved = repository.save(mapper.toEntity(dto));
        return mapper.toDto(saved);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}