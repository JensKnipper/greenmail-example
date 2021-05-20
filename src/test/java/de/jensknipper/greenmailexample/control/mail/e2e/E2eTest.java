package de.jensknipper.greenmailexample.control.mail.e2e;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import de.jensknipper.greenmailexample.control.persistence.NoteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class E2eTest {

    @Value("${mail.store.host}")
    private String storeHost;

    @Value("${spring.mail.host}")
    private String smtpHost;

    @Value("${mail.store.port}")
    private Integer storePort;

    @Value("${spring.mail.port}")
    private Integer smtpPort;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Autowired
    private NoteRepository noteRepository;

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private GreenMail greenMail;
    private ServerSetup smtpSetup;

    @BeforeEach
    public void setup() throws FolderException {
        ServerSetup storeSetup = new ServerSetup(storePort, storeHost, "imap");
        smtpSetup = new ServerSetup(smtpPort, smtpHost, "smtp");
        final ServerSetup[] setup = {
                storeSetup, smtpSetup
        };
        greenMail = new GreenMail(setup);
        greenMail.setUser(username, password);
        greenMail.start();

        Path path = FileSystems.getDefault().getPath("src/test/resources/geckodriver.exe");
        System.setProperty("webdriver.gecko.driver", path.toString());
        driver = new FirefoxDriver();

        greenMail.purgeEmailFromAllMailboxes();
        noteRepository.deleteAll();
    }

    @AfterEach
    public void teardown() {
        driver.quit();
        greenMail.stop();
    }

    @Test
    public void sendingAMailShouldCreateANote() throws InterruptedException {
        GreenMailUtil.sendTextEmail(username, "mail@example.com", "title123", "text123", smtpSetup);

        // wait until scheduler picks up mail
        Thread.sleep(3100);

        driver.get("localhost:" + port);

        new WebDriverWait(driver, 5).until(it -> it.getTitle().startsWith("Your Notes"));

        assertThat(driver.getPageSource().contains("mail@example.com")).isTrue();
        assertThat(driver.getPageSource().contains("title123")).isTrue();
        assertThat(driver.getPageSource().contains("text123")).isTrue();
    }

    @Test
    public void creatingANoteAndMailingItShouldSendTheNoteViaMail() throws MessagingException {

        driver.get("localhost:" + port);

        new WebDriverWait(driver, 5).until(it -> it.getPageSource().contains("No Notes Available"));

        WebElement titleField = driver.findElement(By.id("title"));
        titleField.sendKeys("title123");
        WebElement textField = driver.findElement(By.id("text"));
        textField.sendKeys("text123");
        WebElement mailField = driver.findElement(By.id("email"));
        mailField.sendKeys("mail@example.com");

        WebElement submitButton = driver.findElement(By.id("submit"));
        submitButton.click();

        new WebDriverWait(driver, 5).until(it -> it.getTitle().startsWith("Your Notes"));

        WebElement mailSendButton = driver.findElement(By.id("mail-send-0"));
        mailSendButton.click();

        assertThat(greenMail.waitForIncomingEmail(5000, 1)).isTrue();
        final Message[] messages = greenMail.getReceivedMessages();

        assertThat(messages.length).isEqualTo(1);
        assertThat(messages[0].getSubject()).isEqualTo("title123");
        assertThat(GreenMailUtil.getBody(messages[0]).trim()).isEqualTo("text123");
    }
}
