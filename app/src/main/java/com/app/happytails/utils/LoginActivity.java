package com.app.happytails.utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.happytails.R;
import com.app.happytails.utils.model.UserModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private EditText emailEditText, passwordEditText, usernameEditText;
    private ProgressBar progressBar;
    private Button loginButton;
    private ImageView backToSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        progressBar = findViewById(R.id.LoginProc1);
        loginButton = findViewById(R.id.loginButton);
        backToSignIn = findViewById(R.id.BacktoSignIn);

        backToSignIn.setOnClickListener(v -> navigateToSignIn());
        loginButton.setOnClickListener(v -> createUserAndSendVerification());
    }

    private void navigateToSignIn() {
        Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    private void createUserAndSendVerification() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();

        if (!validateInput(email, password, username)) return;

        setInProgress(true);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            sendEmailVerification(user, username, email);
                        }
                    } else {
                        showError(task.getException().getMessage());
                    }
                });
    }

    private boolean validateInput(String email, String password, String username) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            showToast("Please fill out all fields");
            return false;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Your password should be at least 6 characters");
            return false;
        }

        if (username.length() < 3) {
            usernameEditText.setError("Your username should be at least 3 characters");
            return false;
        }

        return true;
    }

    private void sendEmailVerification(FirebaseUser user, String username, String email) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveUserToFirestore(user, username, email);

                        Intent intent = new Intent(LoginActivity.this, LoginOtpActivity.class);
                        intent.putExtra("email", user.getEmail());
                        intent.putExtra("username", username);
                        startActivity(intent);
                        finish();
                    } else {
                        showError("Error sending verification email: " + task.getException().getMessage());
                    }
                });
    }

    private void saveUserToFirestore(FirebaseUser user, String username, String email) {
        UserModel userModel = new UserModel(Timestamp.now(), username, email, user.getUid(), "", 0, 0, "No status");

        firestore.collection("users")
                .document(user.getUid())
                .set(userModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("User details saved successfully");
                    } else {
                        showError("Error saving user details: " + task.getException().getMessage());
                    }
                });
    }

    private void setInProgress(boolean inProgress) {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!inProgress);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showError(String errorMessage) {
        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
    }
}