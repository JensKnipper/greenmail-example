package de.jensknipper.greenmailexample.control;

import de.jensknipper.greenmailexample.control.mail.MailSendingHandler;
import de.jensknipper.greenmailexample.model.Note;
import org.springframework.stereotype.Service;

@Service
public final class NoteMailHandler {
    private final MailSendingHandler sender;

    public NoteMailHandler( MailSendingHandler sender) {
        this.sender = sender;
    }

    public void send(Note note) {
        sender.send(note.getEmail(), note.getTitle(), note.getText());
    }

}
