package de.jensknipper.greenmailexample.control.mail;

import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import de.jensknipper.greenmailexample.control.mail.model.Mail;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
// @ActiveProfiles("test") // in an active working environment you might set a profile for
// integration testing
@RunWith(SpringRunner.class)
class MailReceivingHandlerTest {

  @Autowired private MailReceivingHandler mailReceivingHandler;

  @Value("${mail.imap.port}")
  private Integer imapPort;

  @Value("${spring.mail.port}")
  private Integer smtpPort;

  @Value("${spring.mail.username}")
  private String username;

  @Value("${spring.mail.password}")
  private String password;

  @Test
  public void testSend() throws MessagingException {
    final ServerSetup[] setup = {
      new ServerSetup(imapPort, null, "imap"), new ServerSetup(smtpPort, null, "smtp")
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

    greenMail.stop();
  }
}
