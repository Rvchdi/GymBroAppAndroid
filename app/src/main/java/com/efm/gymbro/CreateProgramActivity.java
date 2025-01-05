package com.efm.gymbro;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import java.util.Set;

public class CreateProgramActivity extends AppCompatActivity {
    private static final String TAG = "CreateProgramActivity";

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private RecyclerView exercisesRecyclerView;
    private ExerciseAdapter exerciseAdapter;
    private TextInputEditText programNameInput;
    private Toolbar toolbar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_program);

        initializeFirebase();
        initializeViews();
        setupToolbar();
        setupRecyclerView();
        loadExercises();
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        programNameInput = findViewById(R.id.programNameInput);
        exercisesRecyclerView = findViewById(R.id.exercisesRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        findViewById(R.id.saveButton).setOnClickListener(v -> validateAndSaveProgram());
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Create Program");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exercisesRecyclerView.setHasFixedSize(true);
    }

    private void loadExercises() {
        showLoading(true);
        db.collection("exercises")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Exercise> exercises = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Exercise exercise = doc.toObject(Exercise.class);
                        if (exercise != null) {
                            exercise.setId(doc.getId());
                            exercises.add(exercise);
                        }
                    }
                    setupExerciseAdapter(exercises);
                    showLoading(false);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading exercises", e);
                    showError("Failed to load exercises");
                    showLoading(false);
                });
    }

    private void setupExerciseAdapter(List<Exercise> exercises) {
        exerciseAdapter = new ExerciseAdapter(exercises, new ExerciseAdapter.OnExerciseClickListener() {
            @Override
            public void onExerciseClick(Exercise exercise, boolean isSelected) {
                Log.d(TAG, "Exercise " + exercise.getName() + (isSelected ? " selected" : " deselected"));
            }
        });
        exercisesRecyclerView.setAdapter(exerciseAdapter);
    }

    private void validateAndSaveProgram() {
        String name = programNameInput.getText().toString().trim();
        if (name.isEmpty()) {
            programNameInput.setError("Program name is required");
            return;
        }

        List<Exercise> selectedExercises = exerciseAdapter.getSelectedExercises();
        if (selectedExercises.isEmpty()) {
            showError("Please select at least one exercise");
            return;
        }

        saveProgram(name, selectedExercises);
    }

    private void saveProgram(String name, List<Exercise> selectedExercises) {
        showLoading(true);

        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) {
            showError("User not authenticated");
            showLoading(false);
            return;
        }

        Map<String, Object> program = new HashMap<>();
        program.put("name", name);
        program.put("exercises", selectedExercises);
        program.put("createdAt", FieldValue.serverTimestamp());
        program.put("userId", userId);

        Log.d(TAG, "Saving program: " + name + " with " + selectedExercises.size() + " exercises");

        db.collection("programs")
                .add(program)
                .addOnSuccessListener(ref -> {
                    Log.d(TAG, "Program saved with ID: " + ref.getId());
                    Toast.makeText(this, "Program saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving program", e);
                    showError("Failed to save program: " + e.getMessage());
                    showLoading(false);
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}