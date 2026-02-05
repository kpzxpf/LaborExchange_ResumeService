package com.vlz.laborexchange_resumeservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vlz.laborexchange_resumeservice.dto.ResumeDto;
import com.vlz.laborexchange_resumeservice.entity.Resume;
import com.vlz.laborexchange_resumeservice.exception.InsufficientPermissionsException;
import com.vlz.laborexchange_resumeservice.repository.ResumeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @Mock
    private ResumeRepository repository;
    @Mock
    private RoleRetryClient roleRetryClient;

    @InjectMocks
    private ResumeService resumeService;

    private final String REQUIRED_ROLE = "CANDIDATE";
    private final Long USER_ID = 1L;
    private final Long RESUME_ID = 100L;

    @BeforeEach
    void setUp() {
        // Устанавливаем значение нужной роли из конфига
        ReflectionTestUtils.setField(resumeService, "needRoleForCreate", REQUIRED_ROLE);
    }

    @Nested
    @DisplayName("Создание резюме (Create)")
    class CreateTests {
        @Test
        @DisplayName("Успех: роль совпадает")
        void create_Success() {
            Resume resume = Resume.builder().userId(USER_ID).title("Java Dev").build();
            when(roleRetryClient.getUserRoleById(USER_ID)).thenReturn(REQUIRED_ROLE);
            when(repository.save(resume)).thenReturn(resume);

            Resume result = resumeService.create(resume);

            assertNotNull(result);
            verify(repository).save(resume);
        }

        @Test
        @DisplayName("Ошибка: неверная роль")
        void create_WrongRole_ThrowsException() {
            Resume resume = Resume.builder().userId(USER_ID).build();
            when(roleRetryClient.getUserRoleById(USER_ID)).thenReturn("EMPLOYER");

            assertThrows(InsufficientPermissionsException.class, () -> resumeService.create(resume));
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Обновление и права доступа (Update & Ownership)")
    class OwnershipTests {
        @Test
        @DisplayName("Успех: владелец может обновить резюме")
        void update_Success() {
            Resume existing = Resume.builder().id(RESUME_ID).userId(USER_ID).build();
            ResumeDto dto = ResumeDto.builder().id(RESUME_ID).title("New Title").build();

            when(repository.findById(RESUME_ID)).thenReturn(Optional.of(existing));
            when(repository.save(any(Resume.class))).thenAnswer(i -> i.getArgument(0));

            Resume result = resumeService.update(dto, USER_ID);

            assertEquals("New Title", result.getTitle());
            verify(repository).save(existing);
        }

        @Test
        @DisplayName("Ошибка: попытка обновить чужое резюме")
        void update_NotOwner_ThrowsException() {
            Resume existing = Resume.builder().id(RESUME_ID).userId(USER_ID).build(); // Владелец 1
            ResumeDto dto = ResumeDto.builder().id(RESUME_ID).build();

            when(repository.findById(RESUME_ID)).thenReturn(Optional.of(existing));

            assertThrows(InsufficientPermissionsException.class, () -> resumeService.update(dto, 999L));
        }
    }

    @Test
    @DisplayName("Удаление: выброс исключения если резюме нет")
    void delete_NotFound_ThrowsException() {
        when(repository.existsById(RESUME_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> resumeService.delete(RESUME_ID));
    }
}