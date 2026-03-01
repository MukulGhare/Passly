package com.passly.instructor;

import com.passly.instructor.dto.InstructorProfileResponse;
import com.passly.instructor.dto.InstructorSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Public endpoints — no authentication required.
 * Used by learners (and anonymous visitors) to browse instructors.
 */
@RestController
@RequestMapping("/api/instructors")
@RequiredArgsConstructor
public class InstructorSearchController {

    private final InstructorService instructorService;

    /**
     * Search instructors with optional filters.
     *
     * GET /api/instructors?city=London&maxRate=50&page=0&size=20&sort=ratePerHour,asc
     */
    @GetMapping
    public Page<InstructorSummaryResponse> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal maxRate,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return instructorService.search(city, maxRate, pageable);
    }

    /**
     * Get a single instructor's public profile.
     *
     * GET /api/instructors/{id}
     */
    @GetMapping("/{id}")
    public InstructorProfileResponse getProfile(@PathVariable UUID id) {
        return instructorService.getPublicProfile(id);
    }
}
