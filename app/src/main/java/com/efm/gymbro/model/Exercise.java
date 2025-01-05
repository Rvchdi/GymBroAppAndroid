package com.efm.gymbro.model;

import java.util.List;

public class Exercise {
    private String id;
    private String name;
    private String type;
    private String level;
    private List<String> targetMuscles;
    private String category;
    private List<String> equipment;
    private int sets;
    private String reps;
    private boolean isSelected;

    // Empty constructor for Firebase
    public Exercise() {}

    public Exercise(String id, String name, String type, String level, List<String> targetMuscles,
                    String category, List<String> equipment, int sets, String reps) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.level = level;
        this.targetMuscles = targetMuscles;
        this.category = category;
        this.equipment = equipment;
        this.sets = sets;
        this.reps = reps;
        this.isSelected = false;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public List<String> getTargetMuscles() { return targetMuscles; }
    public void setTargetMuscles(List<String> targetMuscles) { this.targetMuscles = targetMuscles; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getEquipment() { return equipment; }
    public void setEquipment(List<String> equipment) { this.equipment = equipment; }

    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }

    public String getReps() { return reps; }
    public void setReps(String reps) { this.reps = reps; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exercise exercise = (Exercise) o;
        return id != null && id.equals(exercise.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}