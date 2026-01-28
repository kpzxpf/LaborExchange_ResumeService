package com.vlz.laborexchange_resumeservice.controller;

import com.vlz.laborexchange_resumeservice.dto.EducationDto;
import com.vlz.laborexchange_resumeservice.entity.Education;
import com.vlz.laborexchange_resumeservice.mapper.EducationMapper;
import com.vlz.laborexchange_resumeservice.service.EducationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/educations")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;
    private final EducationMapper mapper;

    @PostMapping
    public ResponseEntity<EducationDto> create(@RequestBody @Valid EducationDto dto) {
        Education saved = educationService.create(dto);

        return new ResponseEntity<>(mapper.toDto(saved), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EducationDto> update(@PathVariable Long id, @RequestBody @Valid EducationDto dto) {
        Education updated = educationService.update(id, dto);

        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @GetMapping("/resume/{resumeId}")
    public ResponseEntity<List<EducationDto>> getByResume(@PathVariable Long resumeId) {
        List<Education> list = educationService.getByResumeId(resumeId);

        return ResponseEntity.ok(list.stream()
                .map(mapper::toDto)
                .toList());
    }
}