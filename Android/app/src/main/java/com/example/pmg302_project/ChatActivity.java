package com.example.pmg302_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pmg302_project.Fragments.Chats.ChatFragment;

public class ChatActivity extends AppCompatActivity {

    private String currentUserId;
    private String storeId = "1"; // ID cố định của chủ cửa hàng
    private boolean isAdmin;
    private String otherUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra trạng thái đăng nhập
        currentUserId = InMemoryStorage.get("userId");
        otherUserId = getIntent().getStringExtra("otherUserId");
        String otherUserName = getIntent().getStringExtra("otherUserName");
        String role = InMemoryStorage.get("role");
        boolean isAdmin = role != null && role.equalsIgnoreCase("admin");

        if (currentUserId == null || currentUserId.isEmpty()) {
            // Chuyển tới LoginActivity nếu chưa đăng nhập
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Nếu đã đăng nhập, tiếp tục thiết lập ChatActivity
        setContentView(R.layout.activity_chat);
        TextView btnHeaderToolbarMenu = findViewById(R.id.btn_header_toolbar_menu);

        if (isAdmin) {
            btnHeaderToolbarMenu.setText(otherUserName);
            String otherUserId = getIntent().getStringExtra("otherUserId"); // ID khách hàng được truyền từ `ChatListActivity`
            loadChatFragment(currentUserId, otherUserId);
        } else {
            btnHeaderToolbarMenu.setText("Chủ shop");
            // Nếu là khách, trò chuyện với admin (admin ID là `storeId`)
            loadChatFragment(currentUserId, storeId);
        }
    }

    private void loadChatFragment(String userId, String otherUserId) {
        ChatFragment chatFragment = ChatFragment.newInstance(userId, otherUserId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_chat_container, chatFragment)
                .commit();
    }
}