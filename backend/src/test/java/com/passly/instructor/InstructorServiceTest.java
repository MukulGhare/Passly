package com.passly.instructor;

import com.passly.common.exception.ResourceNotFoundException;
import com.passly.common.storage.StorageService;
import com.passly.instructor.dto.InstructorProfileResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Unit tests for InstructorService.
 *
 * The repository and mapper are mocked so we only test the service logic —
 * no database, no Spring context.
 */
@ExtendWith(MockitoExtension.class)
class InstructorServiceTest {

    @Mock InstructorProfileRepository repository;
    @Mock InstructorProfileMapper      mapper;
    @Mock StorageService               storageService;

    @InjectMocks InstructorService instructorService;

    private static final UUID USER_ID    = UUID.randomUUID();
    private static final UUID PROFILE_ID = UUID.randomUUID();

    /** Builds a minimal InstructorProfile with an ID set (simulates a DB-loaded entity). */
    private InstructorProfile buildProfile() {
        InstructorProfile p = InstructorProfile.builder()
                .firstName("John")
                .lastName("Doe")
                .build();
        p.setId(PROFILE_ID);
        return p;
    }

    /** Builds a minimal InstructorProfileResponse (it's a record, cannot be mocked). */
    private InstructorProfileResponse buildResponse() {
        return new InstructorProfileResponse(
                PROFILE_ID, USER_ID, "John", "Doe",
                null, null, null, null, null, null,
                10, null, null, null, null, false, null
        );
    }

    // ═══════════════════════════════════════════════════════════════════════
    // getMyProfile() — instructor fetching their own profile
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void getMyProfile_found_returnsResponse() {
        InstructorProfile profile  = buildProfile();
        InstructorProfileResponse expected = buildResponse();

        when(repository.findByUserId(USER_ID)).thenReturn(Optional.of(profile));
        when(mapper.toResponse(profile)).thenReturn(expected);

        InstructorProfileResponse result = instructorService.getMyProfile(USER_ID);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getMyProfile_notFound_throwsResourceNotFoundException() {
        when(repository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> instructorService.getMyProfile(USER_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Instructor profile not found");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // getPublicProfile() — learner browsing an instructor's public profile
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void getPublicProfile_found_returnsResponse() {
        InstructorProfile profile  = buildProfile();
        InstructorProfileResponse expected = buildResponse();

        when(repository.findById(PROFILE_ID)).thenReturn(Optional.of(profile));
        when(mapper.toResponse(profile)).thenReturn(expected);

        InstructorProfileResponse result = instructorService.getPublicProfile(PROFILE_ID);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getPublicProfile_notFound_throwsResourceNotFoundException() {
        when(repository.findById(PROFILE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> instructorService.getPublicProfile(PROFILE_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Instructor not found");
    }
}
