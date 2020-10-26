package de.jensknipper.greenmailexample.control.mail;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import de.jensknipper.greenmailexample.control.mail.model.Mail;
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
class MailReceivingHandlerTest {

  @Autowired private MailReceivingHandler mailReceivingHandler;

  @Value("${mail.imap.host}")
  private String imapHost;

  @Value("${spring.mail.host}")
  private String smtpHost;

  @Value("${mail.imap.port}")
  private Integer imapPort;

  @Value("${spring.mail.port}")
  private Integer smtpPort;

  @Value("${spring.mail.username}")
  private String username;

  @Value("${spring.mail.password}")
  private String password;

  @Test
  public void testSend() {
    final ServerSetup[] setup = {
      new ServerSetup(imapPort, imapHost, "imap"),
      new ServerSetup(smtpPort, smtpHost, "smtp")
    };
    final GreenMail greenMail = new GreenMail(setup);
    greenMail.setUser(username, password);

    greenMail.start();

    final String sender = GreenMailUtil.random();
    final String subject = GreenMailUtil.random();
    final String text = GreenMailUtil.random();
    GreenMailUtil.sendTextEmailTest(username, sender, subject, text);

    final List<Mail> mails = mailReceivingHandler.receive();

    assertThat(mails).isNotEmpty();
    assertThat(mails.get(0).getSubject()).isEqualTo(subject);
    assertThat(mails.get(0).getContent()).isEqualTo(text);

    greenMail.stop();
  }
}
