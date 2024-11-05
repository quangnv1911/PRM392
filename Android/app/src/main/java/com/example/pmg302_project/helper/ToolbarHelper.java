package com.example.pmg302_project.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.pmg302_project.DashboardActivity;
import com.example.pmg302_project.HomePageActivity;
import com.example.pmg302_project.InMemoryStorage;
import com.example.pmg302_project.ManageProductActivity;
import com.example.pmg302_project.R;

public class ToolbarHelper {

    public static void setupToolbar(Activity activity) {
        TextView btnMenu = activity.findViewById(R.id.btn_header_toolbar_menu);
        ImageView iconNotification = activity.findViewById(R.id.icon_header_toolbar_notification);
        ImageView iconSettings = activity.findViewById(R.id.icon_header_toolbar_settings);

        // Sự kiện cho nút Menu
        btnMenu.setOnClickListener(v -> {
            Intent intent = new Intent(activity, DashboardActivity.class);
            activity.startActivity(intent);
        });

        // Sự kiện cho icon Thông báo
        iconNotification.setOnClickListener(v -> {
            Toast.makeText(activity, "Notification clicked", Toast.LENGTH_SHORT).show();
        });

        // Sự kiện cho icon Cài đặt
        iconSettings.setOnClickListener(v -> showSettingsMenu(activity, v));
    }

    private static void showSettingsMenu(Context context, View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenu().add("Đăng xuất");

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Đăng xuất")) {
                showLogoutDialog(context);
            }
            return true;
        });

        popupMenu.show();
    }

    private static void showLogoutDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    Toast.makeText(context, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

                    InMemoryStorage.clear();
                    // Xử lý đăng xuất
                    Intent intent = new Intent(context, HomePageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
