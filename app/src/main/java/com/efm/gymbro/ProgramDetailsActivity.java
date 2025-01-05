package com.efm.gymbro;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.efm.gymbro.model.Exercise;
import com.efm.gymbro.model.Program;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProgramDetailsActivity extends AppCompatActivity {
    private static final String TAG = "ProgramDetails";

    private FirebaseFirestore db;
    private String programId;
    private Toolbar toolbar;
    private RecyclerView exercisesRecyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_details);

        programId = getIntent().getStringExtra("program_id");
        if (programId == null) {
            Toast.makeText(this, "Error: Program not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupToolbar();
        loadProgramDetails();
    }

    private void initializeViews() {
        db = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.toolbar);
        exercisesRecyclerView = findViewById(R.id.exercisesRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);

        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exercisesRecyclerView.setHasFixedSize(true);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadProgramDetails() {
        showLoading(true);
        db.collection("programs").document(programId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Program program = documentSnapshot.toObject(Program.class);
                    if (program != null) {
                        updateUI(program);
                    } else {
                        showError("Program not found");
                        finish();
                    }
                    showLoading(false);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading program details", e);
                    showError("Error loading program details");
                    showLoading(false);
                    finish();
                });
    }

    private void updateUI(Program program) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(program.getName());
        }

        List<Exercise> exercises = program.getExercises();
        if (exercises != null && !exercises.isEmpty()) {
            ProgramExerciseAdapter adapter = new ProgramExerciseAdapter(exercises);
            exercisesRecyclerView.setAdapter(adapter);
            exercisesRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        } else {
            exercisesRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}