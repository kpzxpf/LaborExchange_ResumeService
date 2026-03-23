package com.vlz.laborexchange_resumeservice.service;

import com.vlz.laborexchange_resumeservice.dto.WorkExperienceDto;
import com.vlz.laborexchange_resumeservice.entity.Resume;
import com.vlz.laborexchange_resumeservice.entity.WorkExperience;
import com.vlz.laborexchange_resumeservice.exception.InsufficientPermissionsException;
import com.vlz.laborexchange_resumeservice.mapper.WorkExperienceMapper;
import com.vlz.laborexchange_resumeservice.repository.ResumeRepository;
import com.vlz.laborexchange_resumeservice.repository.WorkExperienceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkExperienceServiceTest {

    @Mock
    private WorkExperienceRepository repository;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private WorkExperienceMapper mapper;

    @InjectMocks
    private WorkExperienceService service;

    private static final Long USER_ID = 10L;
    private static final Long OTHER_USER_ID = 99L;
    private static final Long RESUME_ID = 1L;
    private static final Long EXP_ID = 100L;

    private Resume resume;
    private WorkExperience experience;

    @BeforeEach
    void setUp() {
        resume = Resume.builder().id(RESUME_ID).userId(USER_ID).build();

        experience = WorkExperience.builder()
                .id(EXP_ID)
                .resume(resume)
                .companyName("Acme Corp")
                .position("Developer")
                .startYear(2020)
                .startMonth(1)
                .build();
    }

    @Nested
    @DisplayName("create")
    class CreateTests {

        @Test
        @DisplayName("успех: создаёт запись для владельца резюме")
        void create_Success() {
            WorkExperienceDto dto = WorkExperienceDto.builder()
                    .resumeId(RESUME_ID).companyName("Acme Corp").position("Developer").build();
            WorkExperienceDto expected = WorkExperienceDto.builder().id(EXP_ID).build();

            when(resumeRepository.findById(RESUME_ID)).thenReturn(Optional.of(resume));
            when(mapper.toEntity(dto)).thenReturn(experience);
            when(repository.save(experience)).thenReturn(experience);
            when(mapper.toDto(experience)).thenReturn(expected);

            WorkExperienceDto result = service.create(dto, USER_ID);

            assertThat(result.getId()).isEqualTo(EXP_ID);
            verify(repository).save(experience);
        }

        @Test
        @DisplayName("ошибка: резюме не найдено")
        void create_ResumeNotFound_ThrowsException() {
            WorkExperienceDto dto = WorkExperienceDto.builder().resumeId(RESUME_ID).build();
            when(resumeRepository.findById(RESUME_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.create(dto, USER_ID))
                    .isInstanceOf(EntityNotFoundException.class);
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("ошибка: пользователь не является владельцем резюме")
        void create_NotOwner_ThrowsException() {
            WorkExperienceDto dto = WorkExperienceDto.builder().resumeId(RESUME_ID).build();
            when(resumeRepository.findById(RESUME_ID)).thenReturn(Optional.of(resume));

            assertThatThrownBy(() -> service.create(dto, OTHER_USER_ID))
                    .isInstanceOf(InsufficientPermissionsException.class);
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTests {

        @Test
        @DisplayName("успех: владелец обновляет опыт работы")
        void update_Success() {
            WorkExperienceDto dto = WorkExperienceDto.builder()
                    .companyName("New Corp").position("Senior Dev")
                    .startYear(2021).startMonth(3).build();
            WorkExperienceDto expected = WorkExperienceDto.builder().id(EXP_ID).companyName("New Corp").build();

            when(repository.findById(EXP_ID)).thenReturn(Optional.of(experience));
            when(repository.save(experience)).thenReturn(experience);
            when(mapper.toDto(experience)).thenReturn(expected);

            WorkExperienceDto result = service.update(EXP_ID, dto, USER_ID);

            assertThat(result.getCompanyName()).isEqualTo("New Corp");
            verify(repository).save(experience);
        }

        @Test
        @DisplayName("ошибка: опыт работы не найден")
        void update_NotFound_ThrowsException() {
            when(repository.findById(EXP_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(EXP_ID, new WorkExperienceDto(), USER_ID))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("ошибка: чужой пользователь не может изменить запись")
        void update_NotOwner_ThrowsException() {
            when(repository.findById(EXP_ID)).thenReturn(Optional.of(experience));

            assertThatThrownBy(() -> service.update(EXP_ID, new WorkExperienceDto(), OTHER_USER_ID))
                    .isInstanceOf(InsufficientPermissionsException.class);
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTests {

        @Test
        @DisplayName("успех: владелец удаляет запись")
        void delete_Success() {
            when(repository.findById(EXP_ID)).thenReturn(Optional.of(experience));

            service.delete(EXP_ID, USER_ID);

            verify(repository).deleteById(EXP_ID);
        }

        @Test
        @DisplayName("ошибка: опыт работы не найден")
        void delete_NotFound_ThrowsException() {
            when(repository.findById(EXP_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.delete(EXP_ID, USER_ID))
                    .isInstanceOf(EntityNotFoundException.class);
            verify(repository, never()).deleteById(any());
        }

        @Test
        @DisplayName("ошибка: чужой пользователь не может удалить запись")
        void delete_NotOwner_ThrowsException() {
            when(repository.findById(EXP_ID)).thenReturn(Optional.of(experience));

            assertThatThrownBy(() -> service.delete(EXP_ID, OTHER_USER_ID))
                    .isInstanceOf(InsufficientPermissionsException.class);
            verify(repository, never()).deleteById(any());
        }
    }

    @Test
    @DisplayName("getByResume: возвращает список опыта работы для резюме")
    void getByResume_ReturnsList() {
        WorkExperienceDto dto = WorkExperienceDto.builder().id(EXP_ID).build();

        when(repository.findAllByResumeIdOrderByStartYearDescStartMonthDesc(RESUME_ID))
                .thenReturn(List.of(experience));
        when(mapper.toDto(experience)).thenReturn(dto);

        List<WorkExperienceDto> result = service.getByResume(RESUME_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(EXP_ID);
    }
}
