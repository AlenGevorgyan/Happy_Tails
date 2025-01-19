package com.app.happytails.utils.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.app.happytails.R;
import com.app.happytails.utils.Fragments.GalleryFragment;
import com.app.happytails.utils.model.PostModel;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends FirestoreRecyclerAdapter<PostModel, PostAdapter.PostViewHolder> {

    private Context context;
    private static final String TAG = "PostAdapter";

    public PostAdapter(@NonNull FirestoreRecyclerOptions<PostModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull PostModel post) {
        Log.d(TAG, "Loading image from URL: " + post.getPostMainImageUrl());

        if (post.getPostMainImageUrl() != null && !post.getPostMainImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(post.getPostMainImageUrl())
                    .placeholder(R.drawable.user_icon)
                    .timeout(6500)
                    .into(holder.dogPic);
        } else {
            holder.dogPic.setImageResource(R.drawable.user_icon);
        }

        holder.dogName.setText(post.getDogName() != null ? post.getDogName() : "Unknown Dog");
        holder.dogAge.setText("Estimated Age: " + (post.getDogAge() != 0 ? String.valueOf(post.getDogAge()) : "N/A"));
        holder.dogGender.setText("Gender: " + (post.getDogGender() != null ? post.getDogGender() : "Unknown Gender"));
        holder.supportersList.setText(post.getSupportersList() != null ? post.getSupportersList().toString() : "No supporters");
        holder.fundingBar.setProgress(post.getFundingPercentage());

        holder.veterinaryBtn.setOnClickListener(v -> {
            holder.vetLastVisitDate.setText(post.getVetLastVisitDate() != null ? post.getVetLastVisitDate() : "No data");
            holder.vetLastVisitDate.setVisibility(View.VISIBLE);
            Toast.makeText(context, "Vet last visit date: " + post.getVetLastVisitDate(), Toast.LENGTH_SHORT).show();
        });

        holder.galleryBtn.setOnClickListener(v -> {
            ArrayList<String> galleryImageUrls = new ArrayList<>(post.getGalleryImageUrls());
            if (!galleryImageUrls.isEmpty()) {
                GalleryFragment galleryFragment = new GalleryFragment();
                Bundle args = new Bundle();
                args.putStringArrayList("galleryImageUrls", galleryImageUrls);
                galleryFragment.setArguments(args);

                if (context instanceof AppCompatActivity) {
                    AppCompatActivity activity = (AppCompatActivity) context;
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, galleryFragment)
                            .addToBackStack(null)
                            .commit();
                }
            } else {
                Toast.makeText(context, "No gallery images available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView dogPic;
        private TextView dogName, dogAge, dogGender, vetLastVisitDate, supportersList;
        private ProgressBar fundingBar;
        private Button galleryBtn, veterinaryBtn, donationBtn;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            dogPic = itemView.findViewById(R.id.profile_picture);
            dogName = itemView.findViewById(R.id.dog_name);
            dogAge = itemView.findViewById(R.id.dog_age);
            dogGender = itemView.findViewById(R.id.dog_sex);
            vetLastVisitDate = itemView.findViewById(R.id.vet_last_visit_date);
            supportersList = itemView.findViewById(R.id.supporters_list);
            fundingBar = itemView.findViewById(R.id.funding_bar);
            galleryBtn = itemView.findViewById(R.id.gallery_button);
            veterinaryBtn = itemView.findViewById(R.id.veterinary_button);
            donationBtn = itemView.findViewById(R.id.donation_button);
        }
    }
}