package com.fortysomethingnerd.android.termtracker.database;

import java.util.Date;

public class AssessmentEntity {

    private int id;
    private int courseId;
    private String title;
    private Date goalDate;
    private Date dueDate;

    public AssessmentEntity() {
    }

    public AssessmentEntity(int id, int courseId, String title, Date goalDate, Date dueDate) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.goalDate = goalDate;
        this.dueDate = dueDate;
    }

    public AssessmentEntity(int courseId, String title, Date goalDate, Date dueDate) {
        this.courseId = courseId;
        this.title = title;
        this.goalDate = goalDate;
        this.dueDate = dueDate;
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

    public Date getGoalDate() {
        return goalDate;
    }

    public void setGoalDate(Date goalDate) {
        this.goalDate = goalDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "AssessmentEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", goalDate=" + goalDate +
                ", dueDate=" + dueDate +
                '}';
    }
}
