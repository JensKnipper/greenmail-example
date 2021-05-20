package de.jensknipper.greenmailexample.control.persistence;

import de.jensknipper.greenmailexample.model.Note;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public final class NoteRepository {
    private final List<Note> noteRepo = new ArrayList<>();
    private int maxId = 0;

    public Note add(String title, String text, String email) {
        Note note = new Note(maxId, title, text, email);
        noteRepo.add(note);
        maxId++;
        return note;
    }

    public Optional<Note> edit(int id, String title, String text, String email) {
        Optional<Note> note = getById(id);
        note.ifPresent(
                it -> {
                    it.setTitle(title);
                    it.setText(text);
                    it.setEmail(email);
                });
        return note;
    }

    public Optional<Note> delete(int id) {
        Optional<Note> note = getById(id);
        note.ifPresent(noteRepo::remove);
        return note;
    }

    public List<Note> getAll() {
        return noteRepo.stream()
                .sorted(Comparator.comparingInt(Note::getId))
                .collect(Collectors.toList());
    }

    public void deleteAll() {
        noteRepo.clear();
        maxId = 0;
    }

    public Optional<Note> getById(int id) {
        return noteRepo.stream().filter(it -> it.getId() == id).findAny();
    }
}
