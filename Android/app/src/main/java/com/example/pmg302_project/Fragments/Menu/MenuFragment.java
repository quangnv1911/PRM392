package com.example.pmg302_project.Fragments.Menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.ChatActivity;
import com.example.pmg302_project.CouponListActivity;
import com.example.pmg302_project.ManageProductActivity;
import com.example.pmg302_project.R;
import com.example.pmg302_project.adapter.MenuAdapter;
import com.example.pmg302_project.model.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {

    private RecyclerView menuRecyclerView;
    private MenuAdapter menuAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        menuRecyclerView = view.findViewById(R.id.menu_grid);
        menuRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Xem thống kê bán hàng", R.drawable.ic_statistical));
        menuItems.add(new MenuItem("Quản lý sản phẩm", R.drawable.ic_store));
        menuItems.add(new MenuItem("Quản lí mã giảm giá", R.drawable.ic_voucher));
        menuItems.add(new MenuItem("Quản lí đơn hàng", R.drawable.ic_inventory));
        menuItems.add(new MenuItem("Chat với khách hàng", R.drawable.baseline_chat_24));

        // Thêm các mục khác vào danh sách tương tự...

        menuAdapter = new MenuAdapter(menuItems, getContext(), position -> {
            switch (position) {
                case 0:
                    startActivity(new Intent(getContext(), ManageProductActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(getContext(), ManageProductActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(getContext(), CouponListActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(getContext(), ManageProductActivity.class));
                    break;
                case 4:
                    startActivity(new Intent(getContext(), ChatActivity.class));
                    break;
                // Add more cases if you add more menu items
            }
        });
        menuRecyclerView.setAdapter(menuAdapter);

        return view;
    }
}
