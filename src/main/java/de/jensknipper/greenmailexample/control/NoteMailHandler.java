package de.jensknipper.greenmailexample.control;

import de.jensknipper.greenmailexample.control.mail.model.Mail;
import de.jensknipper.greenmailexample.control.mail.MailSendingHandler;
import de.jensknipper.greenmailexample.model.Note;
import org.springframework.stereotype.Service;

@Service
public final class NoteMailHandler {
  private final NoteRepository noteRepository;
  private final MailSendingHandler sender;

  public NoteMailHandler(NoteRepository noteRepository, MailSendingHandler sender) {
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
