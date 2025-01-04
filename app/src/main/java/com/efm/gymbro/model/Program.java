package com.efm.gymbro.model;


import com.google.firebase.Timestamp;
import java.util.Date;
import java.util.List;


public class Program {
    private String id;
    private String name;
    private List<Exercise> exercises;
    private String userId;
    private String description;
    private String difficulty;
    private Timestamp createdAt;
    private int durationMinutes;
    private String category;

    public Program() {} //

    public Program(String name, List<Exercise> exercises, String userId) {
        this.name = name;
        this.exercises = exercises;
        this.userId = userId;
        this.createdAt = new Timestamp(new Date().getTime()/1000, 0);
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Exercise> getExercises() { return exercises; }
    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}