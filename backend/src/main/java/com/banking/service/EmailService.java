package com.banking.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Service d'envoi d'email pour les codes OTP.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.email.simulate:false}")
    private boolean simulate;

    @Value("${spring.mail.username:noreply@bankingapp.com}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envoie le code OTP par email avec un template HTML stylise.
     */
    public void sendOtpEmail(String to, String otp) {
        if (simulate) {
            log.info("========================================================");
            log.info("[EMAIL SIMULE] Destinataire : {}", to);
            log.info("[EMAIL SIMULE] Code OTP    : {}", otp);
            log.info("[EMAIL SIMULE] Expire dans 10 minutes");
            log.info("========================================================");
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject("Code de verification - Banking App");
            helper.setText(buildHtmlBody(otp), true);
            mailSender.send(message);
            log.info("Email OTP envoye a {}", to);
        } catch (MessagingException e) {
            log.error("Erreur envoi email OTP : {}", e.getMessage());
            throw new RuntimeException("Impossible d'envoyer l'email OTP : " + e.getMessage());
        }
    }

    private String buildHtmlBody(String otp) {
        return "<!DOCTYPE html>" +
                "<html><body style='font-family:Arial,sans-serif;background:#F5F5F5;padding:30px;'>" +
                "<div style='max-width:600px;margin:auto;background:#FFFFFF;border-radius:12px;padding:40px;" +
                "box-shadow:0 4px 12px rgba(0,0,0,0.08);'>" +
                "<h1 style='color:#0D47A1;text-align:center;margin-bottom:8px;'>Banking App</h1>" +
                "<p style='color:#555;text-align:center;font-size:14px;'>Verification de votre compte</p>" +
                "<hr style='border:none;border-top:1px solid #eee;margin:24px 0;'/>" +
                "<p style='color:#333;font-size:15px;'>Bonjour,</p>" +
                "<p style='color:#333;font-size:15px;'>Voici votre code de verification :</p>" +
                "<div style='text-align:center;margin:32px 0;'>" +
                "<span style='display:inline-block;font-size:42px;font-weight:bold;color:#1976D2;" +
                "letter-spacing:14px;padding:18px 28px;background:#E3F2FD;border-radius:10px;'>" + otp + "</span>" +
                "</div>" +
                "<p style='color:#555;font-size:14px;'>Ce code expirera dans <b>10 minutes</b>.</p>" +
                "<p style='color:#888;font-size:13px;'>Si vous n'etes pas a l'origine de cette demande, ignorez ce message.</p>" +
                "<hr style='border:none;border-top:1px solid #eee;margin:24px 0;'/>" +
                "<p style='text-align:center;color:#777;font-size:13px;margin-bottom:0;'>" +
                "Cordialement,<br/><b>L'equipe Banking App</b></p>" +
                "</div></body></html>";
    }
}
