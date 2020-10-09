package de.jensknipper.greenmailexample.control.mail.model;

public final class Mail {
  private final String subject;
  private final String content;
  private final String from;

  public Mail(String subject, String content, String from) {
    this.subject = subject;
    this.content = content;
    this.from = from;
  }

  public String getSubject() {
    return subject;
  }

  public String getContent() {
    return content;
  }

  public String getFrom() {
    return from;
  }
}
