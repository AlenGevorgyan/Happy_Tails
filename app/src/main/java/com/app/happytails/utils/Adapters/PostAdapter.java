package com.app.happytails.utils.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.happytails.R;
import com.app.happytails.utils.model.PostModel;
import com.bumptech.glide.Glide;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private List<PostModel> list;
    private Context context;

    public PostAdapter(Context context, List<PostModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_post, parent, false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        PostModel post = list.get(position);

        // Set profile picture using Glide
        Glide.with(context)
                .load(post.getPostMainImageUrl()) // Assuming this is the main image URL field
                .placeholder(R.drawable.user_icon)
                .timeout(6500)
                .into(holder.profileImage);

        // Set dog name, age, and gender with null checks and default values
        holder.dogName.setText(post.getDogName() != null ? post.getDogName() : "Unknown");
        holder.dogAge.setText(post.getDogAge() != 0 ? String.valueOf(post.getDogAge()) : "N/A");
        holder.dogGender.setText(post.getDogGender() != null ? post.getDogGender() : "Unknown");

        // Setting supporters list
        holder.supportersList.setText(post.getSupportersList() != null ? post.getSupportersList().toString() : "No supporters");

        // Monthly funding progress
        holder.fundingBar.setProgress(post.getFundingPercentage());

        // Set an OnClickListener for the veterinary button to display the last visit date
        holder.veterinaryBtn.setOnClickListener(v -> {
            holder.vetLastVisitDate.setText(post.getVetLastVisitDate() != null ? post.getVetLastVisitDate() : "No data");
            holder.vetLastVisitDate.setVisibility(View.VISIBLE);
            Toast.makeText(context, "Vet last visit date: " + post.getVetLastVisitDate(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class PostHolder extends RecyclerView.ViewHolder {

        private ImageView profileImage; // ImageView for profile picture
        private TextView dogName, dogAge, dogGender, vetLastVisitDate, supportersList;
        private ProgressBar fundingBar;
        private Button galleryBtn, veterinaryBtn, donationBtn;

        public PostHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize all view components from the layout
            profileImage = itemView.findViewById(R.id.profile_picture); // Adjusted for ImageView
            dogName = itemView.findViewById(R.id.dog_name);
            dogAge = itemView.findViewById(R.id.dog_age);
            dogGender = itemView.findViewById(R.id.dog_sex);
            vetLastVisitDate = itemView.findViewById(R.id.vet_last_visit_date); // Assuming this is part of the layout
            supportersList = itemView.findViewById(R.id.supporters_list); // Assuming this is part of the layout
            fundingBar = itemView.findViewById(R.id.funding_bar); // Monthly funding progress
            galleryBtn = itemView.findViewById(R.id.gallery_button);
            veterinaryBtn = itemView.findViewById(R.id.veterinary_button);
            donationBtn = itemView.findViewById(R.id.donation_button);
        }
    }
}