package com.efm.gymbro;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private TextInputEditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private MaterialButton registerButton;
    private ImageView backButton;
    private TextView loginPromptText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate started");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);
        loginPromptText = findViewById(R.id.loginPromptText);
        Log.d(TAG, "Views initialized");
        // Set click listeners
        registerButton.setOnClickListener(v -> handleRegistration());
        backButton.setOnClickListener(v -> finish());
        loginPromptText.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        });
        Log.d(TAG, "Listeners set");
    }

    private void handleRegistration() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Validate inputs
        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            return;
        }
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            return;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords don't match");
            return;
        }

        // Show progress
        registerButton.setEnabled(false);
        // TODO: Show progress indicator

        // Create user with Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Save additional user info
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveUserInfo(user, name);
                        Intent intent = new Intent(RegisterActivity.this, UserDetailsActivity.class);
                        startActivity(intent);
                    } else {
                        // Handle failure
                        Toast.makeText(RegisterActivity.this,
                                "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        registerButton.setEnabled(true);
                    }
                });
    }

    private void saveUserInfo(FirebaseUser user, String name) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", name);
        userInfo.put("email", user.getEmail());
        userInfo.put("createdAt", FieldValue.serverTimestamp());

        db.collection("users").document(user.getUid())
                .set(userInfo)
                .addOnSuccessListener(aVoid -> {
                    // Navigate to main activity
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this,
                            "Failed to save user info: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    registerButton.setEnabled(true);
                });
    }
}