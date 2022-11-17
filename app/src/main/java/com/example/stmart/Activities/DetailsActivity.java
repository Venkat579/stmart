package com.example.stmart.Activities;

import static com.example.stmart.Adapters.ItemAdapter.list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.stmart.Model.ItemModel;
import com.example.stmart.Model.UserModel;
import com.example.stmart.R;
import com.example.stmart.databinding.ActivityDetailsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    ActivityDetailsBinding binding;

    ItemModel model;
    int position;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        position = getIntent().getIntExtra("position",0);

        model = list.get(position);
        if (model !=null){

            binding.productTitle.setText(model.getName());
            binding.amount.setText("₹"+model.getPrice());
            try{
                Picasso.get().load(model.getImage()).placeholder(R.drawable.logo)
                        .into(binding.productImage);
            }catch (Exception e){
                e.getMessage();
            }
            binding.txtCategory.setText(model.getCategory());
            binding.txtDescription.setText(model.getDescription());

            if (model.getSold()){
                binding.btnWhatsApp.setVisibility(View.GONE);
                binding.txtSold.setVisibility(View.VISIBLE);
            }else {
                binding.btnWhatsApp.setVisibility(View.VISIBLE);
                binding.txtSold.setVisibility(View.GONE);

            }

            getUserData();
        }




    }
    private void getUserData(){
        reference.child(model.getPublisher()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserModel model = snapshot.getValue(UserModel.class);
                    if (model !=null){
                        try{
                            Picasso.get().load(model.getImage()).placeholder(R.drawable.placeholder)
                                    .into(binding.profileImage);
                        }catch (Exception e){
                            binding.profileImage.setImageResource(R.drawable.placeholder);
                        }
                        binding.username.setText(model.getUsername());
                        binding.email.setText(model.getEmail());

                        binding.btnWhatsApp.setOnClickListener(v -> sendMessage(model.getPhone()));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String phoneNumber){
        PackageManager pm=getPackageManager();

        String number;

        if (phoneNumber.startsWith("+91")){
            number = phoneNumber;
        }else {
            number = "+91"+phoneNumber;
        }

        String message = "Hello i have seen your post at Stumart";

        startActivity(
                new Intent(Intent.ACTION_VIEW,
                        Uri.parse(
                                String.format("https://api.whatsapp.com/send?phone=%s&text=%s",
                                        number, message)
                        )
                )
        );
    }
}