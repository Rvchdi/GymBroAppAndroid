package com.efm.gymbro.model;

import com.google.firebase.firestore.DocumentId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Session {
    @DocumentId
    private String id;
    private String programId;
    private String userId;
    private Date startTime;
    private Date endTime;
    private List<ExerciseSet> exerciseSets;
    private String notes;

    public Session() {
        // Required empty constructor for Firestore
    }

    // Nested class to track exercise sets
    public static class ExerciseSet {
        private String exerciseId;
        private String exerciseName;
        private int setNumber;
        private double weight;
        private int reps;
        private boolean completed;

        public ExerciseSet() {}

        public ExerciseSet(String exerciseId, String exerciseName, int setNumber, double weight, int reps) {
            this.exerciseId = exerciseId;
            this.exerciseName = exerciseName;
            this.setNumber = setNumber;
            this.weight = weight;
            this.reps = reps;
            this.completed = false;
        }

        // Getters and setters
        public String getExerciseId() { return exerciseId; }
        public void setExerciseId(String exerciseId) { this.exerciseId = exerciseId; }

        public String getExerciseName() { return exerciseName; }
        public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

        public int getSetNumber() { return setNumber; }
        public void setSetNumber(int setNumber) { this.setNumber = setNumber; }

        public double getWeight() { return weight; }
        public void setWeight(double weight) { this.weight = weight; }

        public int getReps() { return reps; }
        public void setReps(int reps) { this.reps = reps; }

        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProgramId() { return programId; }
    public void setProgramId(String programId) { this.programId = programId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public List<ExerciseSet> getExerciseSets() { return exerciseSets; }
    public void setExerciseSets(List<ExerciseSet> exerciseSets) { this.exerciseSets = exerciseSets; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}