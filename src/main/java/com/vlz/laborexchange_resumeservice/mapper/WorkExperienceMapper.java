package com.vlz.laborexchange_resumeservice.mapper;

import com.vlz.laborexchange_resumeservice.dto.WorkExperienceDto;
import com.vlz.laborexchange_resumeservice.entity.WorkExperience;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkExperienceMapper {

    @Mapping(target = "resumeId", source = "resume.id")
    WorkExperienceDto toDto(WorkExperience entity);

    @Mapping(target = "resume", ignore = true)
    WorkExperience toEntity(WorkExperienceDto dto);
}
