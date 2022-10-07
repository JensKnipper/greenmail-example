package de.jensknipper.greenmailexample.control.mail.receive;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import de.jensknipper.greenmailexample.model.Mail;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.SocketUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MailReceiveClientTest {

    private static final String host = "localhost";

    private static final String smtpProtocol = "smtp";
    private static final int smtpPort = SocketUtils.findAvailableTcpPort();

    private static final String popProtocol = "pop3";
    private static final int popPort = SocketUtils.findAvailableTcpPort();

    private static final String imapProtocol = "imap";
    private static final int imapPort = SocketUtils.findAvailableTcpPort();

    private static final String username = "username@example.com";
    private static final String password = "password";

    private static final ServerSetup smtpServerSetup = new ServerSetup(smtpPort, host, smtpProtocol);
    private static final ServerSetup imapServerSetup = new ServerSetup(imapPort, host, imapProtocol);
    private static final ServerSetup popServerSetup = new ServerSetup(popPort, host, popProtocol);

    private static final ServerSetup[] setups = {
            imapServerSetup, popServerSetup, smtpServerSetup
    };

    private static final GreenMail greenMail = new GreenMail(setups);

    @BeforeAll
    static void beforeAll() {
        greenMail.setUser(username, password);
        greenMail.start();
    }

    @AfterAll
    static void afterAll() {
        greenMail.stop();
    }

    @AfterEach
    void afterEach() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
    }

    @Test
    public void testImapReceive() {
        final MailReceiveClient mailReceiveClient = new MailReceiveClient(imapProtocol, host, "" + imapPort, username, password);

        final String sender = GreenMailUtil.random() + "@example.com";
        final String subject = GreenMailUtil.random();
        final String text = GreenMailUtil.random();
        GreenMailUtil.sendTextEmail(username, sender, subject, text, smtpServerSetup);

        final List<Mail> mails = mailReceiveClient.receive();

        assertThat(mails.size()).isEqualTo(1);
        assertThat(mails.get(0).getFrom()).isEqualTo(sender);
        assertThat(mails.get(0).getRecipient()).isEqualTo(username);
        assertThat(mails.get(0).getSubject()).isEqualTo(subject);
        assertThat(mails.get(0).getContent()).isEqualTo(text);
    }

    @Test
    public void testPopReceive() {
        final MailReceiveClient mailReceiveClient = new MailReceiveClient(popProtocol, host, "" + popPort, username, password);

        final String sender = GreenMailUtil.random() + "@example.com";
        final String subject = GreenMailUtil.random();
        final String text = GreenMailUtil.random();
        GreenMailUtil.sendTextEmail(username, sender, subject, text, smtpServerSetup);

        final List<Mail> mails = mailReceiveClient.receive();

        assertThat(mails.size()).isEqualTo(1);
        assertThat(mails.get(0).getFrom()).isEqualTo(sender);
        assertThat(mails.get(0).getRecipient()).isEqualTo(username);
        assertThat(mails.get(0).getSubject()).isEqualTo(subject);
        assertThat(mails.get(0).getContent().trim()).isEqualTo(text);
    }

    @Test
    public void testReceiveMultipleMails() {
        final MailReceiveClient mailReceiveClient = new MailReceiveClient(imapProtocol, host, "" + imapPort, username, password);

        final String sender = GreenMailUtil.random() + "@example.com";
        final String subject = GreenMailUtil.random();
        final String text = GreenMailUtil.random();
        GreenMailUtil.sendTextEmail(username, sender, subject, text, smtpServerSetup);
        GreenMailUtil.sendTextEmail(username, sender, subject, text, smtpServerSetup);
        GreenMailUtil.sendTextEmail(username, sender, subject, text, smtpServerSetup);

        final List<Mail> mails = mailReceiveClient.receive();

        assertThat(mails.size()).isEqualTo(3);
        mails.forEach(mail -> {
            assertThat(mail.getFrom()).isEqualTo(sender);
            assertThat(mails.get(0).getRecipient()).isEqualTo(username);
            assertThat(mail.getSubject()).isEqualTo(subject);
            assertThat(mail.getContent()).isEqualTo(text);
        });
    }

    @Test
    public void testReceiveLatestMailOnly() {
        final MailReceiveClient mailReceiveClient = new MailReceiveClient(imapProtocol, host, "" + imapPort, username, password);

        GreenMailUtil.sendTextEmail(username, GreenMailUtil.random(), GreenMailUtil.random(), GreenMailUtil.random(), smtpServerSetup);
        mailReceiveClient.receive();

        final String sender = GreenMailUtil.random() + "@example.com";
        final String subject = GreenMailUtil.random();
        final String text = GreenMailUtil.random();
        GreenMailUtil.sendTextEmail(username, sender, subject, text, smtpServerSetup);

        final List<Mail> mails = mailReceiveClient.receive();

        assertThat(mails.size()).isEqualTo(1);
        assertThat(mails.get(0).getFrom()).isEqualTo(sender);
        assertThat(mails.get(0).getRecipient()).isEqualTo(username);
        assertThat(mails.get(0).getSubject()).isEqualTo(subject);
        assertThat(mails.get(0).getContent()).isEqualTo(text);
    }
}
