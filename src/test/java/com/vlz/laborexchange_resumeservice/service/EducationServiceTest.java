package com.vlz.laborexchange_resumeservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vlz.laborexchange_resumeservice.dto.EducationDto;
import com.vlz.laborexchange_resumeservice.entity.Education;
import com.vlz.laborexchange_resumeservice.entity.Resume;
import com.vlz.laborexchange_resumeservice.repository.EducationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EducationServiceTest {

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private ResumeService resumeService;

    @InjectMocks
    private EducationService educationService;

    private final Long EDUCATION_ID = 1L;
    private final Long RESUME_ID = 10L;

    @Test
    @DisplayName("create: создание записи об образовании с привязкой к резюме")
    void create_Success() {
        // Arrange
        EducationDto dto = EducationDto.builder()
                .resumeId(RESUME_ID)
                .institution("MIT")
                .degree("Bachelor")
                .build();

        Resume mockResume = new Resume();
        mockResume.setId(RESUME_ID);

        when(resumeService.getById(RESUME_ID)).thenReturn(mockResume);
        when(educationRepository.save(any(Education.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Education result = educationService.create(dto);

        // Assert
        assertNotNull(result);
        assertEquals("MIT", result.getInstitution());
        assertEquals(RESUME_ID, result.getResume().getId());
        verify(resumeService).getById(RESUME_ID);
        verify(educationRepository).save(any(Education.class));
    }

    @Test
    @DisplayName("update: обновление существующих полей")
    void update_Success() {
        // Arrange
        Education existing = Education.builder()
                .id(EDUCATION_ID)
                .institution("Old Univ")
                .build();

        EducationDto updateDto = EducationDto.builder()
                .institution("New Univ")
                .degree("Master")
                .build();

        when(educationRepository.findById(EDUCATION_ID)).thenReturn(Optional.of(existing));
        when(educationRepository.save(any(Education.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Education result = educationService.update(EDUCATION_ID, updateDto);

        // Assert
        assertEquals("New Univ", result.getInstitution());
        assertEquals("Master", result.getDegree());
        verify(educationRepository).save(existing);
    }

    @Test
    @DisplayName("delete: ошибка при удалении несуществующей записи")
    void delete_NotFound_ThrowsException() {
        // Arrange
        when(educationRepository.existsById(EDUCATION_ID)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> educationService.delete(EDUCATION_ID));
        verify(educationRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("getEducationById: выброс исключения при отсутствии записи")
    void getEducationById_NotFound_ThrowsException() {
        // Arrange
        when(educationRepository.findById(EDUCATION_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> educationService.getEducationById(EDUCATION_ID));
    }
}