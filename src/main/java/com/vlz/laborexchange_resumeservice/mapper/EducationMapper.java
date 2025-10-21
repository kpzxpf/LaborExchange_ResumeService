package com.vlz.laborexchange_resumeservice.mapper;

import com.vlz.laborexchange_resumeservice.dto.EducationDto;
import com.vlz.laborexchange_resumeservice.entity.Education;
import com.vlz.laborexchange_resumeservice.entity.Resume;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EducationMapper {

    @Mappings({
            @Mapping(target = "resumeId", source = "resume.id")
    })
    EducationDto toDto(Education entity);
}
