package de.jensknipper.greenmailexample.control.mail.e2e;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import de.jensknipper.greenmailexample.control.mail.util.DriverSelector;
import de.jensknipper.greenmailexample.control.persistence.NoteRepository;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.SocketUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class E2eTest {
    private static final String EXAMPLE_MAIL_ADDRESS = "mail@example.com";
    private static final String EXAMPLE_NOTE_TITLE = "title123";
    private static final String EXAMPLE_NOTE_TEXT = "text123";

    private static final String storeHost = "localhost";
    private static final String smtpHost= "localhost";
    private static final String storeProtocol = "imap";
    private static final int storePort= SocketUtils.findAvailableTcpPort();
    private static final  int smtpPort= SocketUtils.findAvailableTcpPort();
    private static final  String username ="username";
    private static final  String password ="password";
    private  static final int schedulerInterval = 1000;

    private static final ServerSetup storeSetup = new ServerSetup(storePort, storeHost, storeProtocol);
    private static final ServerSetup smtpSetup= new ServerSetup(smtpPort, smtpHost, "smtp");
    private static final  ServerSetup[] setup = {storeSetup, smtpSetup};
    private static final GreenMail greenMail = new GreenMail(setup);
    private static WebDriver driver;

    @DynamicPropertySource
    static void registerProperties(final DynamicPropertyRegistry registry) {
        registry.add("mail.store.host", () -> storeHost);
        registry.add("spring.mail.host", () -> smtpHost);
        registry.add("mail.store.protocol", () -> storeProtocol);
        registry.add("mail.store.port", () -> storePort);
        registry.add("spring.mail.port", () -> smtpPort);
        registry.add("spring.mail.username", () -> username);
        registry.add("spring.mail.password", () -> password);
        registry.add("mail.receive.schedule.interval.milliseconds", () -> schedulerInterval);
    }

    @Autowired
    private NoteRepository noteRepository;

    @LocalServerPort
    private int port;

    @BeforeAll
    public static void beforeAll() {
        greenMail.setUser(username, password);
        greenMail.start();

        System.setProperty("webdriver.gecko.driver", DriverSelector.getDriver().toString());
        driver = new FirefoxDriver(new FirefoxOptions().setHeadless(true));
    }

    @AfterAll
    public static void afterAll() {
        driver.quit();
        greenMail.stop();
    }

    @AfterEach
    public void afterEach() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
        noteRepository.deleteAll();
    }

    @Test
    public void sendingAMailShouldCreateANote() throws InterruptedException {
        GreenMailUtil.sendTextEmail(username, EXAMPLE_MAIL_ADDRESS, EXAMPLE_NOTE_TITLE, EXAMPLE_NOTE_TEXT, smtpSetup);

        // wait until scheduler picks up mail
        Thread.sleep(schedulerInterval + 100);

        driver.get("localhost:" + port);

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(it -> it.getTitle().startsWith("Your Notes"));

        assertThat(driver.getPageSource().contains(EXAMPLE_MAIL_ADDRESS)).isTrue();
        assertThat(driver.getPageSource().contains(EXAMPLE_NOTE_TITLE)).isTrue();
        assertThat(driver.getPageSource().contains(EXAMPLE_NOTE_TEXT)).isTrue();
    }

    @Test
    public void creatingANoteAndMailingItShouldSendTheNoteViaMail() throws MessagingException {

        driver.get("localhost:" + port);

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(it -> it.getPageSource().contains("No Notes Available"));

        WebElement titleField = driver.findElement(By.id("title"));
        titleField.sendKeys(EXAMPLE_NOTE_TITLE);
        WebElement textField = driver.findElement(By.id("text"));
        textField.sendKeys(EXAMPLE_NOTE_TEXT);
        WebElement mailField = driver.findElement(By.id("email"));
        mailField.sendKeys(EXAMPLE_MAIL_ADDRESS);

        WebElement submitButton = driver.findElement(By.id("submit"));
        submitButton.click();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(it -> it.getTitle().startsWith("Your Notes"));

        assertThat(driver.getPageSource().contains(EXAMPLE_MAIL_ADDRESS)).isTrue();
        assertThat(driver.getPageSource().contains(EXAMPLE_NOTE_TITLE)).isTrue();
        assertThat(driver.getPageSource().contains(EXAMPLE_NOTE_TEXT)).isTrue();

        WebElement mailSendButton = driver.findElement(By.id("mail-send-0"));
        mailSendButton.click();

        assertThat(greenMail.waitForIncomingEmail(5000, 1)).isTrue();
        final Message[] messages = greenMail.getReceivedMessages();

        assertThat(messages.length).isEqualTo(1);
        assertThat(messages[0].getSubject()).isEqualTo(EXAMPLE_NOTE_TITLE);
        assertThat(GreenMailUtil.getBody(messages[0]).trim()).isEqualTo(EXAMPLE_NOTE_TEXT);
    }
}
