package com.passly.auth;

import com.passly.auth.dto.AuthResponse;
import com.passly.auth.dto.LoginRequest;
import com.passly.auth.dto.RefreshTokenRequest;
import com.passly.auth.dto.RegisterRequest;
import com.passly.common.exception.EmailAlreadyExistsException;
import com.passly.instructor.InstructorProfile;
import com.passly.instructor.InstructorProfileRepository;
import com.passly.learner.LearnerProfile;
import com.passly.learner.LearnerProfileRepository;
import com.passly.user.User;
import com.passly.user.UserRepository;
import com.passly.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final InstructorProfileRepository instructorProfileRepository;
    private final LearnerProfileRepository learnerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();
        user = userRepository.save(user);

        if (request.role() == UserRole.INSTRUCTOR) {
            instructorProfileRepository.save(InstructorProfile.builder()
                    .user(user)
                    .firstName(request.firstName())
                    .lastName(request.lastName())
                    .build());
        } else {
            learnerProfileRepository.save(LearnerProfile.builder()
                    .user(user)
                    .firstName(request.firstName())
                    .lastName(request.lastName())
                    .build());
        }

        return issueTokens(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = userRepository.findByEmail(request.email()).orElseThrow();
        return issueTokens(user);
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        if (!jwtUtils.isValid(request.refreshToken())) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }
        String email = jwtUtils.extractEmail(request.refreshToken());
        User user = userRepository.findByEmail(email).orElseThrow();
        return issueTokens(user);
    }

    private AuthResponse issueTokens(User user) {
        String accessToken  = jwtUtils.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtils.generateRefreshToken(user.getId(), user.getEmail(), user.getRole().name());
        return new AuthResponse(user.getId(), user.getEmail(), user.getRole().name(), accessToken, refreshToken);
    }
}
