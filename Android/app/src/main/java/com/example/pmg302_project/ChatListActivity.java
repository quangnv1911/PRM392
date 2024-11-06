package com.example.pmg302_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.DTOs.UserDTO;
import com.example.pmg302_project.Utils.RetrofitClient;
import com.example.pmg302_project.adapter.Chats.UserChatAdapter;
import com.example.pmg302_project.apis.UserApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserChatAdapter userAdapter;
    private List<UserDTO> userList = new ArrayList<>(); // Initialize here to avoid null pointer exception
    private String storeId = "1"; // ID của store cố định cho ví dụ này

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new UserChatAdapter(userList, user -> {
            Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
            intent.putExtra("otherUserId", user.getId().toString());
            intent.putExtra("otherUserName", user.getFullName());

            startActivity(intent);
        });
        recyclerView.setAdapter(userAdapter);

        loadUsers();
    }

    private void loadUsers() {
        UserApi userApi = RetrofitClient.getClient().create(UserApi.class);
        userApi.getAllUsers().enqueue(new Callback<List<UserDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserDTO>> call, @NonNull Response<List<UserDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body()); // Update userList directly
                    userAdapter.notifyDataSetChanged(); // Notify adapter of data change
                } else {
                    Toast.makeText(ChatListActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserDTO>> call, @NonNull Throwable t) {
                Toast.makeText(ChatListActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
