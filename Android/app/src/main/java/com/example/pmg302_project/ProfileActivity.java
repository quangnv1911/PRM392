package com.example.pmg302_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pmg302_project.Utils.COMMONSTRING;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    private EditText fullnameEditText;
    private EditText phoneEditText;
    private EditText addressEditText;
    private TextView createdOnTextView;
    private ImageView profileImageView;
    private Button updateProfileButton;
    private String ip = COMMONSTRING.ip;

    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fullnameEditText = findViewById(R.id.fullnameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        createdOnTextView = findViewById(R.id.createdOnTextView);
        profileImageView = findViewById(R.id.profileImageView);
        updateProfileButton = findViewById(R.id.updateProfileButton);

        fetchUserProfile();

        updateProfileButton.setOnClickListener(view -> updateProfile());
    }

    private void fetchUserProfile() {
        String username = InMemoryStorage.get("username");
        String url = "http://" + ip + ":8081/api/account?username=" + username;

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

                            fullnameEditText.setText(fullname);
                            phoneEditText.setText(phone);
                            addressEditText.setText(address);
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

    private void updateProfile() {
        String username = InMemoryStorage.get("username");
        String fullname = fullnameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String address = addressEditText.getText().toString();

        String url = "http://" + ip + ":8081/api/account/update-profile";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("fullname", fullname);
            jsonObject.put("phone", phone);
            jsonObject.put("address", address);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(ProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}