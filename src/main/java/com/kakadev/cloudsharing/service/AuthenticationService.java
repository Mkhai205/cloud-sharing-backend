package com.kakadev.cloudsharing.service;

import com.kakadev.cloudsharing.dto.request.AuthenticationRequestDTO;
import com.kakadev.cloudsharing.dto.request.IntrospectRequestDTO;
import com.kakadev.cloudsharing.dto.request.LogoutRequestDTO;
import com.kakadev.cloudsharing.dto.request.RefreshTokenRequestDTO;
import com.kakadev.cloudsharing.dto.response.AuthenticationResponseDTO;
import com.kakadev.cloudsharing.dto.response.IntrospectResponseDTO;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request);
    AuthenticationResponseDTO refreshToken(RefreshTokenRequestDTO request) throws ParseException, JOSEException;
    IntrospectResponseDTO introspect(IntrospectRequestDTO request) throws ParseException, JOSEException;
    void logout(LogoutRequestDTO request) throws ParseException, JOSEException;
}
