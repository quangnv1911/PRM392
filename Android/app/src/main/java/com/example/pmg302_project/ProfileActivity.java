package com.example.pmg302_project;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pmg302_project.Utils.COMMONSTRING;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView fullnameTextView;
    private TextView phoneTextView;
    private TextView addressTextView;
    private TextView createdOnTextView;
    private ImageView profileImageView;
    private String ip = COMMONSTRING.ip;

    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fullnameTextView = findViewById(R.id.fullnameEditText);
        phoneTextView = findViewById(R.id.phoneEditText);
        addressTextView = findViewById(R.id.addressEditText);
        createdOnTextView = findViewById(R.id.createdOnTextView);
        profileImageView = findViewById(R.id.profileImageView);

        fetchUserProfile();
    }

    private void fetchUserProfile() {
        String username = InMemoryStorage.get("username");
        String url = "http://"+ip+":8081/api/account?username=" + username;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody);
                            String fullname = jsonObject.getString("fullname");
                            String phone = jsonObject.getString("phone");
                            String address = jsonObject.getString("address");
                            String image = jsonObject.getString("image");
                            String createdOn = jsonObject.getString("createdOn");

                            fullnameTextView.setText(fullname);
                            phoneTextView.setText(phone);
                            addressTextView.setText(address);
                            createdOnTextView.setText(createdOn);
                            Picasso.get().load(image).into(profileImageView);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }
}