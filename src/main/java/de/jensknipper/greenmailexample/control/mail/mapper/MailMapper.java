package de.jensknipper.greenmailexample.control.mail.mapper;

import de.jensknipper.greenmailexample.model.Mail;
import org.owasp.encoder.Encode;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;

public final class MailMapper {

    private MailMapper() {
    }

    public static Mail map(Message message) {
        String subject = Encode.forHtml(getSubject(message));
        String content = Encode.forHtml(getContent(message));
        String from = Encode.forHtml(getFrom(message));
        return new Mail(subject, content, from);
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
}
