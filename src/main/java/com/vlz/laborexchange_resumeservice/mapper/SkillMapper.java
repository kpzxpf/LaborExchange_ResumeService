package com.vlz.laborexchange_resumeservice.mapper;

import com.vlz.laborexchange_resumeservice.dto.SkillDto;
import com.vlz.laborexchange_resumeservice.entity.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {

    @Mapping(target = "resumeId", source = "resume.id")
    SkillDto toDto(Skill entity);

}
