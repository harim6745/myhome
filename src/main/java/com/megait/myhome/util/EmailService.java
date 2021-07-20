package com.megait.myhome.util;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendEmail(EmailMessage emailMessage);
}
