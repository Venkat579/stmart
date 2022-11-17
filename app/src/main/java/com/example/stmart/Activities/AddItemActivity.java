package com.example.stmart.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.stmart.Model.ItemModel;
import com.example.stmart.databinding.ActivityAddItemBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddItemActivity extends AppCompatActivity {


    ActivityAddItemBinding binding;
    String category;

    DatabaseReference reference;
    StorageReference storageReference;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        category = getIntent().getStringExtra("category");
        binding.txtCategory.setText(category);

        reference = FirebaseDatabase.getInstance().getReference().child("Items");
        storageReference = FirebaseStorage.getInstance().getReference().child("ItemImages");

        binding.imgBack.setOnClickListener(v -> finish());


        binding.btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.inputItemName.getText().toString();
                String description = binding.inputDescription.getText().toString();
                String amount = binding.inputAmount.getText().toString();
                int intAmount = Integer.parseInt(amount);

                if (name.isEmpty() || description.isEmpty() || amount.isEmpty()){
                    Toast.makeText(AddItemActivity.this, "Fill ll required fields!", Toast.LENGTH_SHORT).show();
                }else if (intAmount <=0){
                    Toast.makeText(AddItemActivity.this, "Enter minimum amount value!", Toast.LENGTH_SHORT).show();
                }else if (imageUri == null){
                    Toast.makeText(AddItemActivity.this, "select product image", Toast.LENGTH_SHORT).show();
                }

                else {

                    uploadItem(name,description,intAmount);

                }
            }
        });

        binding.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,100);
            }
        });
    }

    private void uploadItem(String name, String description, int intAmount) {

        ProgressDialog progressDialog = new ProgressDialog(AddItemActivity.this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final StorageReference sRef = storageReference.child(System.currentTimeMillis()+".jpg");
        sRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String imageUrl = uri.toString();


                        String id = reference.push().getKey();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        assert user != null;

                        ItemModel model = new ItemModel();
                        model.setName(name);
                        model.setDescription(description);
                        model.setImage(imageUrl);
                        model.setPrice(intAmount);
                        model.setId(id);
                        model.setCategory(category);
                        model.setPublisher(user.getUid());
                        model.setSold(false);

                        assert id != null;
                        reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    progressDialog.dismiss();
                                    finish();
                                    Toast.makeText(AddItemActivity.this, "Item Published!", Toast.LENGTH_SHORT).show();



                                }else {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddItemActivity.this, "Failed: "+
                                            task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });










                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddItemActivity.this, "Failed: "+
                                e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });







    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100){
            if (resultCode == RESULT_OK && data !=null){
                imageUri = data.getData();
                binding.productImage.setImageURI(imageUri);
            }
        }else {
            Toast.makeText(this, "Please select image!", Toast.LENGTH_SHORT).show();
        }
    }
}