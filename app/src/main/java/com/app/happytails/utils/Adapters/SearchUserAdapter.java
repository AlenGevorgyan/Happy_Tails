package com.app.happytails.utils.Adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.happytails.R;
import com.app.happytails.utils.AndroidUtil;
import com.app.happytails.utils.FirebaseUtil;
import com.app.happytails.utils.Fragments.DogProfile;
import com.app.happytails.utils.Fragments.ProfileFragment;
import com.app.happytails.utils.model.SearchModel;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUserAdapter extends FirestoreRecyclerAdapter<SearchModel, SearchUserAdapter.UserModelViewHolder> {

    private final Context context;
    private final FragmentManager fragmentManager;
    private final boolean isSearchPerson;

    public SearchUserAdapter(@NonNull FirestoreRecyclerOptions<SearchModel> options, Context context, FragmentManager fragmentManager, boolean isSearchPerson) {
        super(options);
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.isSearchPerson = isSearchPerson;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull SearchModel model) {
        if (isSearchPerson) {
            holder.usernameText.setText(model.getUsername());
            if (model.getUserId().equals(FirebaseUtil.currentUserId())) {
                holder.usernameText.setText(model.getUsername() + " (Me)");
            }
            loadProfileImage(model.getUserId(), holder.profilePic);
        } else {
            holder.usernameText.setText(model.getDogName());
            loadDogImage(model.getDogId(), holder.profilePic);
        }

        holder.itemView.setOnClickListener(v -> {
            if (isSearchPerson) {
                ProfileFragment profileFragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("creator", model.getUserId());
                bundle.putInt("container_id", R.id.search_container);
                profileFragment.setArguments(bundle);

                fragmentManager.beginTransaction()
                        .replace(R.id.search_container, profileFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                DogProfile dogProfileFragment = new DogProfile();
                Bundle bundle = new Bundle();
                bundle.putString("dogId", model.getDogId());
                bundle.putInt("container_id", R.id.search_container);
                dogProfileFragment.setArguments(bundle);

                fragmentManager.beginTransaction()
                        .replace(R.id.search_container, dogProfileFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void loadProfileImage(String userId, CircleImageView profilePic) {
        FirebaseUtil.getOtherProfileImage(userId).addOnCompleteListener(imageTask -> {
            if (imageTask.isSuccessful() && imageTask.getResult() != null) {
                AndroidUtil.setProfilePic(profilePic.getContext(), Uri.parse(imageTask.getResult()), profilePic);
            } else {
                profilePic.setImageResource(R.drawable.user_icon);
            }
        });
    }

    private void loadDogImage(String dogId, CircleImageView profilePic) {
        FirebaseUtil.getDogProfileImage(dogId).addOnCompleteListener(imageTask -> {
            if (imageTask.isSuccessful() && imageTask.getResult() != null) {
                AndroidUtil.setProfilePic(profilePic.getContext(), Uri.parse(imageTask.getResult()), profilePic);
            } else {
                profilePic.setImageResource(R.drawable.baseline_add_24);
            }
        });
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserModelViewHolder(view);
    }

    static class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        CircleImageView profilePic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.usernameText);
            profilePic = itemView.findViewById(R.id.profilePic);
        }
    }
}