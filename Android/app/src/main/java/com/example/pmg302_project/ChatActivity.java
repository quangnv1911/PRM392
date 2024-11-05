package com.example.pmg302_project;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pmg302_project.Fragments.Chats.ChatFragment;

public class ChatActivity extends AppCompatActivity {

    private String currentUserId;
    private String storeId = "1"; // Giả sử ID của chủ cửa hàng là cố định

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra trạng thái đăng nhập
        currentUserId = InMemoryStorage.get("userId");

        if (currentUserId == null || currentUserId.isEmpty()) {
            // Chuyển tới LoginActivity nếu chưa đăng nhập
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // Kết thúc ChatActivity
            return; // Thoát khỏi onCreate để không thực hiện các bước tiếp theo
        }

        // Nếu đã đăng nhập, tiếp tục thiết lập ChatActivity
        setContentView(R.layout.activity_chat);
        loadChatFragment();
    }

    private void loadChatFragment() {
        ChatFragment chatFragment = ChatFragment.newInstance(currentUserId, storeId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_chat_container, chatFragment)
                .commit();
    }
}
