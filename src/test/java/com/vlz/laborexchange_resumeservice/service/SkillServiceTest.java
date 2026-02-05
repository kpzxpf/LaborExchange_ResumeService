package com.vlz.laborexchange_resumeservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vlz.laborexchange_resumeservice.dto.SkillDto;
import com.vlz.laborexchange_resumeservice.entity.Resume;
import com.vlz.laborexchange_resumeservice.entity.Skill;
import com.vlz.laborexchange_resumeservice.repository.SkillRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private ResumeService resumeService;

    @InjectMocks
    private SkillService skillService;

    private final Long SKILL_ID = 1L;
    private final Long RESUME_ID = 5L;

    @Test
    @DisplayName("create: успех — создание навыка и привязка к резюме")
    void create_Success() {
        // Arrange
        SkillDto dto = SkillDto.builder()
                .name("Java")
                .resumeId(RESUME_ID)
                .build();

        Resume mockResume = new Resume();
        mockResume.setId(RESUME_ID);

        when(resumeService.getById(RESUME_ID)).thenReturn(mockResume);
        when(skillRepository.save(any(Skill.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Skill result = skillService.create(dto);

        // Assert
        assertNotNull(result);
        assertEquals("Java", result.getName());
        assertEquals(RESUME_ID, result.getResume().getId());
        verify(resumeService).getById(RESUME_ID);
        verify(skillRepository).save(any(Skill.class));
    }

    @Test
    @DisplayName("update: успех — изменение имени навыка")
    void update_Success() {
        // Arrange
        Skill existingSkill = Skill.builder().id(SKILL_ID).name("Old Skill").build();
        SkillDto updateDto = SkillDto.builder().name("New Skill").build();

        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(existingSkill));
        when(skillRepository.save(any(Skill.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Skill result = skillService.update(SKILL_ID, updateDto);

        // Assert
        assertEquals("New Skill", result.getName());
        verify(skillRepository).save(existingSkill);
    }

    @Test
    @DisplayName("findSkillById: ошибка — навык не найден")
    void findSkillById_NotFound_ThrowsException() {
        // Arrange
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> skillService.findSkillById(SKILL_ID));
    }

    @Test
    @DisplayName("delete: ошибка при удалении несуществующего навыка")
    void delete_NotFound_ThrowsException() {
        // Arrange
        when(skillRepository.existsById(SKILL_ID)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> skillService.delete(SKILL_ID));
        verify(skillRepository, never()).deleteById(anyLong());
    }
}