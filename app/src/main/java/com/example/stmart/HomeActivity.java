package com.example.stmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.stmart.Activities.AddItemActivity;
import com.example.stmart.Activities.OptionsActivity;
import com.example.stmart.Activities.SplashActivity;
import com.example.stmart.Adapters.ItemAdapter;
import com.example.stmart.Model.ItemModel;
import com.example.stmart.Model.UserModel;
import com.example.stmart.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {


    ActivityHomeBinding binding;
    DatabaseReference reference;

    ArrayList<ItemModel> list = new ArrayList<>();
    ItemAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = FirebaseDatabase.getInstance().getReference().child("Items");


        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.recyclerView.setHasFixedSize(true);

                GridLayoutManager layoutManager = new GridLayoutManager(HomeActivity.this,2);
                binding.recyclerView.setLayoutManager(layoutManager);
                getData();
            }
        },400);




        binding.imgProfile.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, OptionsActivity.class)));

        binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = binding.inputSearch.getText().toString();
                if (input.length() > 0){
                    getResults(input.toLowerCase());
                }else {
                    getData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        userState();



    }

    private void showCatDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Select category");
        builder.setCancelable(false);

        String[] items = {"Textbooks","Laptops","Watches","Mobiles","Stationary Items"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView listView = ((AlertDialog)dialog).getListView();
                Object object = listView.getItemAtPosition(which);
                String cat =object.toString();



                Intent intent = new Intent(HomeActivity.this, AddItemActivity.class);
                intent.putExtra("category",cat);
                startActivity(intent);

                dialog.dismiss();



            }
        });
        builder.setPositiveButton("Close",null);
        builder.create().show();

    }

    private void getData(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    binding.progressBar.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        ItemModel model = dataSnapshot.getValue(ItemModel.class);
                        if (model !=null){
                            list.add(model);
                        }
                    }

                    adapter = new ItemAdapter(HomeActivity.this,list,"all");
                    binding.recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                }else {
                    Toast.makeText(HomeActivity.this, "No items!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Error: "+
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void getResults(String s){
        binding.recyclerView.setHasFixedSize(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        ItemModel model = dataSnapshot.getValue(ItemModel.class);
                        assert model !=null;
                        if (model.getName().toLowerCase().contains(s)){
                            list.add(model);
                        }

                    }

                    adapter = new ItemAdapter(HomeActivity.this,list,"all");
                    binding.recyclerView.setAdapter(adapter);

                    if (list.size() > 0){
                        Log.d("DATA","HAI");

                    }else {
                        Toast.makeText(HomeActivity.this, "No posts available!", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void userState(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserModel model = snapshot.getValue(UserModel.class);
                    if (model !=null){

                        binding.fab.setOnClickListener(v -> {
                            if (model.getVerified()){
                                showCatDialogue();
                            }else {
                                Toast.makeText(HomeActivity.this, "You account is not verified!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        try {
                            Picasso.get().load(model.getImage()).placeholder(R.drawable.placeholder)
                                    .into(binding.imgProfile);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }else {
                    Toast.makeText(HomeActivity.this, "User not exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}