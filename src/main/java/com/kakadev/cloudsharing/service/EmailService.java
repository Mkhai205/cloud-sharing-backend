package com.kakadev.cloudsharing.service;

public interface EmailService {

    void sendVerificationAccount(String toEmail, String verificationCode);
    void sendResetPassword(String toEmail, String resetPasswordToken);
}
