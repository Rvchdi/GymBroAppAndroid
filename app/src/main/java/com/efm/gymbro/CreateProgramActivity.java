package com.efm.gymbro;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.efm.gymbro.model.Exercise;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateProgramActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private RecyclerView exercisesRecyclerView;
    private List<Exercise> selectedExercises = new ArrayList<>();
    private TextInputEditText programNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_program);

        db = FirebaseFirestore.getInstance();
        initializeViews();
        loadExercises();
    }

    private void initializeViews() {
        programNameInput = findViewById(R.id.programNameInput);
        exercisesRecyclerView = findViewById(R.id.exercisesRecyclerView);
        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.saveButton).setOnClickListener(v -> saveProgram());
    }

    private void loadExercises() {
        db.collection("exercises").get()
                .addOnSuccessListener(snapshot -> {
                    List<Exercise> exercises = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Exercise exercise = doc.toObject(Exercise.class);
                        if (exercise != null) {
                            exercises.add(exercise);
                        }
                    }
                    setupRecyclerView(exercises);
                })
                .addOnFailureListener(e -> showError("Error loading exercises: " + e.getMessage()));
    }

    private void setupRecyclerView(List<Exercise> exercises) {
        ExerciseAdapter adapter = new ExerciseAdapter(exercises, this::toggleExerciseSelection);
        exercisesRecyclerView.setAdapter(adapter);
    }

    private void toggleExerciseSelection(Exercise exercise) {
        if (exercise.isSelected()) {
            selectedExercises.add(exercise);
        } else {
            selectedExercises.remove(exercise);
        }
    }

    private void saveProgram() {
        String name = programNameInput.getText().toString().trim();
        if (name.isEmpty()) {
            programNameInput.setError("Required");
            return;
        }

        Map<String, Object> program = new HashMap<>();
        program.put("name", name);
        program.put("exercises", selectedExercises);
        program.put("createdAt", FieldValue.serverTimestamp());
        program.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());

        db.collection("programs")
                .add(program)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Program saved!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> showError("Error: " + e.getMessage()));
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}