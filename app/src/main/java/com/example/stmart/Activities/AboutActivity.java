package com.example.stmart.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.stmart.databinding.ActivityAboutBinding;


public class AboutActivity extends AppCompatActivity {


    ActivityAboutBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.btnPhone.setOnClickListener(v -> {
//            String phone1 = binding.txtPhone.getText().toString();
//            try {
//                String phone = "+91"+phone1;
//                Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", phone, null));
//                startActivity(intent);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            askPermissions();


        });

        binding.txtEmail.setOnClickListener(v -> {
            try {
                Intent intent=new Intent(Intent.ACTION_SEND);
                String[] recipients={"vij1557061@gmail.com"};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT,"Problem in app!");
                intent.putExtra(Intent.EXTRA_TEXT,"Write issues you faced in this application");
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");
                startActivity(Intent.createChooser(intent, "Send mail"));
            }catch (Exception e){
                Toast.makeText(AboutActivity.this, "Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });




    }
    private void askPermissions(){
        if (ContextCompat.checkSelfPermission(AboutActivity.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AboutActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                    100);
        }
        else
        {
            String phone1 = binding.txtPhone.getText().toString();
            try {
                String phone = "+91"+phone1;
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}