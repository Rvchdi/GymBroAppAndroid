package com.efm.gymbro.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.DocumentId;
import java.util.ArrayList;
import java.util.List;

public class Exercise implements Parcelable {
    @DocumentId
    private String id;
    private String name;
    private String description;
    private String type;
    private String level;
    private String category;
    private List<String> equipment;
    private List<String> targetMuscles;
    private String creatorId;
    private int sets;
    private String reps;

    public Exercise() {
        targetMuscles = new ArrayList<>();
        equipment = new ArrayList<>();
    }

    protected Exercise(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        type = in.readString();
        level = in.readString();
        category = in.readString();
        equipment = new ArrayList<>();
        in.readStringList(equipment);
        targetMuscles = new ArrayList<>();
        in.readStringList(targetMuscles);
        creatorId = in.readString();
        sets = in.readInt();
        reps = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(type);
        dest.writeString(level);
        dest.writeString(category);
        dest.writeStringList(equipment);
        dest.writeStringList(targetMuscles);
        dest.writeString(creatorId);
        dest.writeInt(sets);
        dest.writeString(reps);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<String> equipment) {
        this.equipment = equipment != null ? equipment : new ArrayList<>();
    }

    public List<String> getTargetMuscles() {
        return targetMuscles;
    }

    public void setTargetMuscles(List<String> targetMuscles) {
        this.targetMuscles = targetMuscles != null ? targetMuscles : new ArrayList<>();
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public void addEquipment(String item) {
        if (equipment == null) {
            equipment = new ArrayList<>();
        }
        equipment.add(item);
    }

    public void removeEquipment(String item) {
        if (equipment != null) {
            equipment.remove(item);
        }
    }

    public void addTargetMuscle(String muscle) {
        if (targetMuscles == null) {
            targetMuscles = new ArrayList<>();
        }
        targetMuscles.add(muscle);
    }

    public void removeTargetMuscle(String muscle) {
        if (targetMuscles != null) {
            targetMuscles.remove(muscle);
        }
    }
}