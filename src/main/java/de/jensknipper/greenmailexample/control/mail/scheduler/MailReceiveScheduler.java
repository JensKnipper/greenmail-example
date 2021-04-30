package de.jensknipper.greenmailexample.control.mail.scheduler;

import de.jensknipper.greenmailexample.control.mail.NoteMailClient;
import de.jensknipper.greenmailexample.control.mail.receive.MailReceiveClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MailReceiveScheduler {

    private final MailReceiveClient mailReceiveClient;
    private final NoteMailClient noteMailClient;

    public MailReceiveScheduler(
            MailReceiveClient mailReceiveClient, NoteMailClient noteMailClient) {
        this.mailReceiveClient = mailReceiveClient;
        this.noteMailClient = noteMailClient;
    }

    @Scheduled(fixedRate = 3000)
    public void receiveAndSaveNotes() {
        mailReceiveClient.receive().forEach(noteMailClient::receive);
    }
}
