package com.passly.instructor.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Trimmed public view used in search results.
 * Omits phone number and precise coordinates for privacy.
 */
public record InstructorSummaryResponse(
    UUID id,
    String firstName,
    String lastName,
    String bio,
    String photoUrl,
    BigDecimal ratePerHour,
    Integer yearsExperience,
    Integer serviceRadiusKm,
    String city,
    String postcode
) {}
