package com.app.happytails.utils.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.happytails.R;
import com.app.happytails.utils.Fragments.ProfileFragment;
import com.app.happytails.utils.SearchActivity; // Import SearchActivity
import com.app.happytails.utils.model.UserModel;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserAdapter.UserModelViewHolder> {

    private final Context context;
    private final FragmentManager fragmentManager;

    public SearchUserAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context, FragmentManager fragmentManager) {
        super(options);
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        // Bind user data to the views
        holder.usernameText.setText(model.getUsername());
        Glide.with(context)
                .load(model.getUserImage())
                .placeholder(R.drawable.user_icon)
                .error(R.drawable.user_icon)
                .into(holder.profilePic);

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            // Create and navigate to ProfileFragment
            ProfileFragment profileFragment = new ProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putString("creator", model.getUserId());
            profileFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.search_container, profileFragment)
                    .addToBackStack(null)
                    .commit();

            // Toggle the search toolbar visibility
            if (context instanceof SearchActivity) {
                ((SearchActivity) context).toggleSearchToolbar(false);
            }


        });


    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserModelViewHolder(view);
    }

    static class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        ImageView profilePic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.usernameText);
            profilePic = itemView.findViewById(R.id.profilePic);
        }
    }
}