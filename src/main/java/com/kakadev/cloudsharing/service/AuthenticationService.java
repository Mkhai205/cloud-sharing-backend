package com.kakadev.cloudsharing.service;

import com.kakadev.cloudsharing.dto.request.*;
import com.kakadev.cloudsharing.dto.response.AuthenticationResponseDTO;
import com.kakadev.cloudsharing.dto.response.IntrospectResponseDTO;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {
    void sendVerificationAccount(String email);
    AuthenticationResponseDTO verifyAccount(VerifyAccountRequestDTO request);
    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request);
    AuthenticationResponseDTO refreshToken(RefreshTokenRequestDTO request) throws ParseException, JOSEException;
    IntrospectResponseDTO introspect(IntrospectRequestDTO request) throws ParseException, JOSEException;
    void logout(LogoutRequestDTO request) throws ParseException, JOSEException;
}
