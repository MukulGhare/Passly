package com.passly.instructor.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record InstructorProfileResponse(
    UUID id,
    UUID userId,
    String firstName,
    String lastName,
    String bio,
    String photoUrl,
    String phoneNumber,
    BigDecimal ratePerHour,
    Integer yearsExperience,
    String licenseNumber,
    Integer serviceRadiusKm,
    BigDecimal latitude,
    BigDecimal longitude,
    String city,
    String postcode,
    boolean profileComplete,
    Instant createdAt
) {}
