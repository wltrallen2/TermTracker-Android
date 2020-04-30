package com.fortysomethingnerd.android.termtracker.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fortysomethingnerd.android.termtracker.utilities.CourseStatus;

import java.util.Date;

@Entity(tableName = "courses")
public class CourseEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int termId;
    private String title;
    private Date start;
    private Date end;
    private CourseStatus status;
    private String mentorName;
    private String mentorPhone;
    private String mentorEmail;

    @Ignore
    public CourseEntity() {
    }

    public CourseEntity(int id, int termId, String title, Date start, Date end, CourseStatus status, String mentorName, String mentorPhone, String mentorEmail) {
        this.id = id;
        this.termId = termId;
        this.title = title;
        this.start = start;
        this.end = end;
        this.status = status;
        this.mentorName = mentorName;
        this.mentorPhone = mentorPhone;
        this.mentorEmail = mentorEmail;
    }

    @Ignore
    public CourseEntity(int termId, String title, Date start, Date end, CourseStatus status, String mentorName, String mentorPhone, String mentorEmail) {
        this.termId = termId;
        this.title = title;
        this.start = start;
        this.end = end;
        this.status = status;
        this.mentorName = mentorName;
        this.mentorPhone = mentorPhone;
        this.mentorEmail = mentorEmail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
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

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public String getMentorName() {
        return mentorName;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public String getMentorPhone() {
        return mentorPhone;
    }

    public void setMentorPhone(String mentorPhone) {
        this.mentorPhone = mentorPhone;
    }

    public String getMentorEmail() {
        return mentorEmail;
    }

    public void setMentorEmail(String mentorEmail) {
        this.mentorEmail = mentorEmail;
    }

    @Override
    public String toString() {
        return "CourseEntity{" +
                "id=" + id +
                ", termId=" + termId +
                ", title='" + title + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", status=" + status +
                ", mentorName='" + mentorName + '\'' +
                ", mentorPhone='" + mentorPhone + '\'' +
                ", mentorEmail='" + mentorEmail + '\'' +
                '}';
    }
}
