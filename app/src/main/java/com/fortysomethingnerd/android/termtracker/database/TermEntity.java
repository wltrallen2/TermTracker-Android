package com.fortysomethingnerd.android.termtracker.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "terms")
public class TermEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private Date start;
    private Date end;

    @Ignore
    public TermEntity() {
    }

    public TermEntity(int id, String title, Date start, Date end) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
    }

    @Ignore
    public TermEntity(String title, Date start, Date end) {
        this.title = title;
        this.start = start;
        this.end = end;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "TermEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }

    public String getStartString() {
        return DateConverter.parseDateToString(start);
    }

    public String getEndString() {
        return DateConverter.parseDateToString(end);
    }
}
