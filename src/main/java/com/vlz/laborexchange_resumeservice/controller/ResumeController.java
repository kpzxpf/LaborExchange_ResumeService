package com.vlz.laborexchange_resumeservice.controller;

import com.vlz.laborexchange_resumeservice.dto.ResumeDto;
import com.vlz.laborexchange_resumeservice.mapper.ResumeMapper;
import com.vlz.laborexchange_resumeservice.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService service;
    private final ResumeMapper mapper;

    @GetMapping
    public Page<ResumeDto> getAll(Pageable pageable) {
        return service.getAll(pageable).map(mapper::toDto);
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

    @GetMapping("/{id}/title")
    public String getResumeTitle(@PathVariable Long id) {
        return service.getResumeTitle(id);
    }

    @PostMapping
    public ResumeDto create(@Valid @RequestBody ResumeDto dto) {

        return mapper.toDto(service.create(dto));
    }

    @PostMapping("/update")
    public ResumeDto update(@RequestBody @Valid ResumeDto resumeDto,
                             @RequestHeader("X-User-Id") Long userId) {
        return mapper.toDto(service.update(resumeDto, userId));
    }

    @PatchMapping("/{id}/publish")
    public void publish(@PathVariable Long id,
                        @RequestHeader("X-User-Id") Long userId) {
        service.updatePublishStatus(id, userId, true);
    }

    @PatchMapping("/{id}/unpublish")
    public void unpublish(@PathVariable Long id,
                          @RequestHeader("X-User-Id") Long userId) {
        service.updatePublishStatus(id, userId, false);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}/skills")
    public Set<Long> getSkillIds(@PathVariable Long id) {
        return service.getSkillIds(id);
    }

    @PostMapping("/{id}/skills/{skillId}")
    public void addSkill(@PathVariable Long id,
                         @PathVariable Long skillId,
                         @RequestHeader("X-User-Id") Long userId) {
        service.addSkill(id, skillId, userId);
    }

    @DeleteMapping("/{id}/skills/{skillId}")
    public void removeSkill(@PathVariable Long id,
                            @PathVariable Long skillId,
                            @RequestHeader("X-User-Id") Long userId) {
        service.removeSkill(id, skillId, userId);
    }


    @PutMapping("/{id}/skills")
    public void updateSkills(@PathVariable Long id,
                             @RequestBody Set<Long> skillIds,
                             @RequestHeader("X-User-Id") Long userId) {
        service.updateSkills(id, skillIds, userId);
    }
}