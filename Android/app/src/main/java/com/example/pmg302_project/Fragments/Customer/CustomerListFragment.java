    package com.example.pmg302_project.Fragments.Customer;

    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Toast;
    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;
    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;
    import java.util.List;
    import com.example.pmg302_project.R;
    import com.example.pmg302_project.adapter.CustomerAdapter;
    import com.example.pmg302_project.model.Account;
    import com.example.pmg302_project.service.CustomerService;
    import com.example.pmg302_project.util.RetrofitClientInstance;

    public class CustomerListFragment extends Fragment {

        private RecyclerView recyclerView;
        private CustomerAdapter customerAdapter;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_customer_list, container, false);
            recyclerView = view.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // Fetch customers immediately after setting up the RecyclerView
            fetchCustomers();

            return view;
        }

        private void fetchCustomers() {
            CustomerService customerService = RetrofitClientInstance.getCustomerService();
            Call<List<Account>> call = customerService.getCustomers();
            call.enqueue(new Callback<List<Account>>() {
                @Override
                public void onResponse(Call<List<Account>> call, Response<List<Account>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Account> customers = response.body();
                        // Use requireActivity() which guarantees a non-null activity
// Replace `new CustomerAdapter(requireActivity(), customers);` with the following:
                        // Replace `new CustomerAdapter(requireActivity(), customers);` with the following:
                        customerAdapter = new CustomerAdapter(requireActivity().getSupportFragmentManager(), customers);
                        recyclerView.setAdapter(customerAdapter);
                    } else {
                        Toast.makeText(getContext(), "Failed to load customers", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Account>> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }