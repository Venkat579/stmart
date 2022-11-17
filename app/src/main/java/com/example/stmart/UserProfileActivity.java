package com.example.stmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stmart.Model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textViewWelcome,textViewFullName,textViewCollegeId,textViewEmail,textViewDOB,textViewGender,textViewMobile;
    private ProgressBar progressBar;
    private String fullName,collegeid,email,dob,gender,mobile;
    private ImageView imageView;
    private FirebaseAuth authProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        textViewWelcome=findViewById(R.id.textView_show_welcome);
        textViewFullName=findViewById(R.id.textView_show_full_name);
        textViewCollegeId=findViewById(R.id.textView_show_college_id);
        textViewEmail=findViewById(R.id.textView_show_email);
        textViewDOB=findViewById(R.id.textView_show_dob);
        textViewGender=findViewById(R.id.textView_show_gender);
        textViewMobile=findViewById(R.id.textView_show_mobile);
        progressBar=findViewById(R.id.progressBar);

        //Set on click listener on IMageview to openupload pict activity
        imageView=findViewById(R.id.imageView_profile_dp);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserProfileActivity.this,UploadProfilePictureActivity.class);
                startActivity(intent);
            }
        });

        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();
        if(firebaseUser==null)
        {
            Toast.makeText(UserProfileActivity.this,"Something went wrong!User details are not available ",Toast.LENGTH_SHORT).show();
        }
        else
        {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }

    private void showUserProfile(FirebaseUser firebaseUser){
        String userID=firebaseUser.getUid();

        //Extract user reference from database for "Registered users"
        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel readUserDetails=snapshot.getValue(UserModel.class);
                if(readUserDetails!=null)
                {
                    fullName= readUserDetails.getUsername();
                    email= readUserDetails.getEmail();
                    collegeid=readUserDetails.getCollegeId();
                    dob=readUserDetails.getDob();
                    gender= readUserDetails.getGender();
                    mobile= readUserDetails.getPhone();

                    textViewWelcome.setText("Welcome, "+fullName+" !");
                    textViewFullName.setText(fullName);
                    textViewCollegeId.setText(collegeid);
                    textViewEmail.setText(email);
                    textViewDOB.setText(dob);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobile);

                    //Set user dp(After uploaded)
                    Uri uri=firebaseUser.getPhotoUrl();

                    //Imageview setImageURI() should not be used with regular URIs.So we are using picasso
                    try {
                        Picasso.get().load(readUserDetails.getImage()).placeholder(R.drawable.placeholder)
                                .into(imageView);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                else
                {
                    Toast.makeText(UserProfileActivity.this,"Something went wrong! ",Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this,"Something went wrong! ",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }


}