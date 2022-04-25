package de.jensknipper.greenmailexample.control.mail.receive;

import de.jensknipper.greenmailexample.control.mail.mapper.MailMapper;
import de.jensknipper.greenmailexample.model.Mail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public final class MailReceiveClient {

    private final String protocol;
    private final String host;
    private final String port;
    private final String user;
    private final String password;

    public MailReceiveClient(@Value("${mail.store.protocol}") String protocol,
                             @Value("${mail.store.host}") String host,
                             @Value("${mail.store.port}") String port,
                             @Value("${spring.mail.username}") String user,
                             @Value("${spring.mail.password}") String password) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public List<Mail> receive() {
        Store emailStore = null;
        Folder emailFolder = null;

        Properties properties = new Properties();
        properties.put("mail.store.protocol", protocol);
        properties.put("mail." + protocol + ".host", host);
        properties.put("mail." + protocol + ".port", port);
        Session emailSession = Session.getInstance(properties);

        try {
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
        List<Mail> mails = new ArrayList<>();
        for (Message message : emailFolder.getMessages()) {
            if (!message.getFlags().contains(Flags.Flag.SEEN)) {
                message.setFlags(new Flags(Flags.Flag.SEEN), true);
                Mail mail = MailMapper.map(message, user);
                mails.add(mail);
            }
        }
        return mails;
    }
}
