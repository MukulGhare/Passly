package com.passly.instructor;

import com.passly.common.BaseEntity;
import com.passly.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "instructor_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "rate_per_hour", precision = 8, scale = 2)
    private BigDecimal ratePerHour;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(name = "license_number", length = 50)
    private String licenseNumber;

    @Column(name = "service_radius_km")
    @Builder.Default
    private Integer serviceRadiusKm = 10;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(length = 100)
    private String city;

    @Column(length = 20)
    private String postcode;

    @Column(name = "is_profile_complete", nullable = false)
    @Builder.Default
    private boolean profileComplete = false;

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AvailabilitySlot> availabilitySlots = new ArrayList<>();

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AvailabilityException> availabilityExceptions = new ArrayList<>();
}
