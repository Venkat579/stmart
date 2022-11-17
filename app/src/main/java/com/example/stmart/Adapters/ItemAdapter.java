package com.example.stmart.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stmart.Activities.DetailsActivity;
import com.example.stmart.Model.ItemModel;
import com.example.stmart.R;
import com.example.stmart.databinding.ItemProductBinding;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context context;
    public static ArrayList<ItemModel> list;
    String where;

    ItemProductBinding binding;

    public ItemAdapter(Context context, ArrayList<ItemModel> list,String where) {
        this.context = context;
        this.list = list;
        this.where = where;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemProductBinding.inflate(LayoutInflater.from(context));

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ItemModel model = list.get(position);
        if (model !=null){
            if (where.equals("all")){
                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("position",position);
                    context.startActivity(intent);
                });
            }else {
                holder.itemView.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Choose option");
                    builder.setCancelable(false);

                    String[] items = {"Delete Post","Sold Out"};
                    builder.setItems(items, (dialog, which) -> {
                        switch (which){
                            case 0:

                                FirebaseDatabase.getInstance().getReference()
                                        .child("Items")
                                        .child(model.getId())
                                        .removeValue();
                                notifyItemRemoved(position);
                                Toast.makeText(context, "Post deleted!", Toast.LENGTH_SHORT).show();

                                break;

                            case 1:

                                FirebaseDatabase.getInstance().getReference()
                                        .child("Items")
                                        .child(model.getId())
                                        .child("sold")
                                        .setValue(true);
                                notifyItemChanged(position);
                                Toast.makeText(context, "Post has been sold!", Toast.LENGTH_SHORT).show();

                                break;

                        }
                    });

                    builder.setPositiveButton("Close",null);
                    builder.create().show();
                });


            }

            if (model.getSold()){
                binding.imgSold.setVisibility(View.VISIBLE);
            }else {
                binding.imgSold.setVisibility(View.GONE);
            }
            binding.productTitle.setText(model.getName());
            binding.amount.setText("₹"+model.getPrice());
            try{
                Picasso.get().load(model.getImage()).placeholder(R.drawable.logo)
                        .into(binding.productImage);
            }catch (Exception e){
                e.getMessage();
            }


        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemProductBinding binding;
        public ViewHolder(@NonNull ItemProductBinding productBinding) {
            super(productBinding.getRoot());
            binding = productBinding;
        }
    }
}
