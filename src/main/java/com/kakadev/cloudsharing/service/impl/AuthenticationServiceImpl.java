package com.kakadev.cloudsharing.service.impl;

import com.kakadev.cloudsharing.dto.request.AuthenticationRequestDTO;
import com.kakadev.cloudsharing.dto.request.IntrospectRequestDTO;
import com.kakadev.cloudsharing.dto.request.LogoutRequestDTO;
import com.kakadev.cloudsharing.dto.request.RefreshTokenRequestDTO;
import com.kakadev.cloudsharing.dto.response.AuthenticationResponseDTO;
import com.kakadev.cloudsharing.dto.response.IntrospectResponseDTO;
import com.kakadev.cloudsharing.exception.AppException;
import com.kakadev.cloudsharing.exception.ErrorCode;
import com.kakadev.cloudsharing.model.entity.InvalidatedToken;
import com.kakadev.cloudsharing.model.entity.User;
import com.kakadev.cloudsharing.repository.InvalidatedTokenRepository;
import com.kakadev.cloudsharing.repository.UserRepository;
import com.kakadev.cloudsharing.service.AuthenticationService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SECRET_KEY;
    @NonFinal
    @Value("${jwt.valid-duration}")
    protected Long VALID_DURATION;
    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected Long REFRESHABLE_DURATION;

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!user.getRoles().isEmpty()) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
                }
            });
        }

        return stringJoiner.toString();
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwsClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("kakadev.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwsClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Error generating token", e);
            throw new RuntimeException(e);
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expirationTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!verified || expirationTime.before(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    @Override
    public AuthenticationResponseDTO refreshToken(
            RefreshTokenRequestDTO request
    ) throws ParseException, JOSEException {
        SignedJWT signedToken = verifyToken(request.getToken(), true);

        String jit = signedToken.getJWTClaimsSet().getJWTID();
        Date expirationTime = signedToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expirationTime(expirationTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        String email = signedToken.getJWTClaimsSet().getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String token = generateToken(user);

        return AuthenticationResponseDTO.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    @Override
    public AuthenticationResponseDTO authenticate(
            AuthenticationRequestDTO request
    ) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!isAuthenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(user);

        return AuthenticationResponseDTO.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    @Override
    public void logout(
            LogoutRequestDTO request
    ) throws ParseException, JOSEException {
        try {
            SignedJWT signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expirationTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expirationTime(expirationTime)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException e) {
            log.info("Token already expired");
        }
    }

    @Override
    public IntrospectResponseDTO introspect(
            IntrospectRequestDTO request
    ) throws ParseException, JOSEException {
        String token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponseDTO.builder()
                .valid(isValid)
                .build();
    }
}
