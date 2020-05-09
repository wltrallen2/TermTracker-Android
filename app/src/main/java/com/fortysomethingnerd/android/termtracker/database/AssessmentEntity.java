package com.fortysomethingnerd.android.termtracker.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "assessments")
public class AssessmentEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int courseId;
    private String title;
    private Date goalDate;
    private boolean isGoalAlarmActive;
    private Date dueDate;

    @Ignore
    public AssessmentEntity() {
    }

    public AssessmentEntity(int id, int courseId, String title, Date goalDate, boolean isGoalAlarmActive, Date dueDate) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.goalDate = goalDate;
        this.isGoalAlarmActive = isGoalAlarmActive;
        this.dueDate = dueDate;
    }

    @Ignore
    public AssessmentEntity(int courseId, String title, Date goalDate, boolean isGoalAlarmActive, Date dueDate) {
        this.courseId = courseId;
        this.title = title;
        this.goalDate = goalDate;
        this.isGoalAlarmActive = isGoalAlarmActive;
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

    public boolean isGoalAlarmActive() {
        return isGoalAlarmActive;
    }

    public void setGoalAlarmActive(boolean goalAlarmActive) {
        isGoalAlarmActive = goalAlarmActive;
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
                ", courseId=" + courseId +
                ", title='" + title + '\'' +
                ", goalDate=" + goalDate +
                ", isGoalAlarmActive=" + isGoalAlarmActive +
                ", dueDate=" + dueDate +
                '}';
    }
}
