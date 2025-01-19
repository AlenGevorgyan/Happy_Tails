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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.app.happytails.R;
import com.app.happytails.utils.Fragments.GalleryFragment;
import com.app.happytails.utils.Fragments.ProfileFragment;
import com.app.happytails.utils.model.HomeModel;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private List<HomeModel> postList;
    private WeakReference<Context> contextRef;
    private static final String TAG = "HomeAdapter";

    public HomeAdapter(Context context, List<HomeModel> postList) {
        this.contextRef = new WeakReference<>(context);
        this.postList = postList;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_post, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        HomeModel post = postList.get(position);
        Context context = contextRef.get();

        if (context == null) return;

        Log.d(TAG, "Loading image from URL: " + post.getPostMainImageUrl());

        if (post.getPostMainImageUrl() != null && !post.getPostMainImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(post.getPostMainImageUrl())
                    .placeholder(R.drawable.user_icon)
                    .timeout(6500)
                    .into(holder.dogPic);
        } else {
            holder.dogPic.setImageResource(R.drawable.baseline_add_24);
        }

        holder.dogName.setText(post.getDogName() != null ? post.getDogName() : "Unknown Dog");
        holder.dogAge.setText(post.getDogAge() != 0 ? String.valueOf(post.getDogAge()) : "N/A");
        holder.dogGender.setText(post.getDogGender() != null ? post.getDogGender() : "Unknown Gender");
        holder.supportersList.setText(post.getSupportersList() != null ? post.getSupportersList().toString() : "No supporters");
        holder.fundingBar.setProgress(post.getFundingPercentage());

        holder.veterinaryBtn.setOnClickListener(v -> {
            holder.vetLastVisitDate.setText(post.getVetLastVisitDate() != null ? post.getVetLastVisitDate() : "No data");
            holder.vetLastVisitDate.setVisibility(View.VISIBLE);
            Toast.makeText(context, "Vet last visit date: " + post.getVetLastVisitDate(), Toast.LENGTH_SHORT).show();
        });

        String userId = post.getUserId();
        DocumentReference userInfo = FirebaseFirestore.getInstance().collection("users").document(userId);
        userInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    String profileImageUrl = documentSnapshot.getString("userImage");
                    String username = documentSnapshot.getString("username");

                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(context)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.user_icon)
                                .timeout(6500)
                                .into(holder.profileImage);
                    } else {
                        holder.profileImage.setImageResource(R.drawable.user_icon);
                    }

                    holder.username.setText(username != null ? username : "Unknown User");
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        holder.profileImage.setOnClickListener(v -> {
            ProfileFragment profileFragment = new ProfileFragment();
            Bundle args = new Bundle();
            args.putString("profileUid", post.getUserId());
            profileFragment.setArguments(args);

            if (context instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) context;
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, profileFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        holder.galleryBtn.setOnClickListener(v -> {
            GalleryFragment galleryFragment = new GalleryFragment();
            Bundle args = new Bundle();
            args.putStringArrayList("galleryImageUrls", new ArrayList<>(post.getGalleryImageUrls()));
            galleryFragment.setArguments(args);

            if (context instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) context;
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, galleryFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class HomeViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profileImage, dogPic;
        private TextView username, dogName, dogAge, dogGender, vetLastVisitDate, supportersList;
        private ProgressBar fundingBar;
        private Button galleryBtn, veterinaryBtn, donationBtn;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.post_user_image);
            username = itemView.findViewById(R.id.usernameTv);
            dogName = itemView.findViewById(R.id.dog_name_home);
            dogAge = itemView.findViewById(R.id.dog_age_home);
            dogPic = itemView.findViewById(R.id.post_picture_home);
            dogGender = itemView.findViewById(R.id.dog_sex_home);
            supportersList = itemView.findViewById(R.id.supporters_list_home);
            fundingBar = itemView.findViewById(R.id.funding_bar_home);
            galleryBtn = itemView.findViewById(R.id.gallery_button_home);
            veterinaryBtn = itemView.findViewById(R.id.veterinary_button_home);
            donationBtn = itemView.findViewById(R.id.donation_button_home);
        }
    }
}