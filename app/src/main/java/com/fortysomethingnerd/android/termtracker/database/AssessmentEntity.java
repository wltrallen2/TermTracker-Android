package com.fortysomethingnerd.android.termtracker.database;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fortysomethingnerd.android.termtracker.utilities.AssessmentType;

import java.util.Date;

@Entity(tableName = "assessments")
public class AssessmentEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int courseId;
    private String title;
    private AssessmentType type;
    private Date goalDate;
    private boolean isGoalAlarmActive;
    private Date dueDate;
    private boolean isDueAlarmActive;

    @Ignore
    public AssessmentEntity() {
    }

    public AssessmentEntity(int id, int courseId, String title, AssessmentType type, Date goalDate, boolean isGoalAlarmActive, Date dueDate, boolean isDueAlarmActive) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.type = type;
        this.goalDate = goalDate;
        this.isGoalAlarmActive = isGoalAlarmActive;
        this.dueDate = dueDate;
        this.isDueAlarmActive = isDueAlarmActive;
    }

    @Ignore
    public AssessmentEntity(int courseId, String title, AssessmentType type, Date goalDate, boolean isGoalAlarmActive, Date dueDate, boolean isDueAlarmActive) {
        this.courseId = courseId;
        this.title = title;
        this.type = type;
        this.goalDate = goalDate;
        this.isGoalAlarmActive = isGoalAlarmActive;
        this.dueDate = dueDate;
        this.isDueAlarmActive = isDueAlarmActive;
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

    public AssessmentType getType() {
        return type;
    }

    public void setType(AssessmentType type) {
        this.type = type;
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

    public boolean isDueAlarmActive() {
        return isDueAlarmActive;
    }

    public void setDueAlarmActive(boolean dueAlarmActive) {
        isDueAlarmActive = dueAlarmActive;
    }

    @Override
    public String toString() {
        return "AssessmentEntity{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", goalDate=" + goalDate +
                ", isGoalAlarmActive=" + isGoalAlarmActive +
                ", dueDate=" + dueDate +
                ", isDueAlarmActive=" + isDueAlarmActive +
                '}';
    }
}
