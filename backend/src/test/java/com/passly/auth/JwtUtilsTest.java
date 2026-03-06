package com.passly.auth;

import com.passly.common.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for JwtUtils.
 *
 * No mocks needed here — JwtUtils is a pure utility class that only
 * depends on AppProperties (a simple config object we can create manually).
 *
 * @BeforeEach — runs before every test, used to set up the JwtUtils instance.
 */
class JwtUtilsTest {

    private JwtUtils jwtUtils;

    private static final UUID   USER_ID = UUID.randomUUID();
    private static final String EMAIL   = "jwt@example.com";
    private static final String ROLE    = "INSTRUCTOR";

    // Same dev secret used in application-dev.yml — safe to use in tests
    private static final String SECRET = "dGVhY2h1cC1kZXYtb25seS1ub3QtZm9yLXByb2R1Y3Rpb24=";

    @BeforeEach
    void setUp() {
        AppProperties props = new AppProperties();
        props.getJwt().setSecret(SECRET);
        props.getJwt().setExpirationMs(3_600_000L);        // 1 hour — tokens are valid
        props.getJwt().setRefreshExpirationMs(604_800_000L); // 7 days
        jwtUtils = new JwtUtils(props);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Token generation + validation
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void generateAccessToken_producesValidToken() {
        String token = jwtUtils.generateAccessToken(USER_ID, EMAIL, ROLE);
        assertThat(jwtUtils.isValid(token)).isTrue();
    }

    @Test
    void generateRefreshToken_producesValidToken() {
        String token = jwtUtils.generateRefreshToken(USER_ID, EMAIL, ROLE);
        assertThat(jwtUtils.isValid(token)).isTrue();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Claim extraction — the data we encode must come back unchanged
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void extractEmail_returnsCorrectEmail() {
        String token = jwtUtils.generateAccessToken(USER_ID, EMAIL, ROLE);
        assertThat(jwtUtils.extractEmail(token)).isEqualTo(EMAIL);
    }

    @Test
    void extractRole_returnsCorrectRole() {
        String token = jwtUtils.generateAccessToken(USER_ID, EMAIL, ROLE);
        assertThat(jwtUtils.extractRole(token)).isEqualTo(ROLE);
    }

    @Test
    void extractUserId_returnsCorrectUserId() {
        String token = jwtUtils.generateAccessToken(USER_ID, EMAIL, ROLE);
        assertThat(jwtUtils.extractUserId(token)).isEqualTo(USER_ID);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Invalid token scenarios
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    void isValid_withGarbageString_returnsFalse() {
        // A completely invalid string must be rejected
        assertThat(jwtUtils.isValid("this.is.not.a.jwt")).isFalse();
    }

    @Test
    void isValid_withExpiredToken_returnsFalse() {
        // Create a separate JwtUtils configured to issue already-expired tokens.
        // expirationMs = -1000 means expiry = now - 1 second (already in the past).
        AppProperties expiredProps = new AppProperties();
        expiredProps.getJwt().setSecret(SECRET);
        expiredProps.getJwt().setExpirationMs(-1_000L);
        expiredProps.getJwt().setRefreshExpirationMs(-1_000L);
        JwtUtils expiredJwtUtils = new JwtUtils(expiredProps);

        String expiredToken = expiredJwtUtils.generateAccessToken(USER_ID, EMAIL, ROLE);

        // Our normal jwtUtils (which enforces expiry) must reject it
        assertThat(jwtUtils.isValid(expiredToken)).isFalse();
    }

    @Test
    void isValid_withTokenSignedByDifferentKey_returnsFalse() {
        // A token signed by a different secret must be rejected —
        // this is critical for security (prevents token forgery).
        AppProperties otherProps = new AppProperties();
        otherProps.getJwt().setSecret("ZGlmZmVyZW50LXNlY3JldC1rZXktZm9yLXRlc3Rpbmctb25seQ==");
        otherProps.getJwt().setExpirationMs(3_600_000L);
        JwtUtils otherUtils = new JwtUtils(otherProps);

        String tokenFromOtherKey = otherUtils.generateAccessToken(USER_ID, EMAIL, ROLE);

        assertThat(jwtUtils.isValid(tokenFromOtherKey)).isFalse();
    }
}
