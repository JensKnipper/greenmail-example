package de.jensknipper.greenmailexample.control.mail.scheduler;

import de.jensknipper.greenmailexample.control.NoteMailHandler;
import de.jensknipper.greenmailexample.control.mail.MailReceivingHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MailReceivingScheduler {

    private final MailReceivingHandler mailReceivingHandler;
    private final NoteMailHandler noteMailHandler;

    public MailReceivingScheduler(
            MailReceivingHandler mailReceivingHandler, NoteMailHandler noteMailHandler) {
        this.mailReceivingHandler = mailReceivingHandler;
        this.noteMailHandler = noteMailHandler;
    }

    @Scheduled(fixedRate = 3000)
    public void receiveAndSaveNotes() {
        mailReceivingHandler.receive().forEach(noteMailHandler::receive);
    }
}
