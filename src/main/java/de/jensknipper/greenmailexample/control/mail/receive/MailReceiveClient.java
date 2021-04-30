package de.jensknipper.greenmailexample.control.mail.receive;

import de.jensknipper.greenmailexample.control.mail.mapper.MailMapper;
import de.jensknipper.greenmailexample.model.Mail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Component
public final class MailReceiveClient {

    @Value("${mail.store.protocol}")
    private String protocol;

    @Value("${mail.store.host}")
    private String host;

    @Value("${mail.store.port}")
    private String port;

    @Value("${spring.mail.username}")
    private String user;

    @Value("${spring.mail.password}")
    private String password;

    public List<Mail> receive() {
        Store emailStore = null;
        Folder emailFolder = null;

        try {
            Properties properties = new Properties();
            properties.put("mail.imap.host", host);
            properties.put("mail.imap.port", port);
            properties.put("mail.store.protocol", protocol);
            Session emailSession = Session.getDefaultInstance(properties);
            emailStore = emailSession.getStore();
            emailStore.connect(user, password);

            emailFolder = emailStore.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);

            return getNewMails(emailFolder);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (emailFolder != null && emailFolder.isOpen()) {
                    emailFolder.close(false);
                }
                if (emailStore != null && emailStore.isConnected()) {
                    emailStore.close();
                }
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<Mail> getNewMails(Folder emailFolder) throws MessagingException {
        List<Mail> mails =
                Arrays.stream(emailFolder.getMessages())
                        .filter(
                                it -> {
                                    try {
                                        return !it.getFlags().contains(Flags.Flag.SEEN);
                                    } catch (MessagingException e) {
                                        e.printStackTrace();
                                    }
                                    return false;
                                })
                        .map(MailMapper::map)
                        .collect(Collectors.toList());
        emailFolder.setFlags(1, emailFolder.getMessageCount(), new Flags(Flags.Flag.SEEN), true);
        return mails;
    }
}
