package com.passly.instructor;

import com.passly.instructor.dto.InstructorProfileResponse;
import com.passly.instructor.dto.InstructorSummaryResponse;
import com.passly.instructor.dto.UpdateProfileRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface InstructorProfileMapper {

    // Maps user.id → userId explicitly (nested field)
    @Mapping(source = "user.id", target = "userId")
    InstructorProfileResponse toResponse(InstructorProfile profile);

    InstructorSummaryResponse toSummary(InstructorProfile profile);

    // Partial update: null fields in request are ignored — existing values kept
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(UpdateProfileRequest request, @MappingTarget InstructorProfile profile);
}
