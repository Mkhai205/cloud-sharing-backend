package com.kakadev.cloudsharing.service.impl;

import com.kakadev.cloudsharing.exception.AppException;
import com.kakadev.cloudsharing.exception.ErrorCode;
import com.kakadev.cloudsharing.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String fromEmail;
    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationAccount(String toEmail, String verificationCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setFrom(fromEmail);
            message.setSubject("Account Verification OTP");
            message.setText("Your otp is "+verificationCode+". Verify your account using this OTP");

            mailSender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AppException(ErrorCode.SEND_EMAIL_FAILED);
        }
    }

    @Override
    public void sendResetPassword(String toEmail, String resetPasswordToken) {
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + resetPasswordToken;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setFrom(fromEmail);
            message.setSubject("Reset Password!");
            message.setText("Click the link to reset your password: " + resetLink);

            mailSender.send(message);
        } catch (Exception e) {
            throw  new AppException(ErrorCode.SEND_EMAIL_FAILED);
        }
    }
}
