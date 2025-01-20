package com.efm.gymbro.activity;

import androidx.appcompat.widget.Toolbar;

import com.efm.gymbro.R;
import com.efm.gymbro.adapter.ExerciseAdapter;
import com.efm.gymbro.fragment.AddExerciseDialogFragment;
import com.efm.gymbro.fragment.ExerciseDetailsDialogFragment;
import com.efm.gymbro.model.Exercise;
import com.efm.gymbro.model.Program;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateProgramActivity extends AppCompatActivity {
    private TextInputEditText programNameInput;
    private MaterialButton saveButton;
    private RecyclerView exercisesRecyclerView;
    private ProgressBar progressBar;
    private ExerciseAdapter exerciseAdapter;
    private List<Exercise> exercises;
    private Toolbar toolbar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_program);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        programNameInput = findViewById(R.id.programNameInput);
        saveButton = findViewById(R.id.saveButton);
        exercisesRecyclerView = findViewById(R.id.exercisesRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);

        // Set up toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize exercise list and adapter
        exercises = new ArrayList<>();
        exerciseAdapter = new ExerciseAdapter(exercises, (view, exercise) -> {
            // Handle exercise click - show details or edit options
            showExerciseDetailsDialog(exercise);
        });

        // Set up RecyclerView
        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exercisesRecyclerView.setAdapter(exerciseAdapter);

        // Set click listeners
        saveButton.setOnClickListener(v -> createProgram());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_program, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_exercise) {
            showAddExerciseDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddExerciseDialog() {
        AddExerciseDialogFragment dialog = new AddExerciseDialogFragment();
        dialog.setExerciseSelectedListener(exercise -> {
            exercises.add(exercise);
            exerciseAdapter.notifyItemInserted(exercises.size() - 1);
        });
        dialog.show(getSupportFragmentManager(), "AddExercise");
    }

    private void showExerciseDetailsDialog(Exercise exercise) {
        ExerciseDetailsDialogFragment dialog = new ExerciseDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("exercise", exercise);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "ExerciseDetails");
    }

    private void createProgram() {
        String programName = programNameInput.getText().toString().trim();

        if (TextUtils.isEmpty(programName)) {
            programNameInput.setError("Program name is required");
            return;
        }

        if (exercises.isEmpty()) {
            showSnackbar("Add at least one exercise");
            return;
        }

        // Show progress
        setLoading(true);

        // Create program object
        Program program = new Program();
        program.setName(programName);
        program.setExercises(exercises);
        program.setCreatorId(mAuth.getCurrentUser().getUid());

        // Save to Firestore
        Map<String, Object> programMap = new HashMap<>();
        programMap.put("name", program.getName());
        programMap.put("creatorId", program.getCreatorId());
        programMap.put("exercises", program.getExercises());

        db.collection("programs")
                .add(programMap)
                .addOnSuccessListener(documentReference -> {
                    setLoading(false);
                    showSnackbar("Program created successfully");
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    showSnackbar("Error creating program: " + e.getMessage());
                });
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        saveButton.setEnabled(!isLoading);
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }
}