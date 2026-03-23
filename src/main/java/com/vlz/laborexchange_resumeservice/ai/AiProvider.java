package com.vlz.laborexchange_resumeservice.ai;

import com.vlz.laborexchange_resumeservice.dto.AiResumeRequestDto;
import com.vlz.laborexchange_resumeservice.dto.AiResumeResponseDto;

/**
 * Strategy interface for AI resume generation.
 * Swap providers by changing ai.provider in application.yaml
 * and providing a new implementation bean.
 */
public interface AiProvider {
    AiResumeResponseDto generate(AiResumeRequestDto request);
}
