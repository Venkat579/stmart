package com.example.stmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button buttonPwdReset;
    private EditText editTextPwdResetEmail;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        editTextPwdResetEmail=findViewById(R.id.editText_password_reset_email);
        buttonPwdReset=findViewById(R.id.button_password_reset_email);
        progressBar=findViewById(R.id.progressBar);

        buttonPwdReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=editTextPwdResetEmail.getText().toString();
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(ForgotPasswordActivity.this,"Please enter your registered email",Toast.LENGTH_SHORT).show();
                    editTextPwdResetEmail.setError("Email is required");
                    editTextPwdResetEmail.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    Toast.makeText(ForgotPasswordActivity.this,"Please enter valid email",Toast.LENGTH_SHORT).show();
                    editTextPwdResetEmail.setError("Valid Email is required");
                    editTextPwdResetEmail.requestFocus();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    resetPassword(email);
                }
            }
        });
    }
    private void resetPassword(String email){
        authProfile=FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this,"Please check your inbox for password reset link",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(ForgotPasswordActivity.this,MainActivity.class);

                    //Clear stack to prevent user coming back to user profile activity on pressing back button
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(ForgotPasswordActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}