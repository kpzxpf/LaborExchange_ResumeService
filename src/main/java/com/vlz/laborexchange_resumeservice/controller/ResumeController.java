package com.vlz.laborexchange_resumeservice.controller;

import com.vlz.laborexchange_resumeservice.dto.ResumeDto;
import com.vlz.laborexchange_resumeservice.mapper.ResumeMapper;
import com.vlz.laborexchange_resumeservice.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Tag(name = "Resumes", description = "Resume CRUD, skill management, and publish control")
@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService service;
    private final ResumeMapper mapper;

    @Operation(summary = "Get all published resumes (paginated)")
    @ApiResponse(responseCode = "200", description = "Page of published resumes")
    @GetMapping
    public Page<ResumeDto> getAll(Pageable pageable) {
        return service.getAll(pageable).map(mapper::toDto);
    }

    @Operation(summary = "Get resume by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resume found", content = @Content(schema = @Schema(implementation = ResumeDto.class))),
            @ApiResponse(responseCode = "404", description = "Resume not found")
    })
    @GetMapping("/{id}")
    public ResumeDto getById(
            @Parameter(description = "Resume ID", required = true) @PathVariable Long id) {
        return mapper.toDto(service.getById(id));
    }

    @Operation(summary = "Get all resumes by user ID")
    @ApiResponse(responseCode = "200", description = "List of resumes", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResumeDto.class))))
    @GetMapping("/user/{userId}")
    public List<ResumeDto> getByUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
        return service.getByUserId(userId).stream().map(mapper::toDto).toList();
    }

    @Operation(summary = "Get resume title by ID", description = "Internal endpoint used by ApplicationService.")
    @ApiResponse(responseCode = "200", description = "Title string")
    @GetMapping("/{id}/title")
    public String getResumeTitle(
            @Parameter(description = "Resume ID", required = true) @PathVariable Long id) {
        return service.getResumeTitle(id);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Create a resume",
            description = "Only users with `JOB_SEEKER` role may create resumes. Role is verified via UserService. Publishes a ResumeIndexEvent to Kafka."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resume created", content = @Content(schema = @Schema(implementation = ResumeDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Forbidden — JOB_SEEKER role required")
    })
    @PostMapping
    public ResumeDto create(@Valid @RequestBody ResumeDto dto) {
        return mapper.toDto(service.create(dto));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get my resumes", description = "Returns all resumes for the authenticated user (X-User-Id).")
    @ApiResponse(responseCode = "200", description = "List of resumes", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResumeDto.class))))
    @GetMapping("/my")
    public List<ResumeDto> getMy(
            @Parameter(description = "Authenticated user ID (injected by Gateway)", required = true)
            @RequestHeader("X-User-Id") Long userId) {
        return service.getByUserId(userId).stream().map(mapper::toDto).toList();
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update a resume", description = "Only the resume owner may update. Re-indexes in Elasticsearch.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated resume", content = @Content(schema = @Schema(implementation = ResumeDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — not the resume owner"),
            @ApiResponse(responseCode = "404", description = "Resume not found")
    })
    @PutMapping("/{id}")
    public ResumeDto update(
            @Parameter(description = "Resume ID", required = true) @PathVariable Long id,
            @RequestBody @Valid ResumeDto resumeDto,
            @Parameter(description = "Authenticated user ID (injected by Gateway)", required = true)
            @RequestHeader("X-User-Id") Long userId) {
        resumeDto.setId(id);
        return mapper.toDto(service.update(resumeDto, userId));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Publish a resume", description = "Makes the resume visible in public search. Owner only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Published"),
            @ApiResponse(responseCode = "403", description = "Forbidden — not the resume owner")
    })
    @PatchMapping("/{id}/publish")
    public void publish(
            @Parameter(description = "Resume ID", required = true) @PathVariable Long id,
            @Parameter(description = "Authenticated user ID (injected by Gateway)", required = true)
            @RequestHeader("X-User-Id") Long userId) {
        service.updatePublishStatus(id, userId, true);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Unpublish a resume")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unpublished"),
            @ApiResponse(responseCode = "403", description = "Forbidden — not the resume owner")
    })
    @PatchMapping("/{id}/unpublish")
    public void unpublish(
            @Parameter(description = "Resume ID", required = true) @PathVariable Long id,
            @Parameter(description = "Authenticated user ID (injected by Gateway)", required = true)
            @RequestHeader("X-User-Id") Long userId) {
        service.updatePublishStatus(id, userId, false);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete a resume", description = "Owner only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden — not the resume owner"),
            @ApiResponse(responseCode = "404", description = "Resume not found")
    })
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "Resume ID", required = true) @PathVariable Long id,
            @Parameter(description = "Authenticated user ID (injected by Gateway)", required = true)
            @RequestHeader("X-User-Id") Long userId) {
        service.delete(id, userId);
    }

    @Operation(summary = "Get skill IDs on a resume")
    @ApiResponse(responseCode = "200", description = "Set of skill IDs")
    @GetMapping("/{id}/skills")
    public Set<Long> getSkillIds(
            @Parameter(description = "Resume ID", required = true) @PathVariable Long id) {
        return service.getSkillIds(id);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Add a skill to a resume")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Skill added"),
            @ApiResponse(responseCode = "403", description = "Forbidden — not the resume owner")
    })
    @PostMapping("/{id}/skills/{skillId}")
    public void addSkill(
            @Parameter(description = "Resume ID", required = true) @PathVariable Long id,
            @Parameter(description = "Skill ID", required = true) @PathVariable Long skillId,
            @Parameter(description = "Authenticated user ID (injected by Gateway)", required = true)
            @RequestHeader("X-User-Id") Long userId) {
        service.addSkill(id, skillId, userId);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Remove a skill from a resume")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Skill removed"),
            @ApiResponse(responseCode = "403", description = "Forbidden — not the resume owner")
    })
    @DeleteMapping("/{id}/skills/{skillId}")
    public void removeSkill(
            @Parameter(description = "Resume ID", required = true) @PathVariable Long id,
            @Parameter(description = "Skill ID", required = true) @PathVariable Long skillId,
            @Parameter(description = "Authenticated user ID (injected by Gateway)", required = true)
            @RequestHeader("X-User-Id") Long userId) {
        service.removeSkill(id, skillId, userId);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Replace all skills on a resume", description = "Re-indexes the resume in Elasticsearch after update.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Skills updated"),
            @ApiResponse(responseCode = "403", description = "Forbidden — not the resume owner")
    })
    @PutMapping("/{id}/skills")
    public void updateSkills(
            @Parameter(description = "Resume ID", required = true) @PathVariable Long id,
            @RequestBody Set<Long> skillIds,
            @Parameter(description = "Authenticated user ID (injected by Gateway)", required = true)
            @RequestHeader("X-User-Id") Long userId) {
        service.updateSkills(id, skillIds, userId);
    }

    @Operation(summary = "Reindex all published resumes", description = "Pushes all published resumes to Elasticsearch. Admin / maintenance operation.")
    @ApiResponse(responseCode = "200", description = "Reindex triggered")
    @PostMapping("/reindex")
    public void reindex() {
        service.reindexAll();
    }
}
