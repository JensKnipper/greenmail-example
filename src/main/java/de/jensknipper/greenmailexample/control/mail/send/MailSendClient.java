package de.jensknipper.greenmailexample.control.mail.send;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public final class MailSendClient {

    private final String user;

    private final JavaMailSenderImpl javaMailSender;

    public MailSendClient(JavaMailSenderImpl javaMailSender,
                          @Value("${spring.mail.username}") String user) {
        this.javaMailSender = javaMailSender;
        this.user = user;
    }

    public void send(String recipient, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(user);
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }
}
