package com.app.happytails.utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.happytails.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    private Button recoverBtn;
    private EditText emailEt;
    private ImageButton backBtn;
    private FirebaseAuth auth;
    private ProgressBar progBar;

    public static final String EMAIL_REGEX = "^(.+)@(.+)$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        backBtn = findViewById(R.id.BacktoSignInF);
        recoverBtn = findViewById(R.id.forgotButton);
        emailEt = findViewById(R.id.emailEditTextF);
        progBar = findViewById(R.id.recoverProgBar);
        progBar.setVisibility(View.GONE);

        // On back button click, navigate back to SignIn screen
        backBtn.setOnClickListener(v -> onBackPressed());

        // On recover button click
        recoverBtn.setOnClickListener(v -> {
            String email = emailEt.getText().toString().trim();

            // Validate email input
            if (email.isEmpty() || !email.matches(EMAIL_REGEX)) {
                emailEt.setError("Invalid email format");
                return;
            }

            setInProgress(true);

            // Send password reset email
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            setInProgress(false);
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgetPassword.this, "Password reset email sent successfully", Toast.LENGTH_SHORT).show();
                                // Redirect to ForgotOtpActivity
                                Intent intent = new Intent(ForgetPassword.this, SignInActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                String err = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                Toast.makeText(ForgetPassword.this, err, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }

    private void setInProgress(boolean inProgress) {
        progBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        recoverBtn.setEnabled(!inProgress);
    }
}