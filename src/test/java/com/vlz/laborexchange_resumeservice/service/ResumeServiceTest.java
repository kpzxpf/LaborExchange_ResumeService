package com.vlz.laborexchange_resumeservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vlz.laborexchange_resumeservice.dto.ResumeDto;
import com.vlz.laborexchange_resumeservice.entity.Resume;
import com.vlz.laborexchange_resumeservice.exception.InsufficientPermissionsException;
import com.vlz.laborexchange_resumeservice.service.ContentModerationService;
import com.vlz.laborexchange_resumeservice.producer.ResumeIndexProducer;
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
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @Mock
    private ResumeRepository repository;
    @Mock
    private RoleRetryClient roleRetryClient;
    @Mock
    private ResumeIndexProducer resumeIndexProducer;
    @Mock
    private SkillRetryClient skillRetryClient;
    @Mock
    private TransactionTemplate transactionTemplate;

    @Mock
    private ContentModerationService contentModerationService;

    @InjectMocks
    private ResumeService resumeService;

    private final String REQUIRED_ROLE = "CANDIDATE";
    private final Long USER_ID = 1L;
    private final Long RESUME_ID = 100L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(resumeService, "needRoleForCreate", REQUIRED_ROLE);
        // TransactionTemplate.execute() must run the callback inline in unit tests
        lenient().when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });
    }

    @Nested
    @DisplayName("Обновление и права доступа (Update & Ownership)")
    class OwnershipTests {
        @Test
        @DisplayName("Успех: владелец может обновить резюме")
        void update_Success() {
            Resume existing = Resume.builder().id(RESUME_ID).userId(USER_ID).build();
            ResumeDto dto = ResumeDto.builder().id(RESUME_ID).title("New Title").build();

            when(skillRetryClient.getNameSkillsByIds(any())).thenReturn(Set.of());
            when(repository.findById(RESUME_ID)).thenReturn(Optional.of(existing));
            when(repository.save(any(Resume.class))).thenAnswer(i -> i.getArgument(0));

            Resume result = resumeService.update(dto, USER_ID);

            assertEquals("New Title", result.getTitle());
            verify(repository).save(existing);
        }

        @Test
        @DisplayName("Ошибка: попытка обновить чужое резюме")
        void update_NotOwner_ThrowsException() {
            Resume existing = Resume.builder().id(RESUME_ID).userId(USER_ID).build();
            ResumeDto dto = ResumeDto.builder().id(RESUME_ID).build();

            when(skillRetryClient.getNameSkillsByIds(any())).thenReturn(Set.of());
            when(repository.findById(RESUME_ID)).thenReturn(Optional.of(existing));

            assertThrows(InsufficientPermissionsException.class, () -> resumeService.update(dto, 999L));
        }
    }

    @Test
    @DisplayName("Удаление: выброс исключения если резюме нет")
    void delete_NotFound_ThrowsException() {
        when(repository.findById(RESUME_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> resumeService.delete(RESUME_ID, USER_ID));
    }
}
