package com.vlz.laborexchange_resumeservice.controller;

import com.vlz.laborexchange_resumeservice.dto.ResumeDto;
import com.vlz.laborexchange_resumeservice.entity.Resume;
import com.vlz.laborexchange_resumeservice.mapper.ResumeMapper;
import com.vlz.laborexchange_resumeservice.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService service;
    private final ResumeMapper mapper;

    @GetMapping
    public List<ResumeDto> getAll() {
        return service.getAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResumeDto getById(@PathVariable Long id) {
        return mapper.toDto(service.getById(id));
    }

    @GetMapping("/user/{userId}")
    public List<ResumeDto> getByUser(@PathVariable Long userId) {
        return service.getByUserId(userId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @PostMapping
    public ResumeDto create(@Valid @RequestBody ResumeDto dto) {
        Resume entity = mapper.toEntity(dto);
        Resume saved = service.create(entity);
        return mapper.toDto(saved);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}