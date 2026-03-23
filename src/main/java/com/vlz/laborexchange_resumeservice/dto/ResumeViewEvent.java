package com.vlz.laborexchange_resumeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeViewEvent {
    private Long resumeId;
    private String resumeTitle;
    private Long ownerId;
    private Long viewerId;
    private String viewerRole;
    private LocalDateTime viewedAt;
}
