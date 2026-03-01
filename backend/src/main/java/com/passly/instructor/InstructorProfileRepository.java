package com.passly.instructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface InstructorProfileRepository extends JpaRepository<InstructorProfile, UUID> {

    Optional<InstructorProfile> findByUserId(UUID userId);

    /**
     * Dynamic search — all filters are optional (null = no filter).
     * Only returns profiles marked as complete.
     */
    @Query("""
            SELECT p FROM InstructorProfile p
            WHERE p.profileComplete = true
              AND (:city    IS NULL OR LOWER(p.city) = LOWER(:city))
              AND (:maxRate IS NULL OR p.ratePerHour <= :maxRate)
            """)
    Page<InstructorProfile> search(
            @Param("city")    String city,
            @Param("maxRate") BigDecimal maxRate,
            Pageable pageable
    );
}
