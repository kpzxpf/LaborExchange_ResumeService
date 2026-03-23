package com.vlz.laborexchange_resumeservice.controller;

import com.vlz.laborexchange_resumeservice.dto.WorkExperienceDto;
import com.vlz.laborexchange_resumeservice.service.WorkExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Work Experience", description = "CRUD for resume work experience entries")
@RestController
@RequestMapping("/api/work-experience")
@RequiredArgsConstructor
public class WorkExperienceController {

    private final WorkExperienceService service;

    @Operation(summary = "Get all work experience for a resume")
    @GetMapping("/resume/{resumeId}")
    public List<WorkExperienceDto> getByResume(
            @PathVariable Long resumeId) {
        return service.getByResume(resumeId);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Add work experience entry")
    @PostMapping
    public WorkExperienceDto create(
            @Valid @RequestBody WorkExperienceDto dto,
            @Parameter(description = "Authenticated user ID (injected by Gateway)", required = true)
            @RequestHeader("X-User-Id") Long userId) {
        return service.create(dto, userId);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update work experience entry")
    @PutMapping("/{id}")
    public WorkExperienceDto update(
            @PathVariable Long id,
            @Valid @RequestBody WorkExperienceDto dto,
            @RequestHeader("X-User-Id") Long userId) {
        return service.update(id, dto, userId);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete work experience entry")
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        service.delete(id, userId);
    }
}
