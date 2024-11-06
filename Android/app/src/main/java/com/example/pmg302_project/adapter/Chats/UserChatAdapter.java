package com.example.pmg302_project.adapter.Chats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.DTOs.UserDTO;
import com.example.pmg302_project.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.UserViewHolder> {

    private final List<UserDTO> userList;
    private final OnUserClickListener onUserClickListener;

    public UserChatAdapter(List<UserDTO> userList, OnUserClickListener onUserClickListener) {
        this.userList = userList;
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserDTO user = userList.get(position);
        holder.bind(user);
        holder.cardView.setOnClickListener(v -> onUserClickListener.onUserClick(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface OnUserClickListener {
        void onUserClick(UserDTO user);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView userNameTextView;
        ImageView userAvatarImageView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewUser);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            userAvatarImageView = itemView.findViewById(R.id.userAvatarImageView); // Thêm ImageView cho avatar
        }

        public void bind(UserDTO user) {
            userNameTextView.setText(user.getFullName());
            Picasso.get()
                    .load(user.getImage()) // URL của ảnh đại diện
                    .placeholder(R.drawable.ic_avatar_placeholder) // Ảnh placeholder khi đang tải
                    .error(R.drawable.ic_avatar_placeholder) // Ảnh khi tải lỗi
                    .into(userAvatarImageView);
        }
    }
}
