package com.efm.gymbro;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class DatabaseInitActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_init);

        db = FirebaseFirestore.getInstance();

        Button initButton = findViewById(R.id.initButton);
        initButton.setOnClickListener(v -> initializeExerciseDatabase());
    }

    private void initializeExerciseDatabase() {
        WriteBatch batch = db.batch();

        // CHEST
        addExerciseToBatch(batch, "Bench Press", "strength", "intermediate",
                Arrays.asList("chest", "triceps"), "push", Arrays.asList("barbell", "bench"), 3, "8-12");
        addExerciseToBatch(batch, "Incline Bench Press", "strength", "intermediate",
                Arrays.asList("upper chest", "triceps"), "push", Arrays.asList("barbell", "bench"), 3, "8-12");
        addExerciseToBatch(batch, "Decline Bench Press", "strength", "intermediate",
                Arrays.asList("lower chest", "triceps"), "push", Arrays.asList("barbell", "bench"), 3, "8-12");
        addExerciseToBatch(batch, "Push-ups", "strength", "beginner",
                Arrays.asList("chest", "triceps"), "push", Arrays.asList("bodyweight"), 3, "10-15");
        addExerciseToBatch(batch, "Dumbbell Flyes", "strength", "intermediate",
                Arrays.asList("chest"), "push", Arrays.asList("dumbbells", "bench"), 3, "10-12");
        addExerciseToBatch(batch, "Cable Flyes", "strength", "intermediate",
                Arrays.asList("chest"), "push", Arrays.asList("cable machine"), 3, "12-15");

// BACK
        addExerciseToBatch(batch, "Pull-ups", "strength", "intermediate",
                Arrays.asList("back", "biceps"), "pull", Arrays.asList("pull-up bar"), 3, "8-12");
        addExerciseToBatch(batch, "Lat Pulldowns", "strength", "beginner",
                Arrays.asList("back", "biceps"), "pull", Arrays.asList("cable machine"), 3, "10-12");
        addExerciseToBatch(batch, "Bent Over Rows", "strength", "intermediate",
                Arrays.asList("back", "biceps"), "pull", Arrays.asList("barbell"), 3, "8-12");
        addExerciseToBatch(batch, "T-Bar Rows", "strength", "intermediate",
                Arrays.asList("back"), "pull", Arrays.asList("t-bar"), 3, "8-12");
        addExerciseToBatch(batch, "Face Pulls", "strength", "beginner",
                Arrays.asList("back", "shoulders"), "pull", Arrays.asList("cable machine"), 3, "12-15");

// LEGS
        addExerciseToBatch(batch, "Squats", "strength", "beginner",
                Arrays.asList("quadriceps", "glutes"), "legs", Arrays.asList("barbell"), 3, "8-12");
        addExerciseToBatch(batch, "Romanian Deadlifts", "strength", "intermediate",
                Arrays.asList("hamstrings", "glutes"), "legs", Arrays.asList("barbell"), 3, "8-12");
        addExerciseToBatch(batch, "Leg Press", "strength", "beginner",
                Arrays.asList("quadriceps", "glutes"), "legs", Arrays.asList("machine"), 3, "10-12");
        addExerciseToBatch(batch, "Lunges", "strength", "beginner",
                Arrays.asList("quadriceps", "glutes"), "legs", Arrays.asList("dumbbells"), 3, "10-12 each");
        addExerciseToBatch(batch, "Calf Raises", "strength", "beginner",
                Arrays.asList("calves"), "legs", Arrays.asList("machine"), 3, "15-20");
        addExerciseToBatch(batch, "Leg Extensions", "strength", "beginner",
                Arrays.asList("quadriceps"), "legs", Arrays.asList("machine"), 3, "12-15");
        addExerciseToBatch(batch, "Leg Curls", "strength", "beginner",
                Arrays.asList("hamstrings"), "legs", Arrays.asList("machine"), 3, "12-15");

// SHOULDERS
        addExerciseToBatch(batch, "Overhead Press", "strength", "intermediate",
                Arrays.asList("shoulders"), "push", Arrays.asList("barbell"), 3, "8-12");
        addExerciseToBatch(batch, "Lateral Raises", "strength", "beginner",
                Arrays.asList("shoulders"), "push", Arrays.asList("dumbbells"), 3, "12-15");
        addExerciseToBatch(batch, "Front Raises", "strength", "beginner",
                Arrays.asList("shoulders"), "push", Arrays.asList("dumbbells"), 3, "12-15");
        addExerciseToBatch(batch, "Reverse Flyes", "strength", "beginner",
                Arrays.asList("rear deltoids"), "pull", Arrays.asList("dumbbells"), 3, "12-15");

// ARMS
        addExerciseToBatch(batch, "Bicep Curls", "strength", "beginner",
                Arrays.asList("biceps"), "pull", Arrays.asList("dumbbells"), 3, "10-12");
        addExerciseToBatch(batch, "Hammer Curls", "strength", "beginner",
                Arrays.asList("biceps"), "pull", Arrays.asList("dumbbells"), 3, "10-12");
        addExerciseToBatch(batch, "Tricep Pushdowns", "strength", "beginner",
                Arrays.asList("triceps"), "push", Arrays.asList("cable machine"), 3, "12-15");
        addExerciseToBatch(batch, "Skull Crushers", "strength", "intermediate",
                Arrays.asList("triceps"), "push", Arrays.asList("barbell", "bench"), 3, "10-12");

// CORE
        addExerciseToBatch(batch, "Crunches", "strength", "beginner",
                Arrays.asList("abs"), "core", Arrays.asList("bodyweight"), 3, "15-20");
        addExerciseToBatch(batch, "Planks", "strength", "beginner",
                Arrays.asList("core"), "core", Arrays.asList("bodyweight"), 3, "30-60sec");
        addExerciseToBatch(batch, "Russian Twists", "strength", "beginner",
                Arrays.asList("obliques"), "core", Arrays.asList("weight plate"), 3, "15-20");
        addExerciseToBatch(batch, "Leg Raises", "strength", "beginner",
                Arrays.asList("lower abs"), "core", Arrays.asList("bodyweight"), 3, "12-15");

// CARDIO
        addExerciseToBatch(batch, "Running", "cardio", "beginner",
                Arrays.asList("full body"), "cardio", Arrays.asList("none"), 1, "20-30min");
        addExerciseToBatch(batch, "Jump Rope", "cardio", "beginner",
                Arrays.asList("full body"), "cardio", Arrays.asList("rope"), 1, "10-15min");
        addExerciseToBatch(batch, "Cycling", "cardio", "beginner",
                Arrays.asList("legs"), "cardio", Arrays.asList("bike"), 1, "20-30min");
        addExerciseToBatch(batch, "Burpees", "cardio", "intermediate",
                Arrays.asList("full body"), "cardio", Arrays.asList("bodyweight"), 3, "10-15");



        batch.commit()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Database initialized", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void addExerciseToBatch(WriteBatch batch, String name, String type, String level,
                                    List<String> muscles, String category, List<String> equipment,
                                    int sets, String reps) {
        DocumentReference ref = db.collection("exercises").document();
        Map<String, Object> exercise = new HashMap<>();
        exercise.put("name", name);
        exercise.put("type", type);
        exercise.put("level", level);
        exercise.put("targetMuscles", muscles);
        exercise.put("category", category);
        exercise.put("equipment", equipment);
        exercise.put("sets", sets);
        exercise.put("reps", reps);

        batch.set(ref, exercise);
    }
}