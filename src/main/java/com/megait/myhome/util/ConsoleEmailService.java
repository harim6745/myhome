package com.megait.myhome.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Profile("local")
@Service
@Slf4j
public class ConsoleEmailService implements EmailService{
    @Override
    public void sendEmail(EmailMessage emailMessage) {
        log.info("email has sent : {}", emailMessage.getMessage());
    }
}
