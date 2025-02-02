package com.app.happytails.utils.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.app.happytails.R;
import com.app.happytails.utils.Fragments.ProfileFragment;
import com.app.happytails.utils.model.UserModel;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SupportersAdapter extends RecyclerView.Adapter<SupportersAdapter.SupporterViewHolder> {

    private Context context;
    private List<UserModel> supporters;
    private FragmentManager fragmentManager;

    public SupportersAdapter(Context context, List<UserModel> supporters, FragmentManager fragmentManager) {
        this.context = context;
        this.supporters = supporters;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public SupporterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_supporter, parent, false);
        return new SupporterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupporterViewHolder holder, int position) {
        UserModel supporter = supporters.get(position);
        holder.usernameText.setText(supporter.getUsername());
        Glide.with(context).load(supporter.getUserImage()).into(holder.profilePicView);

        // Set click listener to navigate to the user's profile
        holder.itemView.setOnClickListener(v -> {
            ProfileFragment profileFragment = new ProfileFragment();
            Bundle args = new Bundle();
            args.putString("userId", supporter.getUserId());
            profileFragment.setArguments(args);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, profileFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return supporters.size();
    }

    static class SupporterViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        CircleImageView profilePicView;

        SupporterViewHolder(View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.usernameText);
            profilePicView = itemView.findViewById(R.id.profile_pic_ImageView);
        }
    }
}