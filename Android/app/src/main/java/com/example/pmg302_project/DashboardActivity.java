package com.example.pmg302_project;

import android.content.Intent;
import android.os.Bundle;

import com.example.pmg302_project.Fragments.Menu.MenuFragment;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.pmg302_project.databinding.ActivityDashboardBinding;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MenuFragment myFragment = new MenuFragment();
        fragmentTransaction.replace(R.id.fragment_container, myFragment);
        fragmentTransaction.commit();

        // Thiết lập sự kiện cho nút Menu
        TextView btnMenu = findViewById(R.id.btn_header_toolbar_menu);
        btnMenu.setOnClickListener(v -> {
            // Xử lý sự kiện nhấn nút Menu
            Toast.makeText(DashboardActivity.this, "Menu clicked", Toast.LENGTH_SHORT).show();
        });

        // Thiết lập sự kiện cho icon Thông báo
        ImageView iconNotification = findViewById(R.id.icon_header_toolbar_notification);
        iconNotification.setOnClickListener(v -> {
            // Xử lý sự kiện nhấn vào icon Thông báo
            Toast.makeText(DashboardActivity.this, "Notification clicked", Toast.LENGTH_SHORT).show();
        });

        // Thiết lập sự kiện cho icon Cài đặt
        ImageView iconSettings = findViewById(R.id.icon_header_toolbar_settings);
        iconSettings.setOnClickListener(v -> {
            // Xử lý sự kiện nhấn vào icon Cài đặt
//            Toast.makeText(DashboardActivity.this, "Settings clicked", Toast.LENGTH_SHORT).show();
            iconSettings.setOnClickListener(this::showSettingsMenu);

        });
    }

    private void showSettingsMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenu().add("Đăng xuất"); // Thêm tùy chọn Đăng xuất

        // Xử lý sự kiện khi người dùng chọn Đăng xuất
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Đăng xuất")) {
                showLogoutDialog(); // Hiển thị hộp thoại xác nhận đăng xuất
            }
            return true;
        });

        popupMenu.show();
    }

    private void showLogoutDialog() {
        // Tạo hộp thoại xác nhận đăng xuất
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    // Thực hiện hành động đăng xuất
                    Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

                    InMemoryStorage.clear();
                    Intent intent3 = new Intent(DashboardActivity.this, HomePageActivity.class);
                    startActivity(intent3);
                    // Thêm logic để chuyển về màn hình đăng nhập hoặc kết thúc phiên làm việc
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}