package com.app.happytails.utils.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.app.happytails.R;
import com.app.happytails.utils.Fragments.DogProfile;
import com.app.happytails.utils.Fragments.ProfileFragment;
import com.app.happytails.utils.model.HomeModel;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.WeakReference;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private List<HomeModel> postList;
    private WeakReference<Context> contextRef;

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

        // Load dog image
        if (post.getMainImage() != null && !post.getMainImage().isEmpty()) {
            Glide.with(context)
                    .load(post.getMainImage())
                    .placeholder(R.drawable.user_icon)
                    .into(holder.dogPic);
        } else {
            holder.dogPic.setImageResource(R.drawable.user_icon);
        }

        // Set dog details
        holder.dogName.setText(post.getDogName() != null ? post.getDogName() : "No Data");
        holder.dogAge.setText(post.getDogAge() != 0 ? "Age: " + post.getDogAge() : "No Data");
        holder.dogGender.setText(post.getDogGender() != null ? "Gender: " + post.getDogGender() : "No Data");
        holder.supportersList.setText(post.getSupporters() != null ? post.getSupporters().toString() : "No supporters");
        holder.fundingBar.setProgress(post.getFundingPercentage());

        // Fetch and set creator's details (username and picture)
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(post.getCreator());
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot userDoc = task.getResult();
                String username = userDoc.getString("username");
                String userProfileImage = userDoc.getString("userImage");

                holder.creatorName.setText(username != null ? username : "Unknown");

                if (userProfileImage != null && !userProfileImage.isEmpty()) {
                    Glide.with(context)
                            .load(userProfileImage)
                            .placeholder(R.drawable.user_icon)
                            .into(holder.creatorImage);
                } else {
                    holder.creatorImage.setImageResource(R.drawable.user_icon);
                }
            } else {
                holder.creatorName.setText("Unknown");
                holder.creatorImage.setImageResource(R.drawable.user_icon);
            }
        });

        // Handle "View Profile" button click
        holder.viewProfile.setOnClickListener(v -> {
            DogProfile fragment = new DogProfile();
            Bundle args = new Bundle();
            args.putString("dogId", post.getDogId());
            fragment.setArguments(args);

            if (context instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) context;
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        holder.creatorImage.setOnClickListener(v -> {
            ProfileFragment fragment = new ProfileFragment();
            Bundle args = new Bundle();
            args.putString("creator", post.getCreator());
            fragment.setArguments(args);

            if (context instanceof AppCompatActivity) {
                AppCompatActivity activity = (AppCompatActivity) context;
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
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
        CircleImageView dogPic, creatorImage;
        TextView dogName, dogAge, dogGender, supportersList, creatorName;
        ProgressBar fundingBar;
        Button viewProfile;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);

            dogPic = itemView.findViewById(R.id.post_picture_home);
            dogName = itemView.findViewById(R.id.dog_name_home);
            dogAge = itemView.findViewById(R.id.dog_age_home);
            dogGender = itemView.findViewById(R.id.dog_sex_home);
            supportersList = itemView.findViewById(R.id.supporters_list_home);
            fundingBar = itemView.findViewById(R.id.funding_bar_home);
            viewProfile = itemView.findViewById(R.id.view_profile);
            creatorImage = itemView.findViewById(R.id.post_user_image);
            creatorName = itemView.findViewById(R.id.usernameTv);
        }
    }
}