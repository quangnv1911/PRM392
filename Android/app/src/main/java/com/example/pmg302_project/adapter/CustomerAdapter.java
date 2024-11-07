package com.example.pmg302_project.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.Fragments.Customer.CustomerListFragment;
import com.example.pmg302_project.Fragments.Customer.OrderDetailFragment;
import com.example.pmg302_project.ManageCustomerActivity;
import com.example.pmg302_project.R;
import com.example.pmg302_project.model.Account;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    private List<Account> customerList;
    private FragmentManager fragmentManager;

    public CustomerAdapter(FragmentManager fragmentManager, List<Account> customerList) {
        this.fragmentManager = fragmentManager;
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Account account = customerList.get(position);
        holder.fullName.setText(account.getFullname());
        holder.details.setText(account.getPhone() + "\n" + account.getAddress());
        holder.index.setText(String.valueOf(position + 1));

        // Handle the click event
        holder.itemView.setOnClickListener(v -> {
            Log.d("CustomerAdapter", "Clicked accountId: " + account.getId());  // Log the account ID
            OrderDetailFragment orderDetailFragment = OrderDetailFragment.newInstance(account.getId());

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, orderDetailFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView fullName;
        TextView details;
        TextView index;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.text_fullname);
            details = itemView.findViewById(R.id.text_details);
            index = itemView.findViewById(R.id.text_index);
        }
    }
}
