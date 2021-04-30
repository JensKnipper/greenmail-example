package de.jensknipper.greenmailexample.control.web;

import de.jensknipper.greenmailexample.control.mail.NoteMailClient;
import de.jensknipper.greenmailexample.control.persistence.NoteRepository;
import de.jensknipper.greenmailexample.control.web.dto.NoteDto;
import de.jensknipper.greenmailexample.model.Note;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public final class ViewController {
    private final NoteRepository noteRepository;
    private final NoteMailClient noteMailClient;

    public ViewController(NoteRepository noteRepository, NoteMailClient noteMailClient) {
        this.noteRepository = noteRepository;
        this.noteMailClient = noteMailClient;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("notes", noteRepository.getAll());
        model.addAttribute("noteDto", new NoteDto());
        return "home";
    }

    @GetMapping("/note")
    public String createNote(Model model) {
        model.addAttribute("noteDto", new NoteDto());
        return "note";
    }

    @PostMapping("/note")
    public String createNote(
            @ModelAttribute NoteDto noteDto, BindingResult bindingResult, Model model) {
        noteRepository.add(noteDto.getTitle(), noteDto.getText(), noteDto.getEmail());
        return "redirect:/";
    }

    @GetMapping(value = "/note/{noteId}")
    public String editNote(@PathVariable int noteId, Model model) {
        Optional<Note> note = noteRepository.getById(noteId);
        if (note.isEmpty()) {
            return "redirect:/note";
        }
        model.addAttribute(
                "noteDto",
                new NoteDto(
                        note.get().getId(),
                        note.get().getTitle(),
                        note.get().getText(),
                        note.get().getEmail()));
        return "note";
    }

    @PostMapping(value = "/note/{noteId}")
    public String editNote(
            @PathVariable int noteId,
            @ModelAttribute NoteDto noteDto,
            BindingResult bindingResult,
            Model model) {
        noteRepository.edit(noteId, noteDto.getTitle(), noteDto.getText(), noteDto.getEmail());
        return "redirect:/";
    }

    @GetMapping(value = "/note/{noteId}/delete")
    public String deleteNote(@PathVariable int noteId, Model model) {
        noteRepository.delete(noteId);
        return "redirect:/";
    }

    @GetMapping(value = "/note/{noteId}/mail")
    public String mailNote(@PathVariable int noteId, Model model) {
        Optional<Note> note = noteRepository.getById(noteId);
        if (note.isEmpty()) {
            return "redirect:/";
        }
        noteMailClient.send(note.get());
        return "redirect:/";
    }
}
