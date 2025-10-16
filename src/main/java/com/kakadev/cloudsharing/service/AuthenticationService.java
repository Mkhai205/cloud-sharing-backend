package com.kakadev.cloudsharing.service;

import com.kakadev.cloudsharing.dto.request.AuthenticationRequestDTO;
import com.kakadev.cloudsharing.dto.request.IntrospectRequestDTO;
import com.kakadev.cloudsharing.dto.request.ResetPasswordRequestDTO;
import com.kakadev.cloudsharing.dto.request.VerifyAccountRequestDTO;
import com.kakadev.cloudsharing.dto.response.AuthenticationResponseDTO;
import com.kakadev.cloudsharing.dto.response.IntrospectResponseDTO;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {
    void sendVerificationAccount(String email);
    AuthenticationResponseDTO verifyAccount(VerifyAccountRequestDTO request);
    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request);

    AuthenticationResponseDTO refreshToken(String token) throws ParseException, JOSEException;
    IntrospectResponseDTO introspect(IntrospectRequestDTO request) throws ParseException, JOSEException;

    void logout(String token);
    void forgotPassword( String email);
    void resetPassword(ResetPasswordRequestDTO request);
}
