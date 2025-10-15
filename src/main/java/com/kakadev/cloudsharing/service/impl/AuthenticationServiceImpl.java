package com.kakadev.cloudsharing.service.impl;

import com.kakadev.cloudsharing.dto.request.*;
import com.kakadev.cloudsharing.dto.response.AuthenticationResponseDTO;
import com.kakadev.cloudsharing.dto.response.IntrospectResponseDTO;
import com.kakadev.cloudsharing.exception.AppException;
import com.kakadev.cloudsharing.exception.ErrorCode;
import com.kakadev.cloudsharing.model.entity.InvalidatedToken;
import com.kakadev.cloudsharing.model.entity.User;
import com.kakadev.cloudsharing.repository.InvalidatedTokenRepository;
import com.kakadev.cloudsharing.repository.UserRepository;
import com.kakadev.cloudsharing.service.AuthenticationService;
import com.kakadev.cloudsharing.service.EmailService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
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
public class AuthenticationServiceImpl implements AuthenticationService {

    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    @Value("${app.mail.verification-otp-expiry-duration}")
    private Long VERIFY_OTP_EXPIRY_DURATION;
    @Value("${app.mail.reset-password-token-expiry-duration}")
    private Long RESET_PASSWORD_TOKEN_EXPIRY_DURATION;
    @Value("${app.jwt.secret-key}")
    private String JWT_SECRET_KEY;
    @Value("${app.jwt.valid-duration}")
    private Long JWT_EXPIRY_DURATION;
    @Value("${app.jwt.refreshable-duration}")
    private Long REFRESHABLE_JWT_EXPIRY_DURATION;

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
                .expirationTime(new Date(Instant.now().plus(JWT_EXPIRY_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwsClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(JWT_SECRET_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Error generating token", e);
            throw new RuntimeException(e);
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(JWT_SECRET_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expirationTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
                .plus(REFRESHABLE_JWT_EXPIRY_DURATION, ChronoUnit.SECONDS).toEpochMilli())
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

    private String generateVerificationCode() {
        // Generate a random 6-digit verification code
        int code = (int)(Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }

    @Override
    public void sendVerificationAccount(String email) {
        User newUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (newUser.getIsAccountVerified()) {
            throw new AppException(ErrorCode.USER_ALREADY_VERIFIED);
        }

        String verificationCode = generateVerificationCode();
        newUser.setVerifyOtp(verificationCode);
        newUser.setVerifyOtpExpiry(Instant.now().plus(VERIFY_OTP_EXPIRY_DURATION, ChronoUnit.SECONDS));

        userRepository.save(newUser);

        emailService.sendVerificationAccount(email, verificationCode);
    }

    @Override
    public void forgotPassword(String email) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!existingUser.getIsAccountVerified()) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_VERIFIED);
        }

        String resetPasswordToken = UUID.randomUUID().toString();
        existingUser.setResetPasswordToken(resetPasswordToken);
        existingUser.setResetPasswordTokenExpiry(
                Instant.now().plus(RESET_PASSWORD_TOKEN_EXPIRY_DURATION, ChronoUnit.SECONDS));

        userRepository.save(existingUser);

        emailService.sendResetPassword(email, resetPasswordToken);
    }

    @Override
    public void resetPassword(ResetPasswordRequestDTO request) {
        User existingUser = userRepository.findByResetPasswordToken(request.getResetPasswordToken())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (existingUser.getResetPasswordTokenExpiry() == null ||
                existingUser.getResetPasswordTokenExpiry().isBefore(Instant.now())) {
            throw new AppException(ErrorCode.RESET_PASSWORD_TOKEN_EXPIRED);
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        existingUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        existingUser.setResetPasswordToken(null);
        existingUser.setResetPasswordTokenExpiry(null);

        userRepository.save(existingUser);
    }

    @Override
    public AuthenticationResponseDTO verifyAccount(
            VerifyAccountRequestDTO request
    ) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getVerifyOtp() == null || !user.getVerifyOtp().equals(request.getVerificationCode())) {
            throw new AppException(ErrorCode.VERIFICATION_CODE_INVALID);
        }

        if (user.getVerifyOtpExpiry() == null || user.getVerifyOtpExpiry().isBefore(Instant.now())) {
            throw new AppException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        user.setVerifyOtp(null);
        user.setVerifyOtpExpiry(null);
        user.setIsAccountVerified(true);
        userRepository.save(user);

        String token = generateToken(user);

        return AuthenticationResponseDTO.builder()
                .token(token)
                .authenticated(true)
                .build();
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

        if (!user.getIsAccountVerified()) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_VERIFIED);
        }

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
