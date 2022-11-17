package com.example.stmart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.stmart.Model.UserModel;
import com.example.stmart.Utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextRegisterFullName,editTextRegisterEmail,editTextRegisterDOB,editTextRegisterCollegeId,editTextRegisterMobile,editTextRegisterPwd,editTextRegisterConfirmPwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private DatePickerDialog picker;
    ImageView profileImage,adharImage;
    private static final String TAG="RegisterActivity";

    PreferenceManager preferenceManager;

    private Uri imageUri,adharUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        preferenceManager = new PreferenceManager(this);


        progressBar=findViewById(R.id.progressBar);
        editTextRegisterFullName=findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail=findViewById(R.id.editText_register_email);
        editTextRegisterDOB=findViewById(R.id.editText_register_dob);
        editTextRegisterCollegeId=findViewById(R.id.editText_register_college_id);
        editTextRegisterMobile=findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd=findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd=findViewById(R.id.editText_register_confirm_password);
        profileImage = findViewById(R.id.profileImage);
        adharImage = findViewById(R.id.adharImage);


        //RadioButton for Gender
        radioGroupRegisterGender=findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        //Setting up datepicker on edittext
        editTextRegisterDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar=Calendar.getInstance();
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                int month=calendar.get(Calendar.MONTH);
                int year=calendar.get(Calendar.YEAR);

                //Date Picker Dialog
                picker=new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayofMonth) {
                        editTextRegisterDOB.setText(dayofMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();
            }
        });

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedGenderId=radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected=findViewById(selectedGenderId);

                //Obtain the entered data
                String textFullName=editTextRegisterFullName.getText().toString();

                String textDOB=editTextRegisterDOB.getText().toString();
                String textCollegeid=editTextRegisterCollegeId.getText().toString();
                String textMobile=editTextRegisterMobile.getText().toString();
                String textPwd=editTextRegisterPwd.getText().toString();
                String textConfirmPwd=editTextRegisterConfirmPwd.getText().toString();
                String textGender;   //Can't obtain the value before verifying if any button was selected or not
                String textEmail=" ";

                //Validate Mobile Number using matcher and pattern(Regular expression
                String mobileRegex="[6-9][0-9][6-9]"; //First no can be {6,7,8,9} and rest nos can be any no
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher=mobilePattern.matcher(textMobile);

                if(editTextRegisterEmail.getText().toString().contains("@vrsec.ac.in") || editTextRegisterEmail.getText().toString().contains("@vrsiddhartha.ac.in"))
                {
                    textEmail=editTextRegisterEmail.getText().toString();
                }
                else
                {
                    Toast.makeText(RegisterActivity.this,"Please enter your email with college domain",Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email domain is not matched");
                    editTextRegisterEmail.requestFocus();
                }

                if(TextUtils.isEmpty(textFullName))
                {
                    Toast.makeText(RegisterActivity.this,"Please enter your full name",Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full Name is required");
                    editTextRegisterFullName.requestFocus();
                }
                else if(TextUtils.isEmpty(textEmail))
                {
                    Toast.makeText(RegisterActivity.this,"Please enter your email",Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();
                }
                /*else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches())
                {
                    Toast.makeText(RegisterActivity.this,"Please re-enter your full name",Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Valid email is required");
                    editTextRegisterEmail.requestFocus();
                }*/
                else if(TextUtils.isEmpty(textDOB))
                {
                    Toast.makeText(RegisterActivity.this,"Please enter your date of birth",Toast.LENGTH_LONG).show();
                    editTextRegisterDOB.setError("Date of Birth is required");
                    editTextRegisterDOB.requestFocus();
                }
                else if(radioGroupRegisterGender.getCheckedRadioButtonId()==-1)
                {
                    Toast.makeText(RegisterActivity.this,"Please select your Gender",Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderSelected.setError("Gender is required");
                    radioButtonRegisterGenderSelected.requestFocus();
                }
                else if(TextUtils.isEmpty(textCollegeid))
                {
                    Toast.makeText(RegisterActivity.this,"Please enter your College id",Toast.LENGTH_LONG).show();
                    editTextRegisterCollegeId.setError("College id is required");
                    editTextRegisterCollegeId.requestFocus();
                }
                else if(TextUtils.isEmpty(textMobile))
                {
                    Toast.makeText(RegisterActivity.this,"Please enter your Mobile Number",Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile Number is required");
                    editTextRegisterMobile.requestFocus();
                }
                else if(textMobile.length()!=10)
                {
                    Toast.makeText(RegisterActivity.this,"Please re-enter your Mobile Number",Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile NO should be 10 digits");
                    editTextRegisterMobile.requestFocus();
                }
                else if(!mobileMatcher.find()){
                    Toast.makeText(RegisterActivity.this,"Please re-enter your Mobile Number",Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile NO is not valid");
                    editTextRegisterMobile.requestFocus();
                }
                else if(TextUtils.isEmpty(textPwd))
                {
                    Toast.makeText(RegisterActivity.this,"Please enter your password",Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password is required");
                    editTextRegisterPwd.requestFocus();
                }
                else if(textPwd.length()<6)
                {
                    Toast.makeText(RegisterActivity.this,"Password should be atleast 6 digits",Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password too weak");
                    editTextRegisterPwd.requestFocus();
                }
                else if(TextUtils.isEmpty(textConfirmPwd))
                {
                    Toast.makeText(RegisterActivity.this,"Please confirm your password",Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Password confirmation is required");
                    editTextRegisterConfirmPwd.requestFocus();
                }
                else if(!textPwd.equals(textConfirmPwd))
                {
                    Toast.makeText(RegisterActivity.this,"Please enter same password",Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Password confirmation is required");
                    editTextRegisterConfirmPwd.requestFocus();
                    //Clear entered the passwords
                    editTextRegisterPwd.clearComposingText();
                    editTextRegisterConfirmPwd.clearComposingText();
                }else if (imageUri == null){
                    Toast.makeText(RegisterActivity.this, "Select id card image!", Toast.LENGTH_SHORT).show();
                }else if (adharUri == null){
                    Toast.makeText(RegisterActivity.this, "Select adhar card image!", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    textGender=radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName,textEmail,textCollegeid,textDOB,textGender,textMobile,textPwd);
                }
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,100);
            }
        });

        adharImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,101);
            }
        });
        askPermissions();

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 100:
                if (resultCode == RESULT_OK && data !=null){
                    imageUri = data.getData();
                    profileImage.setImageURI(imageUri);
                }else {
                    Toast.makeText(this, "Select id card image", Toast.LENGTH_SHORT).show();

                }
                break;
            case 101:
                if (resultCode == RESULT_OK && data !=null){
                    adharUri = data.getData();
                    adharImage.setImageURI(adharUri);
                }else {
                    Toast.makeText(this, "Select aadhaar card image", Toast.LENGTH_SHORT).show();

                }
                break;
        }

    }

    //Register USer using credentials given
    private void registerUser(String textFullName,String textEmail,String textCollegeid,String textDOB,
                              String textGender,String textMobile,String textPwd){
        FirebaseAuth auth=FirebaseAuth.getInstance();
        //Create user profile
        auth.createUserWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser firebaseUser= auth.getCurrentUser();
                    DatabaseReference referenceProfile= FirebaseDatabase.getInstance()
                            .getReference("Users");


                    assert firebaseUser != null;
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Idcards")
                            .child(firebaseUser.getUid())
                            .child(System.currentTimeMillis()+".jpg");


                    storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();


                                //upload addahar

                                StorageReference sRef = FirebaseStorage.getInstance().getReference().child("Aadhar")
                                        .child(firebaseUser.getUid())
                                        .child(System.currentTimeMillis()+".jpg");
                                sRef.putFile(adharUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                String adhar = uri.toString();

                                                //Update Display name of user

                                                UserModel model = new UserModel();
                                                model.setUsername(textFullName);
                                                model.setCollegeId(textCollegeid);
                                                model.setDob(textDOB);
                                                model.setUid(firebaseUser.getUid());
                                                model.setPhone(textMobile);
                                                model.setGender(textGender);
                                                model.setEmail(textEmail);
                                                model.setImage("");
                                                model.setIdCard(imageUrl);
                                                model.setVerified(false);
                                                model.setAadhar(adhar);


                                                //Extracting user reference from database for "Registered Users"

                                                referenceProfile.child(firebaseUser.getUid()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task1) {
                                                        if(task1.isSuccessful())
                                                        {
                                                            Toast.makeText(RegisterActivity.this,"User registered Successfully.Please verify your mail",Toast.LENGTH_LONG).show();

                                                            //Send Verification Email
                                                            firebaseUser.sendEmailVerification();
                                                            Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                                                            //To prevent user from returning back to register activity on pressing back button after registration
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(intent);
                                                            finish(); //To close Register activity
                                                            preferenceManager.putBoolean("signed",false);
                                                        }
                                                        else {
                                                            Toast.makeText(RegisterActivity.this,"User registration failed.Please try again",Toast.LENGTH_LONG).show();
                                                        }
                                                        //Hide progress bar
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                });







                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(RegisterActivity.this, "Aadhar Failed: "+
                                                        e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });







                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Error: "+
                                    e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));
                }
                else
                {
                    try {
                        throw  task.getException();
                    }
                    catch (FirebaseAuthWeakPasswordException e) {
                        editTextRegisterPwd.setError("Your password is too weak.Kindly use mix of alphabets,numbers and special characters");
                        editTextRegisterPwd.requestFocus();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e)
                    {
                        editTextRegisterPwd.setError("Your email is invalid or already in use.Kindly re enter.");
                        editTextRegisterPwd.requestFocus();
                    }
                    catch (FirebaseAuthUserCollisionException e)
                    {
                        editTextRegisterPwd.setError("User is already registered with the mail.Use another mail.");
                        editTextRegisterPwd.requestFocus();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void askPermissions(){
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                    100);
        }
    }
}