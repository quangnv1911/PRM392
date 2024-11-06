package com.example.pmg302_project.Fragments.Chats;

import static com.example.pmg302_project.R.*;
import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.InMemoryStorage;
import com.example.pmg302_project.R;
import com.example.pmg302_project.adapter.Chats.MessageAdapter;
import com.example.pmg302_project.model.Message;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private static final String ARG_CURRENT_USER_ID = "2";
    private static final String ARG_OTHER_USER_ID = "1";

    private String currentUserId;
    private String otherUserId;
    private RecyclerView recyclerView;
    private EditText inputMessage;
    private ImageButton sendButton;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private DatabaseReference chatRef;

    public static ChatFragment newInstance(String currentUserId, String otherUserId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CURRENT_USER_ID, currentUserId);
        args.putString(ARG_OTHER_USER_ID, otherUserId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUserId = getArguments().getString(ARG_CURRENT_USER_ID);
            otherUserId = getArguments().getString(ARG_OTHER_USER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewChat);
        inputMessage = view.findViewById(R.id.editTextMessage);
        sendButton = view.findViewById(R.id.send_chat_button);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(getContext(), messageList, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(messageAdapter);

        String role = InMemoryStorage.get("role");
        boolean isAdmin = role != null && role.equalsIgnoreCase("admin");
        if (isAdmin) {
            chatRef = FirebaseDatabase.getInstance().getReference("chats")
                    .child(currentUserId + "_" + otherUserId);
        } else {
            chatRef = FirebaseDatabase.getInstance().getReference("chats")
                    .child(otherUserId + "_" + currentUserId);
        }
        loadMessages();
        sendButton.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void sendMessage() {
        String messageText = inputMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            Message message = new Message(currentUserId, messageText, System.currentTimeMillis());
            chatRef.child("messages").push().setValue(message);
            inputMessage.setText("");
        }
    }

    private void loadMessages() {
        chatRef.child("messages").addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                messageList.add(message);
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}

