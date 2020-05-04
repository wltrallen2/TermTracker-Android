package com.fortysomethingnerd.android.termtracker.database;

public class NoteEntity {

    private int id;
    private int courseId;
    private String title;
    private String text;

    public NoteEntity() {
    }

    public NoteEntity(int courseId, String title, String text) {
        this.courseId = courseId;
        this.title = title;
        this.text = text;
    }

    public NoteEntity(int id, int courseId, String title, String text) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
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

    @Override
    public String toString() {
        return "NoteEntity{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
