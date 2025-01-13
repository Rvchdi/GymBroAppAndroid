package com.efm.gymbro.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.DocumentId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Program implements Parcelable {
    @DocumentId
    private String id;
    private String name;
    private List<Exercise> exercises;
    private String creatorId;
    private Date createdAt;

    // Required empty constructor for Firestore
    public Program() {}

    // Parcelable constructor
    protected Program(Parcel in) {
        id = in.readString();
        name = in.readString();
        creatorId = in.readString();
        createdAt = new Date(in.readLong());
        exercises = new ArrayList<>();
        in.readTypedList(exercises, Exercise.CREATOR);
    }

    // Parcelable CREATOR
    public static final Creator<Program> CREATOR = new Creator<Program>() {
        @Override
        public Program createFromParcel(Parcel in) {
            return new Program(in);
        }

        @Override
        public Program[] newArray(int size) {
            return new Program[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(creatorId);
        dest.writeLong(createdAt != null ? createdAt.getTime() : 0);
        dest.writeTypedList(exercises);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Exercise> getExercises() { return exercises; }
    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }

    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}