package de.jensknipper.greenmailexample.control.mail.receive;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import de.jensknipper.greenmailexample.model.Mail;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
class MailReceiveClientTest {

    @Autowired
    private MailReceiveClient mailReceiveClient;

    @Value("${mail.store.host}")
    private String storeHost;

    @Value("${spring.mail.host}")
    private String smtpHost;

    @Value("${mail.store.protocol}")
    private String storeProtocol;

    @Value("${mail.store.port}")
    private Integer storePort;

    @Value("${spring.mail.port}")
    private Integer smtpPort;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Test
    public void testReceive() {
        final ServerSetup storeSetup = new ServerSetup(storePort, storeHost, storeProtocol);
        final ServerSetup smtpSetup = new ServerSetup(smtpPort, smtpHost, "smtp");
        final ServerSetup[] setup = {
                storeSetup, smtpSetup
        };
        final GreenMail greenMail = new GreenMail(setup);
        greenMail.setUser(username, password);
        greenMail.start();

        final String sender = GreenMailUtil.random();
        final String subject = GreenMailUtil.random();
        final String text = GreenMailUtil.random();
        GreenMailUtil.sendTextEmail(username, sender, subject, text, smtpSetup);

        final List<Mail> mails = mailReceiveClient.receive();

        assertThat(mails).isNotEmpty();
        assertThat(mails.get(0).getSubject()).isEqualTo(subject);
        assertThat(mails.get(0).getContent()).isEqualTo(text);

        greenMail.stop();
    }
}
