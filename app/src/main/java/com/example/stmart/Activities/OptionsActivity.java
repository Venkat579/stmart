package com.example.stmart.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

//import com.example.stmart.BuildConfig;
import com.example.stmart.LoginActivity;
import com.example.stmart.MainActivity;
import com.example.stmart.Model.UserModel;
import com.example.stmart.R;
import com.example.stmart.UploadProfilePictureActivity;
import com.example.stmart.UserProfileActivity;
import com.example.stmart.Utilities.PreferenceManager;
import com.example.stmart.databinding.ActivityOpyionsBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class OptionsActivity extends AppCompatActivity {


    ActivityOpyionsBinding binding;

    DatabaseReference reference;
    FirebaseUser user;

    PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOpyionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);

        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        user = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        getUserData();

        binding.txtUpdateProfile.setOnClickListener(v ->
                startActivity(new Intent(OptionsActivity.this, UploadProfilePictureActivity.class)));

        binding.btnViewAll.setOnClickListener(v ->
                startActivity(new Intent(OptionsActivity.this, UserProfileActivity.class)));

        binding.txtMyPosts.setOnClickListener(v ->
                startActivity(new Intent(OptionsActivity.this, MyPostsActivity.class)));

        binding.txtAboutMyApp.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(OptionsActivity.this);
            builder.setTitle("About:");
            builder.setMessage("App name: "+ getResources().getString(R.string.app_name)+"\n"+"App version: "+
                    BuildConfig.VERSION_NAME);
            builder.setCancelable(false);
            builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

            builder.create().show();
        });

        binding.txtCustomer.setOnClickListener(v -> startActivity(new Intent(OptionsActivity.this, AboutActivity.class)));

        binding.txtLogout.setOnClickListener(v -> showLogoutDialogue());
    }
    private void showLogoutDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(OptionsActivity.this);
        builder.setTitle("Are you want to logout from this account?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            dialog.dismiss();
            AuthUI.getInstance().signOut(OptionsActivity.this)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(OptionsActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            preferenceManager.clear();

                        }else {
                            Toast.makeText(OptionsActivity.this, "Failed: "+
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        builder.setNegativeButton("Cancel",null);

        builder.create().show();
    }


    private void getUserData(){
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserModel model = snapshot.getValue(UserModel.class);
                    if (model !=null){
                        binding.username.setText(model.getUsername());
                        binding.email.setText(model.getEmail());

                        try {
                            Picasso.get().load(model.getImage()).placeholder(R.drawable.placeholder)
                                    .into(binding.profileImage);
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                }else {
                    Toast.makeText(OptionsActivity.this, "User not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}