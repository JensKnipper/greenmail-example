package de.jensknipper.greenmailexample.control.mail;

import de.jensknipper.greenmailexample.control.mail.model.Mail;
import de.jensknipper.greenmailexample.control.mail.send.MailSendClient;
import de.jensknipper.greenmailexample.control.persistence.NoteRepository;
import de.jensknipper.greenmailexample.model.Note;
import org.springframework.stereotype.Service;

@Service
public final class NoteMailClient {
    private final NoteRepository noteRepository;
    private final MailSendClient sender;

    public NoteMailClient(NoteRepository noteRepository, MailSendClient sender) {
        this.noteRepository = noteRepository;
        this.sender = sender;
    }

    public void send(Note note) {
        sender.send(note.getEmail(), note.getTitle(), note.getText());
    }

    public void receive(Mail mail) {
        noteRepository.add(mail.getSubject(), mail.getContent(), mail.getFrom());
    }
}
