package com.vlz.laborexchange_resumeservice.controller;

import com.vlz.laborexchange_resumeservice.dto.SkillDto;
import com.vlz.laborexchange_resumeservice.entity.Skill;
import com.vlz.laborexchange_resumeservice.mapper.SkillMapper;
import com.vlz.laborexchange_resumeservice.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;
    private final SkillMapper mapper;

    @PostMapping
    public ResponseEntity<SkillDto> create(@RequestBody @Valid SkillDto dto) {
        Skill saved = skillService.create(dto);
        return new ResponseEntity<>(mapper.toDto(saved), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillDto> update(@PathVariable Long id, @RequestBody @Valid SkillDto dto) {
        Skill updated = skillService.update(id, dto);
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @GetMapping("/resume/{resumeId}")
    public ResponseEntity<List<SkillDto>> getByResume(@PathVariable Long resumeId) {
        List<Skill> list = skillService.getByResumeId(resumeId);
        return ResponseEntity.ok(list.stream()
                .map(mapper::toDto)
                .toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        skillService.delete(id);
        return ResponseEntity.noContent().build();
    }
}