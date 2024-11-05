package com.example.pmg302_project;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.Fragments.Coupons.CouponsFragment;
import com.example.pmg302_project.Utils.RetrofitClient;
import com.example.pmg302_project.adapter.Coupons.CouponsListAdapter;
import com.example.pmg302_project.apis.CouponApi;
import com.example.pmg302_project.apis.ProductApi;
import com.example.pmg302_project.helper.ToolbarHelper;
import com.example.pmg302_project.model.Coupon;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_list);

        ToolbarHelper.setupToolbar(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new CouponsFragment())
                .commit();
    }


    // Thêm phương thức cho create, edit, delete coupon tại đây
}
