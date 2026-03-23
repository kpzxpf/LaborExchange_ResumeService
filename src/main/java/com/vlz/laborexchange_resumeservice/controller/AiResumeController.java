package com.vlz.laborexchange_resumeservice.controller;

import com.vlz.laborexchange_resumeservice.dto.AiResumeRequestDto;
import com.vlz.laborexchange_resumeservice.dto.AiResumeResponseDto;
import com.vlz.laborexchange_resumeservice.service.AiResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI Resume", description = "Generate a resume draft using AI")
@RestController
@RequestMapping("/api/resumes/ai")
@RequiredArgsConstructor
public class AiResumeController {

    private final AiResumeService aiResumeService;

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Generate a resume draft with AI",
            description = "Generates a full Russian-language resume draft based on minimal user input. " +
                    "The result is NOT saved — it is returned as a prefilled draft for the user to review and edit. " +
                    "Only JOB_SEEKER role is allowed."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Generated resume draft",
                    content = @Content(schema = @Schema(implementation = AiResumeResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Forbidden — JOB_SEEKER role required")
    })
    @PostMapping("/generate")
    public AiResumeResponseDto generate(
            @Valid @RequestBody AiResumeRequestDto request,
            @Parameter(description = "Authenticated user ID (injected by Gateway)", required = true)
            @RequestHeader("X-User-Id") Long userId) {
        return aiResumeService.generate(request, userId);
    }
}
