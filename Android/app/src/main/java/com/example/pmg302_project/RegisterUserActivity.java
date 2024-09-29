package com.example.pmg302_project;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterUserActivity extends AppCompatActivity {
    private EditText editTextFullName;
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        // Khởi tạo EditText
        editTextFullName = findViewById(R.id.editTextText);
    }

    public void onCancelClicked (View view){
        Intent intent = new Intent(RegisterUserActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Kết thúc RegisterUserActivity nếu không cần quay lại
    }

    public void onRegisterClicked (View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    String userName = InMemoryStorage.get("username");
                    String fullName = editTextFullName.getText().toString(); // Lấy giá trị từ EditText
                    String token = InMemoryStorage.get("token");

                    String json = "{\"username\":\"" + userName + "\", \"fullName\":\"" + fullName + "\"}";
                    RequestBody body = RequestBody.create(json, JSON);
                    Request request = new Request.Builder()
                            .url("http://172.20.109.44:8081/api/register")
                            .post(body)
                            .addHeader("Authorization", "Bearer " + token) // Thêm header Authorization
                            .build();
                    Log.d(TAG, "Sending request to: " + request);
                    Log.d(TAG, "body: " + json);
                    try (Response response = client.newCall(request).execute()) {
                        String responseBody = response.body().string();
                        if (response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject jsonObject = new JSONObject(responseBody);
                                        String serverFullName = jsonObject.optString("fullName", "");
                                        InMemoryStorage.save("fullName", serverFullName);
                                        Intent intent = new Intent(RegisterUserActivity.this, HomePageActivity.class);
                                        startActivity(intent);
                                        finish(); // Kết thúc RegisterUserActivity nếu không cần quay lại
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }

                                }
                            });
                        }
                    }
                }catch (Exception e) {
                    Log.e(TAG, "Register failed: " + e.getMessage());
                }
            }
        }).start();
    }
}