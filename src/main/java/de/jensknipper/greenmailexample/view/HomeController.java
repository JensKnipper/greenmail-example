package de.jensknipper.greenmailexample.view;

import de.jensknipper.greenmailexample.control.NoteMailHandler;
import de.jensknipper.greenmailexample.control.NoteRepository;
import de.jensknipper.greenmailexample.model.Note;
import de.jensknipper.greenmailexample.view.dto.NoteDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public final class HomeController {
  private final NoteRepository noteRepository;
  private final NoteMailHandler noteMailHandler;

  public HomeController(NoteRepository noteRepository, NoteMailHandler noteMailHandler) {
    this.noteRepository = noteRepository;
    this.noteMailHandler = noteMailHandler;
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
    return "home";
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
    Optional<Note> note = noteRepository.getById(noteId);
    if (note.isEmpty()) {
      return "redirect:/note";
    }
    noteRepository.edit(
        note.get().getId(),
        note.get().getTitle(),
        note.get().getText(),
        note.get().getEmail()); // TODO edit funktioniert nicht
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
    noteMailHandler.send(note.get());
    return "redirect:/";
  }
}
