package de.jensknipper.greenmailexample.control.mail;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.Message;
import javax.mail.MessagingException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
//@ActiveProfiles("test") // in an active working environment you might set a profile for integration testing
@RunWith(SpringRunner.class)
class MailSendingHandlerTest {

    @Autowired
    private MailSendingHandler mailSendingHandler;

    @Value("${spring.mail.port}")
    private Integer port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Test
    public void testSend() throws MessagingException {
        final GreenMail greenMail = new GreenMail(new ServerSetup(port, null, "smtp"));
        greenMail.setUser(username, password);

        final String recipient = GreenMailUtil.random();
        final String subject = GreenMailUtil.random();
        final String text = GreenMailUtil.random();

        greenMail.start();

        mailSendingHandler.send(recipient, subject, text);

        assertThat(greenMail.waitForIncomingEmail(5000, 1)).isTrue();
        Message[] messages = greenMail.getReceivedMessages();
        greenMail.stop();

        assertThat(messages.length).isEqualTo(1);
        assertThat(messages[0].getSubject()).isEqualTo(subject);
        assertThat(GreenMailUtil.getBody(messages[0]).trim()).isEqualTo(text);


    }
}