package de.jensknipper.greenmailexample.model;

public final class Note {
    private final int id;
    private String title;
    private String text;
    private String email;

    public Note(int id, String title, String text, String email) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.email = email;
    }

    public int getId() {
        return id;
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
