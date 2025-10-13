package com.kakadev.cloudsharing.controller;

import com.kakadev.cloudsharing.dto.request.AuthenticationRequestDTO;
import com.kakadev.cloudsharing.dto.request.IntrospectRequestDTO;
import com.kakadev.cloudsharing.dto.request.LogoutRequestDTO;
import com.kakadev.cloudsharing.dto.request.RefreshTokenRequestDTO;
import com.kakadev.cloudsharing.dto.response.ApiResponse;
import com.kakadev.cloudsharing.dto.response.AuthenticationResponseDTO;
import com.kakadev.cloudsharing.dto.response.IntrospectResponseDTO;
import com.kakadev.cloudsharing.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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

    AuthenticationService authenticationService;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponseDTO> authenticate(
            @RequestBody AuthenticationRequestDTO request
    ) {
        return ApiResponse.<AuthenticationResponseDTO>builder()
                .result(authenticationService.authenticate(request))
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(
            @RequestBody LogoutRequestDTO request
    ) throws ParseException, JOSEException {
        authenticationService.logout(request);

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

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponseDTO> refreshToken(
            @RequestBody RefreshTokenRequestDTO request
    ) throws ParseException, JOSEException {
        return ApiResponse.<AuthenticationResponseDTO>builder()
                .result(authenticationService.refreshToken(request))
                .build();
    }

}
