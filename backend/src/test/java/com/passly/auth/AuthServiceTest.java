package com.passly.auth;

import com.passly.auth.dto.AuthResponse;
import com.passly.auth.dto.LoginRequest;
import com.passly.auth.dto.RefreshTokenRequest;
import com.passly.auth.dto.RegisterRequest;
import com.passly.common.exception.EmailAlreadyExistsException;
import com.passly.common.exception.InvalidTokenException;
import com.passly.instructor.InstructorProfileRepository;
import com.passly.learner.LearnerProfileRepository;
import com.passly.user.User;
import com.passly.user.UserRepository;
import com.passly.user.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService.
 *
 * These are PURE unit tests — no Spring context, no database.
 * Every dependency is replaced with a Mockito mock so we can test
 * the service logic in isolation.
 *
 * Key annotations:
 *   @ExtendWith(MockitoExtension.class) — activates Mockito for this test class
 *   @Mock                               — creates a fake/mock object
 *   @InjectMocks                        — creates the real AuthService and injects the mocks into it
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // ── Mocked dependencies ───────────────────────────────────────────────
    @Mock UserRepository              userRepository;
    @Mock InstructorProfileRepository instructorProfileRepository;
    @Mock LearnerProfileRepository    learnerProfileRepository;
    @Mock PasswordEncoder             passwordEncoder;
    @Mock JwtUtils                    jwtUtils;
    @Mock AuthenticationManager       authenticationManager;

    // ── Class under test ──────────────────────────────────────────────────
    @InjectMocks AuthService authService;

    // ── Shared test data ──────────────────────────────────────────────────
    private static final UUID   USER_ID  = UUID.randomUUID();
    private static final String EMAIL    = "test@example.com";
    private static final String PASSWORD = "password123";
    private static final String HASHED   = "hashed_password";
    private static final String ACCESS   = "access.token.here";
    private static final String REFRESH  = "refresh.token.here";

    /**
     * Builds a User as if JPA had saved it and assigned a UUID.
     * (In real code the DB assigns the ID via @GeneratedValue,
     *  so we simulate that by calling setId() manually.)
     */
    private User savedUser(UserRole role) {
        User u = User.builder()
                .email(EMAIL)
                .passwordHash(HASHED)
                .role(role)
                .build();
        u.setId(USER_ID);
        return u;
    }

    /** Stubs JwtUtils to return predictable tokens for a given role. */
    private void stubTokenGeneration(UserRole role) {
        when(jwtUtils.generateAccessToken(USER_ID, EMAIL, role.name())).thenReturn(ACCESS);
        when(jwtUtils.generateRefreshToken(USER_ID, EMAIL, role.name())).thenReturn(REFRESH);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // register()
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void register_asInstructor_savesInstructorProfileAndReturnsTokens() {
        // GIVEN — a valid registration request for an INSTRUCTOR
        RegisterRequest req = new RegisterRequest(EMAIL, PASSWORD, UserRole.INSTRUCTOR, "John", "Doe");
        User saved = savedUser(UserRole.INSTRUCTOR);

        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(HASHED);
        when(userRepository.save(any())).thenReturn(saved);
        stubTokenGeneration(UserRole.INSTRUCTOR);

        // WHEN
        AuthResponse response = authService.register(req);

        // THEN — tokens are returned and the INSTRUCTOR profile was created
        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(response.email()).isEqualTo(EMAIL);
        assertThat(response.role()).isEqualTo("INSTRUCTOR");
        assertThat(response.accessToken()).isEqualTo(ACCESS);
        assertThat(response.refreshToken()).isEqualTo(REFRESH);

        verify(instructorProfileRepository).save(any());
        verify(learnerProfileRepository, never()).save(any()); // wrong profile type must NOT be created
    }

    @Test
    void register_asLearner_savesLearnerProfileAndReturnsTokens() {
        // GIVEN — a valid registration request for a LEARNER
        RegisterRequest req = new RegisterRequest(EMAIL, PASSWORD, UserRole.LEARNER, "Jane", "Doe");
        User saved = savedUser(UserRole.LEARNER);

        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(HASHED);
        when(userRepository.save(any())).thenReturn(saved);
        stubTokenGeneration(UserRole.LEARNER);

        // WHEN
        AuthResponse response = authService.register(req);

        // THEN
        assertThat(response.role()).isEqualTo("LEARNER");
        verify(learnerProfileRepository).save(any());
        verify(instructorProfileRepository, never()).save(any());
    }

    @Test
    void register_emailAlreadyExists_throwsEmailAlreadyExistsException() {
        // GIVEN — email is already taken
        RegisterRequest req = new RegisterRequest(EMAIL, PASSWORD, UserRole.LEARNER, "Jane", "Doe");
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        // WHEN / THEN — exception is thrown and no user is saved
        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining(EMAIL);

        verify(userRepository, never()).save(any());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // login()
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void login_validCredentials_returnsTokens() {
        // GIVEN — correct email and password
        LoginRequest req = new LoginRequest(EMAIL, PASSWORD);
        User user = savedUser(UserRole.INSTRUCTOR);

        // authenticationManager.authenticate() won't throw = credentials are valid
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        stubTokenGeneration(UserRole.INSTRUCTOR);

        // WHEN
        AuthResponse response = authService.login(req);

        // THEN
        assertThat(response.email()).isEqualTo(EMAIL);
        assertThat(response.accessToken()).isEqualTo(ACCESS);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_wrongPassword_throwsBadCredentialsException() {
        // GIVEN — authenticationManager rejects the credentials
        LoginRequest req = new LoginRequest(EMAIL, "wrong_password");
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        // WHEN / THEN
        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(BadCredentialsException.class);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // refresh()
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void refresh_validToken_returnsNewTokens() {
        // GIVEN — a valid refresh token
        RefreshTokenRequest req = new RefreshTokenRequest(REFRESH);
        User user = savedUser(UserRole.INSTRUCTOR);

        when(jwtUtils.isValid(REFRESH)).thenReturn(true);
        when(jwtUtils.extractEmail(REFRESH)).thenReturn(EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        stubTokenGeneration(UserRole.INSTRUCTOR);

        // WHEN
        AuthResponse response = authService.refresh(req);

        // THEN
        assertThat(response.userId()).isEqualTo(USER_ID);
        assertThat(response.accessToken()).isEqualTo(ACCESS);
    }

    @Test
    void refresh_invalidToken_throwsInvalidTokenException() {
        // GIVEN — token fails validation (expired or tampered)
        RefreshTokenRequest req = new RefreshTokenRequest("bad.token.here");
        when(jwtUtils.isValid("bad.token.here")).thenReturn(false);

        // WHEN / THEN
        assertThatThrownBy(() -> authService.refresh(req))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Invalid or expired refresh token");
    }
}
