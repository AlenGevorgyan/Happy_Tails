package com.app.happytails.utils.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.happytails.R;
import com.app.happytails.utils.model.PostModel;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private List<PostModel> list;
    private Context context;

    public PostAdapter(Context context, List<PostModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PostAdapter.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostHolder(view); // Returning the PostHolder
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PostHolder holder, int position) {
        PostModel post = list.get(position);

        // Setting the values for each view
        holder.userNameTv.setText(post.getUsername());

        // Format the timestamp to display as readable time
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedTime = sdf.format(post.getTimestamp());
        holder.timeTv.setText(formattedTime);

        holder.dogName.setText(post.getDogName());
        holder.dogAge.setText(String.valueOf(post.getDogAge()));

        // Display gender as text ("Male" or "Female")
        String gender = Objects.equals(post.getDogGender(), "Male") ? "Male" : "Female";
        holder.dogGender.setText(gender);

        int count = post.getLikeCount();
        if(count == 0) {
            holder.likeCountTv.setVisibility(View.INVISIBLE);
        } else {
            holder.likeCountTv.setVisibility(View.VISIBLE);
            holder.likeCountTv.setText(count == 1 ? count + " like" : count + " likes");
        }

        // Glide to load image
        Glide.with(context.getApplicationContext())
                .load(post.getImageUrl())
                .placeholder(R.drawable.user_icon) // Placeholder if imageUrl is null
                .timeout(6500)
                .into(holder.profileImage); // Loading profile image
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class PostHolder extends RecyclerView.ViewHolder {

        private CircleImageView profileImage;
        private TextView userNameTv, timeTv, likeCountTv, dogName, dogAge, dogGender;
        private ImageButton likeBtn, commentBtn;

        public PostHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize all view components from the layout
            profileImage = itemView.findViewById(R.id.profileImage);
            userNameTv = itemView.findViewById(R.id.nameTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            likeCountTv = itemView.findViewById(R.id.postAdapterLikeCount);
//            likeBtn = itemView.findViewById(R.id.likeBtnAdapter);
//            commentBtn = itemView.findViewById(R.id.commentBtnAdapter);
            dogName = itemView.findViewById(R.id.dogNameAdapter);
            dogAge = itemView.findViewById(R.id.dogAgeAdapter);
            dogGender = itemView.findViewById(R.id.dogGender);
        }
    }
}
