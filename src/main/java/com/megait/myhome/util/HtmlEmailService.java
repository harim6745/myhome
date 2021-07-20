package com.megait.myhome.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Profile("dev")
@Service
@Slf4j
@RequiredArgsConstructor
public class HtmlEmailService implements EmailService{

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(EmailMessage emailMessage) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, false, "utf-8");

            helper.setTo(emailMessage.getTo());
            helper.setSubject(emailMessage.getSubject());
            helper.setText(emailMessage.getMessage(), true);
            javaMailSender.send(mimeMessage);
            log.info("Email has sent: {}", emailMessage.getMessage());

        } catch (MessagingException e){
            log.error("Failed to send email.", e);
        }
    }
}
