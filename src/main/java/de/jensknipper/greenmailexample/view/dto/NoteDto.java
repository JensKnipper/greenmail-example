package de.jensknipper.greenmailexample.view.dto;

public final class NoteDto {
  private Integer id;
  private String title;
  private String text;
  private String email;

  public NoteDto() {}

  public NoteDto(Integer id, String title, String text, String email) {
    this.id = id;
    this.title = title;
    this.text = text;
    this.email = email;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
