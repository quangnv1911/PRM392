package com.example.pmg302_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.R;
import com.example.pmg302_project.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        holder.bind(chatMessage);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewMessage;
        private TextView textViewSender;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewSender = itemView.findViewById(R.id.textViewSender);
        }

        public void bind(ChatMessage chatMessage) {
            textViewMessage.setText(chatMessage.getMessage());
            textViewSender.setText(chatMessage.getSender());
        }
    }
}