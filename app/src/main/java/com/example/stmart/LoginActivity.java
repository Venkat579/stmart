package com.example.stmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stmart.Utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextLoginEmail,editTextLoginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG="LoginActivity";

    TextView txtSignUp;
    PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferenceManager = new PreferenceManager(this);

        editTextLoginEmail=findViewById(R.id.editText_login_email);
        editTextLoginPwd=findViewById(R.id.editText_login_pwd);
        progressBar=findViewById(R.id.progressBar);
        txtSignUp = findViewById(R.id.txtSignUp);

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        authProfile=FirebaseAuth.getInstance();

        //Reset Password
        Button buttonForgotPassword=findViewById(R.id.button_forgot_password);
        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this,"You can reset password now",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        });

        //Show Hide Password using Eye Icon
        ImageView imageViewShowHidePwd=findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //If password visible the hide it
                    editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Change icon
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                }
                else
                {
                    //If password not visible then show it
                    editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    //Change icon
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        //LoginUser
        Button buttonLogin=findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail=" ";
                String textPwd=editTextLoginPwd.getText().toString();

                if(editTextLoginEmail.getText().toString().contains("@vrsec.ac.in") || editTextLoginEmail.getText().toString().contains("@vrsiddhartha.ac.in"))
                {
                    textEmail=editTextLoginEmail.getText().toString();
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Please enter your email with college domain",Toast.LENGTH_LONG).show();
                    editTextLoginEmail.setError("Email domain is not matched");
                    editTextLoginEmail.requestFocus();
                }

                if(TextUtils.isEmpty(textEmail))
                {
                    Toast.makeText(LoginActivity.this,"Please enter your email",Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email is required");
                    editTextLoginEmail.requestFocus();
                }
                /*else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches())
                {
                    Toast.makeText(LoginActivity.this,"Please re-enter your email",Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Valid Email is required");
                    editTextLoginEmail.requestFocus();
                }*/
                else if(TextUtils.isEmpty(textPwd))
                {
                    Toast.makeText(LoginActivity.this,"Please enter your password",Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Password is required");
                    editTextLoginEmail.requestFocus();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail,textPwd);
                }
            }
        });
    }
    private void loginUser(String email,String pwd){


        authProfile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                Toast.makeText(LoginActivity.this,"You are logged in now",Toast.LENGTH_SHORT).show();

                //Get instance of the current user
                FirebaseUser firebaseUser= authProfile.getCurrentUser();

                //Check if email is verified before can access their profile
                    if(firebaseUser.isEmailVerified())
                    {
                        Toast.makeText(LoginActivity.this,"You are logged in now",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this,HomeActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();// close login act

                        preferenceManager.putBoolean("signed",true);
                        progressBar.setVisibility(View.GONE);

                    }
                    else{
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlertDialog();
                    }


            }
            else
            {
                try
                {
                    throw task.getException();
                }
                catch (FirebaseAuthInvalidUserException e){
                    editTextLoginEmail.setError("User does not exist or no longer valid.Please register again");
                    editTextLoginEmail.requestFocus();
                }
                catch (FirebaseAuthInvalidCredentialsException e)
                {
                    editTextLoginEmail.setError("Invalid Credentials.Kindly,check and re-enter");
                    editTextLoginEmail.requestFocus();
                }
                catch (Exception e)
                {
                    Log.e(TAG,e.getMessage());
                    Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
            progressBar.setVisibility(View.GONE);
        });
    }
    private void showAlertDialog(){
        //Set up alert builder
        AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now.You can not login without email verification.");

        //Open email apps if user clicks /taps continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //tO EMAIL APP IN NEW WINDOW
                startActivity(intent);
            }
        });
        //Create the AlertDialog
        AlertDialog alertDialog=builder.create();
        //Show alert dialog
        alertDialog.show();
    }


}