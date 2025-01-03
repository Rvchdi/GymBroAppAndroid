package com.efm.gymbro;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class UserDetailsActivity extends AppCompatActivity {
    private TextInputEditText ageEditText, weightEditText;
    private Spinner workoutFrequencySpinner;
    private MaterialButton saveButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        ageEditText = findViewById(R.id.ageEditText);
        weightEditText = findViewById(R.id.weightEditText);
        workoutFrequencySpinner = findViewById(R.id.workoutFrequencySpinner);
        saveButton = findViewById(R.id.saveButton);

        // Setup workout frequency spinner
        String[] frequencies = {
                "Select frequency",
                "1-2 times per week",
                "3-4 times per week",
                "5-6 times per week",
                "Every day"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                frequencies
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workoutFrequencySpinner.setAdapter(adapter);

        // Save button click listener
        saveButton.setOnClickListener(v -> saveUserDetails());
    }

    private void saveUserDetails() {
        // Get input values
        String age = ageEditText.getText().toString().trim();
        String weight = weightEditText.getText().toString().trim();
        String frequency = workoutFrequencySpinner.getSelectedItem().toString();

        // Get selected body type
        String bodyType = "";
        if (findViewById(R.id.ectomorphRadio).isSelected()) bodyType = "Ectomorph";
        else if (findViewById(R.id.mesomorphRadio).isSelected()) bodyType = "Mesomorph";
        else if (findViewById(R.id.endomorphRadio).isSelected()) bodyType = "Endomorph";

        // Get selected goal
        String goal = "";
        if (findViewById(R.id.loseWeightRadio).isSelected()) goal = "Lose Weight";
        else if (findViewById(R.id.buildMuscleRadio).isSelected()) goal = "Build Muscle";
        else if (findViewById(R.id.stayFitRadio).isSelected()) goal = "Stay Fit";

        // Validation
        if (age.isEmpty() || weight.isEmpty() || bodyType.isEmpty() ||
                frequency.equals("Select frequency") || goal.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to Firestore
        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("age", Integer.parseInt(age));
        userDetails.put("weight", Double.parseDouble(weight));
        userDetails.put("bodyType", bodyType);
        userDetails.put("workoutFrequency", frequency);
        userDetails.put("goal", goal);

        db.collection("users")
                .document(userId)
                .set(userDetails)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UserDetailsActivity.this,
                            "Profile saved successfully!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UserDetailsActivity.this,
                            "Error saving profile: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}