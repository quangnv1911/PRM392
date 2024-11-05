package com.example.pmg302_project.adapter.Chats;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.R;
import com.example.pmg302_project.model.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private Context context;
    private List<Message> messageList;
    private String currentUserId;

    public MessageAdapter(Context context, List<Message> messageList, String currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        holder.messageTextView.setMaxWidth(dpToPx(250));


        if (message.getSenderId().equals(currentUserId)) {
            // Tin nhắn của người dùng hiện tại
            holder.containerLayout.setGravity(Gravity.END); // Căn phải cho container
            holder.messageTextView.setBackgroundResource(R.drawable.bg_message_right);
        } else {
            // Tin nhắn từ người khác
            holder.containerLayout.setGravity(Gravity.START); // Căn trái cho container
            holder.messageTextView.setBackgroundResource(R.drawable.bg_message_left);
        }

        holder.messageTextView.setText(message.getMessage());
    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        LinearLayout containerLayout;
        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            containerLayout = itemView.findViewById(R.id.containerChatItemLayout);
        }
    }
}

