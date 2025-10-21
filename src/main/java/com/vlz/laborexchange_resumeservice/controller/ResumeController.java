package com.vlz.laborexchange_resumeservice.controller;

import com.vlz.laborexchange_resumeservice.dto.ResumeDto;
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

    @GetMapping
    public List<ResumeDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/user/{userId}")
    public List<ResumeDto> getByUser(@PathVariable Long userId) {
        return service.getByUserId(userId);
    }

    @PostMapping
    public ResumeDto create(@Valid @RequestBody ResumeDto resume) {
        return service.create(resume);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
