package com.passly.instructor.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * All fields are optional — null means "don't change this field".
 * MapStruct's NullValuePropertyMappingStrategy.IGNORE handles this automatically.
 */
public record UpdateProfileRequest(

    @Size(max = 1000)
    String bio,

    @Size(max = 20)
    String phoneNumber,

    @DecimalMin("5.00") @DecimalMax("500.00")
    BigDecimal ratePerHour,

    @Min(0) @Max(50)
    Integer yearsExperience,

    @Size(max = 50)
    String licenseNumber,

    @Min(1) @Max(100)
    Integer serviceRadiusKm,

    @DecimalMin("-90.0") @DecimalMax("90.0")
    BigDecimal latitude,

    @DecimalMin("-180.0") @DecimalMax("180.0")
    BigDecimal longitude,

    @Size(max = 100)
    String city,

    @Size(max = 20)
    String postcode
) {}
