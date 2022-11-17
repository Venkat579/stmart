package com.example.stmart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class UploadProfilePictureActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ImageView imageViewUploadPic;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private static final int PICK_IMAGE_REQUEST=1;
    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_picture);



        Button buttonUploadPicChoose=findViewById(R.id.upload_pic_choose_button);
        Button buttonUploadPic=findViewById(R.id.upload_pic_button);
        progressBar=findViewById(R.id.progressBar);
        imageViewUploadPic=findViewById(R.id.imageView_profile_dp);

        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();

        storageReference= FirebaseStorage.getInstance().getReference("ProfilePics");
        Uri uri=firebaseUser.getPhotoUrl();
        //Set Users current DP in ImageView(if uploaded already).We will picasso since imageviewer set image
        //regular uris

        Picasso.get().load(uri).into(imageViewUploadPic);

        //choosing image to upload
        buttonUploadPicChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        //Upload image
        buttonUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                UploadPic();
            }
        });

    }
    private void openFileChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK &&data!=null && data.getData()!=null){
            uriImage=data.getData();
            imageViewUploadPic.setImageURI(uriImage);
        }

    }

    private void UploadPic(){
        if(uriImage!=null){
            //Save image with uid of the currently logged user
            StorageReference fileReference=storageReference.child(authProfile.getCurrentUser().getUid()+"."+getFileExtension(uriImage));

            //upload image to storage
            fileReference.putFile(uriImage).addOnSuccessListener(taskSnapshot ->
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl=uri.toString();
                firebaseUser=authProfile.getCurrentUser();

                HashMap<String,Object> map = new HashMap<>();
                map.put("image",imageUrl);

                FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(firebaseUser.getUid())
                        .updateChildren(map)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                finish();
                                Toast.makeText(UploadProfilePictureActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                            }else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(UploadProfilePictureActivity.this, "Failed: "+
                                        task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                //Finally set the display image of the user


            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UploadProfilePictureActivity.this, "Failed: "+
                        e.getMessage(), Toast.LENGTH_SHORT).show();
            }));

        }
    }
    //Obtain file Extension of the image
    private String getFileExtension(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}