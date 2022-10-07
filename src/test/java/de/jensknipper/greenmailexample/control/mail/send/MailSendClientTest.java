package de.jensknipper.greenmailexample.control.mail.send;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.SocketUtils;

import javax.mail.Message;
import javax.mail.MessagingException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class MailSendClientTest {

  @Autowired private MailSendClient mailSendClient;

  private static final String storeHost = "localhost";
  private static final String smtpHost= "localhost";
  private static final String storeProtocol = "imap";
  private static final int storePort= SocketUtils.findAvailableTcpPort();
  private static final  int smtpPort= SocketUtils.findAvailableTcpPort();
  private static final  String username ="username";
  private static final  String password ="password";

  @DynamicPropertySource
  static void registerProperties(final DynamicPropertyRegistry registry) {
    registry.add("mail.store.host", () -> storeHost);
    registry.add("spring.mail.host", () -> smtpHost);
    registry.add("mail.store.protocol", () -> storeProtocol);
    registry.add("mail.store.port", () -> storePort);
    registry.add("spring.mail.port", () -> smtpPort);
    registry.add("spring.mail.username", () -> username);
    registry.add("spring.mail.password", () -> password);
  }

  @Test
  public void testSend() throws MessagingException {
    final ServerSetup[] setup = {
      new ServerSetup(storePort, storeHost, storeProtocol),
      new ServerSetup(smtpPort, smtpHost, "smtp")
    };
    final GreenMail greenMail = new GreenMail(setup);
    greenMail.setUser(username, password);
    greenMail.start();

    final String recipient = GreenMailUtil.random();
    final String subject = GreenMailUtil.random();
    final String text = GreenMailUtil.random();
    mailSendClient.send(recipient, subject, text);

    assertThat(greenMail.waitForIncomingEmail(5000, 1)).isTrue();
    final Message[] messages = greenMail.getReceivedMessages();

    assertThat(messages.length).isEqualTo(1);
    assertThat(messages[0].getSubject()).isEqualTo(subject);
    assertThat(GreenMailUtil.getBody(messages[0]).trim()).isEqualTo(text);

    greenMail.stop();
  }
}
