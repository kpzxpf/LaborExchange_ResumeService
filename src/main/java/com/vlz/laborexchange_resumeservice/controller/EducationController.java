package com.vlz.laborexchange_resumeservice.controller;

import com.vlz.laborexchange_resumeservice.dto.EducationDto;
import com.vlz.laborexchange_resumeservice.entity.Education;
import com.vlz.laborexchange_resumeservice.mapper.EducationMapper;
import com.vlz.laborexchange_resumeservice.service.EducationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Education", description = "Education records linked to a resume")
@RestController
@RequestMapping("/api/educations")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;
    private final EducationMapper mapper;

    @Operation(summary = "Add education record to a resume")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Education record created", content = @Content(schema = @Schema(implementation = EducationDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Resume not found")
    })
    @PostMapping
    public ResponseEntity<EducationDto> create(@RequestBody @Valid EducationDto dto) {
        Education saved = educationService.create(dto);
        return new ResponseEntity<>(mapper.toDto(saved), HttpStatus.CREATED);
    }

    @Operation(summary = "Update education record")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated", content = @Content(schema = @Schema(implementation = EducationDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Education record not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EducationDto> update(
            @Parameter(description = "Education record ID", required = true) @PathVariable Long id,
            @RequestBody @Valid EducationDto dto) {
        Education updated = educationService.update(id, dto);
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @Operation(summary = "Delete education record")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "Education record not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Education record ID", required = true) @PathVariable Long id) {
        educationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all education records for a resume")
    @ApiResponse(responseCode = "200", description = "List of education records",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = EducationDto.class))))
    @GetMapping("/resume/{resumeId}")
    public ResponseEntity<List<EducationDto>> getByResume(
            @Parameter(description = "Resume ID", required = true) @PathVariable Long resumeId) {
        List<Education> list = educationService.getByResumeId(resumeId);
        return ResponseEntity.ok(list.stream().map(mapper::toDto).toList());
    }
}
