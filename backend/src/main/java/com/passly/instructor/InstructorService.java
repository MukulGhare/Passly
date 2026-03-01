package com.passly.instructor;

import com.passly.common.exception.ResourceNotFoundException;
import com.passly.common.storage.StorageService;
import com.passly.instructor.dto.InstructorProfileResponse;
import com.passly.instructor.dto.InstructorSummaryResponse;
import com.passly.instructor.dto.UpdateProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstructorService {

    private final InstructorProfileRepository repository;
    private final InstructorProfileMapper mapper;
    private final StorageService storageService;

    // ----------------------------------------------------------------
    // Instructor's own profile
    // ----------------------------------------------------------------

    @Transactional(readOnly = true)
    public InstructorProfileResponse getMyProfile(UUID userId) {
        return mapper.toResponse(findByUserId(userId));
    }

    @Transactional
    public InstructorProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        InstructorProfile profile = findByUserId(userId);
        mapper.updateFromRequest(request, profile);       // null fields are ignored
        profile.setProfileComplete(isComplete(profile));
        return mapper.toResponse(repository.save(profile));
    }

    @Transactional
    public InstructorProfileResponse uploadPhoto(UUID userId, MultipartFile photo) {
        InstructorProfile profile = findByUserId(userId);
        String url = storageService.upload(photo, "instructors/" + profile.getId());
        profile.setPhotoUrl(url);
        return mapper.toResponse(repository.save(profile));
    }

    // ----------------------------------------------------------------
    // Public — used by learners to browse instructors
    // ----------------------------------------------------------------

    @Transactional(readOnly = true)
    public InstructorProfileResponse getPublicProfile(UUID profileId) {
        InstructorProfile profile = repository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));
        return mapper.toResponse(profile);
    }

    @Transactional(readOnly = true)
    public Page<InstructorSummaryResponse> search(String city, BigDecimal maxRate, Pageable pageable) {
        return repository.search(city, maxRate, pageable)
                .map(mapper::toSummary);
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------

    private InstructorProfile findByUserId(UUID userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor profile not found"));
    }

    /**
     * A profile is considered complete once the essential fields are filled.
     * Controls whether the instructor appears in learner searches.
     */
    private boolean isComplete(InstructorProfile p) {
        return p.getBio() != null && !p.getBio().isBlank()
                && p.getPhoneNumber() != null && !p.getPhoneNumber().isBlank()
                && p.getRatePerHour() != null
                && p.getCity() != null && !p.getCity().isBlank();
    }
}
