package com.efm.gymbro.model;
import com.efm.gymbro.model.Exercise;
import com.google.firebase.firestore.DocumentId;

import java.util.Date;
import java.util.List;

public class Program {
    @DocumentId
    private String id;
    private String name;
    private List<Exercise> exercises;
    private String userId;
    private Date createdAt;

    // Required empty constructor for Firestore
    public Program() {}

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Exercise> getExercises() { return exercises; }
    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}