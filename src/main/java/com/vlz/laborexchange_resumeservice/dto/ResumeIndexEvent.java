package com.vlz.laborexchange_resumeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeIndexEvent {
    private Long id;
    private String title;
    private String summary;
    private Integer experienceYears;
    private Set<String> skills;
    private Set<String> institutions;
}
