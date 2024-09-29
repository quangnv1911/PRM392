package com.example.pmg302_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.adapter.ChatAdapter;
import com.example.pmg302_project.model.ChatMessage;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private Button buttonSend;
    private Button buttonLogin;
    private TextView textViewLoginPrompt;
    private DatabaseReference chatDatabaseReference;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private static final String TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this); // Initialize Firebase
        Log.d(TAG, "Firebase initialized in ChatActivity");

        setContentView(R.layout.activity_chat);

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewLoginPrompt = findViewById(R.id.textViewLoginPrompt);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(chatAdapter);

        chatDatabaseReference = FirebaseDatabase.getInstance().getReference("chats");

        String username = InMemoryStorage.get("username");
        if (username == null || username.isEmpty()) {
            textViewLoginPrompt.setVisibility(View.VISIBLE);
            buttonLogin.setVisibility(View.VISIBLE);
            buttonSend.setEnabled(false);

            buttonLogin.setOnClickListener(v -> {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            });
        } else {
            buttonSend.setOnClickListener(v -> sendMessage(username));

            chatDatabaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    chatMessages.add(chatMessage);
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                    recyclerViewChat.scrollToPosition(chatMessages.size() - 1);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    private void sendMessage(String sender) {
        String message = editTextMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            long timestamp = System.currentTimeMillis();
            ChatMessage chatMessage = new ChatMessage(message, sender, timestamp);
            chatDatabaseReference.push().setValue(chatMessage);
            editTextMessage.setText("");
        }
    }
}