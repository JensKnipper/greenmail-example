package de.jensknipper.greenmailexample.control.mail.mapper;

import de.jensknipper.greenmailexample.model.Mail;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import org.owasp.encoder.Encode;

import java.io.IOException;
import java.util.Arrays;

public final class MailMapper {

    private MailMapper() {
    }

    public static Mail map(Message message, String user) {
        String subject = Encode.forHtml(getSubject(message));
        String content = Encode.forHtml(getContent(message));
        String from = Encode.forHtml(getFrom(message));
        String recipient = Encode.forHtml(getRecipient(message, user));
        return new Mail(subject, content, from, recipient);
    }

    private static String getSubject(Message message) {
        try {
            return message.getSubject();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getContent(Message message) {
        try {
            Object content = message.getContent();
            if (content == null) {
                return null;
            }
            return content.toString();
        } catch (IOException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFrom(Message message) {
        try {
            Address[] from = message.getFrom();
            if (from.length == 0 || from[0] == null) {
                return null;
            }
            return from[0].toString();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getRecipient(Message message, String user) {
        try {
            return Arrays.stream(message.getAllRecipients())
                    .map(Address::toString)
                    .filter(it -> it.startsWith(user))
                    .findFirst()
                    .orElse(user);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
