package com.kakadev.cloudsharing.controller;

import com.kakadev.cloudsharing.dto.request.*;
import com.kakadev.cloudsharing.dto.response.ApiResponse;
import com.kakadev.cloudsharing.dto.response.AuthenticationResponseDTO;
import com.kakadev.cloudsharing.dto.response.IntrospectResponseDTO;
import com.kakadev.cloudsharing.exception.AppException;
import com.kakadev.cloudsharing.exception.ErrorCode;
import com.kakadev.cloudsharing.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    @NonFinal
    @Value("${app.jwt.cookie-max-age}")
    int JWT_COOKIE_MAX_AGE;

    AuthenticationService authenticationService;

    @PostMapping("/verify-account")
    ApiResponse<AuthenticationResponseDTO> verifyAccount(
            @RequestBody VerifyAccountRequestDTO request,
            HttpServletResponse response
    ) {
        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.verifyAccount(request);

        setJwtTokenCookie(response, authenticationResponseDTO.getToken());

        return ApiResponse.<AuthenticationResponseDTO>builder()
                .result(authenticationResponseDTO)
                .build();
    }

    @PostMapping("/resend-verification")
    ApiResponse<Void> resendVerification(
            @RequestBody ResendVerificationRequestDTO request
    ) {
        authenticationService.sendVerificationAccount(request.getEmail());
        return ApiResponse.<Void>builder()
                .build();
    }

    @PostMapping("/forgot-password")
    ApiResponse<Void> forgotPassword(
            @RequestBody ForgotPasswordRequestDTO request
    ) {
        authenticationService.forgotPassword(request.getEmail());
        return ApiResponse.<Void>builder()
                .build();
    }

    @PostMapping("/reset-password")
    ApiResponse<Void> resetPassword(
            @RequestBody ResetPasswordRequestDTO request
    ) {
        authenticationService.resetPassword(request);
        return ApiResponse.<Void>builder()
                .build();
    }

    @PostMapping("/login")
    ApiResponse<AuthenticationResponseDTO> authenticate(
            @RequestBody AuthenticationRequestDTO request,
            HttpServletResponse response
    ) {
        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.authenticate(request);

        setJwtTokenCookie(response, authenticationResponseDTO.getToken());

        return ApiResponse.<AuthenticationResponseDTO>builder()
                .result(authenticationResponseDTO)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(
            HttpServletRequest request, HttpServletResponse response
    ) {
        String token = extractJwtTokenFromCookies(request);

        if (token == null || token.isEmpty()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        authenticationService.logout(token);

        clearJwtTokenCookie(response);

        return ApiResponse.<Void>builder()
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponseDTO> introspect(
            @RequestBody IntrospectRequestDTO request
    ) throws ParseException, JOSEException {
        return ApiResponse.<IntrospectResponseDTO>builder()
                .result(authenticationService.introspect(request))
                .build();
    }

    @PostMapping("/refresh-token")
    ApiResponse<AuthenticationResponseDTO> refreshToken(
            HttpServletRequest request, HttpServletResponse response
    ) throws ParseException, JOSEException {
        String token = extractJwtTokenFromCookies(request);

        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.refreshToken(token);

        setJwtTokenCookie(response, authenticationResponseDTO.getToken());

        return ApiResponse.<AuthenticationResponseDTO>builder()
                .result(authenticationResponseDTO)
                .build();
    }

    private String extractJwtTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt-token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void setJwtTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie jwtTokenCookie = ResponseCookie.from("jwt-token", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(JWT_COOKIE_MAX_AGE)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtTokenCookie.toString());
    }

    private void clearJwtTokenCookie(HttpServletResponse response) {
        ResponseCookie deleteJwtTokenCookie = ResponseCookie.from("jwt-token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteJwtTokenCookie.toString());
    }
}
