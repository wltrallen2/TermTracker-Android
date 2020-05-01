package com.fortysomethingnerd.android.termtracker.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface AssessmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAssessment(AssessmentEntity assessment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AssessmentEntity> assessments);

    @Delete
    void deleteAssessment(AssessmentEntity assessment);

    @Query("SELECT * FROM assessments WHERE id = :id")
    AssessmentEntity getAssessmentById(int id);

    @Query("SELECT * FROM assessments WHERE courseId = :courseId")
    LiveData<List<AssessmentEntity>> getAssessmentsForCourseId(int courseId);

    @Query("SELECT * FROM assessments ORDER BY dueDate ASC")
    LiveData<List<AssessmentEntity>> getAll();

    @Query("DELETE FROM assessments")
    int deleteAll();

    @Query("DELETE FROM assessments WHERE courseId = :courseId")
    int deleteAssessmentsForCourseId(int courseId);

    @Query("SELECT COUNT(*) FROM assessments")
    int getCount();
}
