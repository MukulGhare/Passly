package com.passly.instructor;

import com.passly.auth.AppUserDetails;
import com.passly.instructor.dto.InstructorProfileResponse;
import com.passly.instructor.dto.UpdateProfileRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Private endpoints — accessible only by authenticated instructors.
 * Manages the instructor's own profile.
 */
@RestController
@RequestMapping("/api/instructor/profile")
@PreAuthorize("hasRole('INSTRUCTOR')")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    /** Get the current instructor's own full profile. */
    @GetMapping
    public InstructorProfileResponse getMyProfile(@AuthenticationPrincipal AppUserDetails user) {
        return instructorService.getMyProfile(user.getUserId());
    }

    /**
     * Update profile fields.
     * Send only the fields you want to change — nulls are ignored.
     */
    @PutMapping
    public InstructorProfileResponse updateProfile(
            @AuthenticationPrincipal AppUserDetails user,
            @Valid @RequestBody UpdateProfileRequest request) {
        return instructorService.updateProfile(user.getUserId(), request);
    }

    /** Upload or replace the instructor's profile photo. */
    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public InstructorProfileResponse uploadPhoto(
            @AuthenticationPrincipal AppUserDetails user,
            @RequestPart("photo") MultipartFile photo) {
        return instructorService.uploadPhoto(user.getUserId(), photo);
    }
}
