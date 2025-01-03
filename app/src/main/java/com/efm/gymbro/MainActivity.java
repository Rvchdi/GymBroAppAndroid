package com.efm.gymbro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {


    private TextInputEditText emailEditText, passwordEditText;
    private MaterialButton loginButton, registerButton;
    private TextView forgotPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        FirebaseApp.initializeApp(this);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        FirebaseApp.initializeApp(this);
        // Set click listeners
        loginButton.setOnClickListener(v -> handleLogin());
        registerButton.setOnClickListener(v -> {
            // Open Register Activity
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        forgotPasswordText.setOnClickListener(v -> handleForgotPassword());
    }

    private void handleLogin() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        // Implement Firebase login logic
    }

    private void handleRegister() {
        // Navigate to registration screen or handle registration logic
    }

    private void handleForgotPassword() {
        // Handle forgot password logic
    }
}