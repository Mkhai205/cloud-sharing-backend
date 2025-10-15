package com.kakadev.cloudsharing.service.impl;

import com.kakadev.cloudsharing.exception.AppException;
import com.kakadev.cloudsharing.exception.ErrorCode;
import com.kakadev.cloudsharing.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    String fromEmail;

    final JavaMailSender mailSender;

    @Override
    public void sendVerificationAccount(String toEmail, String verificationCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setFrom(fromEmail);
            message.setSubject("Password reset OTP!");
            message.setText("Your otp for resetting your password is "+verificationCode+". Use this OTP to proceed with resetting your password");

            mailSender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AppException(ErrorCode.SEND_EMAIL_FAILED);
        }
    }

    @Override
    public void sendResetPassword(String toEmail, String resetCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setFrom(fromEmail);
            message.setSubject("Account Verification OTP");
            message.setText("Your otp is "+resetCode+". Verify your account using this OTP");

            mailSender.send(message);
        } catch (Exception e) {
            throw  new AppException(ErrorCode.SEND_EMAIL_FAILED);
        }
    }
}
