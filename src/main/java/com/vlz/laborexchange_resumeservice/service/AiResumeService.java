package com.vlz.laborexchange_resumeservice.service;

import com.vlz.laborexchange_resumeservice.ai.AiProvider;
import com.vlz.laborexchange_resumeservice.dto.AiResumeRequestDto;
import com.vlz.laborexchange_resumeservice.dto.AiResumeResponseDto;
import com.vlz.laborexchange_resumeservice.exception.InsufficientPermissionsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiResumeService {

    private final AiProvider aiProvider;
    private final RoleRetryClient roleRetryClient;

    @Value("${spring.resume-create.role}")
    private String requiredRole;

    public AiResumeResponseDto generate(AiResumeRequestDto request, Long userId) {
        String userRole = roleRetryClient.getUserRoleById(userId);
        if (!requiredRole.equals(userRole)) {
            throw new InsufficientPermissionsException(
                    "Only JOB_SEEKER can generate resumes. Current role: " + userRole);
        }

        log.info("Generating AI resume for userId={}, jobTitle={}", userId, request.getJobTitle());
        return aiProvider.generate(request);
    }
}
