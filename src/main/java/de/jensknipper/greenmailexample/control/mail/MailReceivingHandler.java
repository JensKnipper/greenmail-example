package de.jensknipper.greenmailexample.control.mail;

import de.jensknipper.greenmailexample.control.mail.mapper.MailMapper;
import de.jensknipper.greenmailexample.control.mail.model.Mail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Component
public final class MailReceivingHandler {

  private final String protocol;
  private final String imapHost;
  private final Integer imapPort;
  private final String mailUser;
  private final String mailUserPassword;

  private Store emailStore;
  private Folder emailFolder;

  public MailReceivingHandler(Environment env) {
    protocol = env.getProperty("mail.store.protocol");
    imapHost = env.getProperty("mail.imap.host");
    imapPort = Integer.parseInt(env.getProperty("mail.imap.port"));
    mailUser = env.getProperty("spring.mail.username");
    mailUserPassword = env.getProperty("spring.mail.password");
  }

  public List<Mail> receive() {
    try {
      initMailStore();
      initMailFolder();

      List<Mail> mails = getNewMails();

      if (emailFolder.isOpen()) {
        emailFolder.close(false);
      }
      if (emailStore.isConnected()) {
        emailStore.close();
      }

      return mails;

    } catch (MessagingException e) {
      e.printStackTrace();
    }
    return Collections.emptyList();
  }

  private void initMailStore() throws MessagingException {
    if (emailStore == null) {
      Properties properties = new Properties();
      properties.put("mail.imap.host", imapHost);
      properties.put("mail.store.protocol", protocol);
      Session emailSession = Session.getDefaultInstance(properties);
      emailStore = emailSession.getStore();
    }
    if (!emailStore.isConnected()) {
      emailStore.connect(imapHost, imapPort, mailUser, mailUserPassword);
    }
  }

  private void initMailFolder() throws MessagingException {
    if (emailFolder == null) {
      emailFolder = emailStore.getFolder("INBOX");
    }
    if (!emailFolder.isOpen()) {
      emailFolder.open(Folder.READ_WRITE);
    }
  }

  private List<Mail> getNewMails() throws MessagingException {
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
            .map(it -> new MailMapper().map(it))
            .collect(Collectors.toList());
    emailFolder.setFlags(1, emailFolder.getMessageCount(), new Flags(Flags.Flag.SEEN), true);
    return mails;
  }
}
