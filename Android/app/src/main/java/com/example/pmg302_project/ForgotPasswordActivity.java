package com.example.pmg302_project;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pmg302_project.Utils.COMMONSTRING;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText editTextEmail;
    private OkHttpClient client = new OkHttpClient();
    String ip = COMMONSTRING.ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password); // Đảm bảo bạn có layout cho ForgotPasswordActivity
        editTextEmail = findViewById(R.id.editTextEmail);
    }

    public void onBackToLogin(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void resetPassword(View view) {
    String email = editTextEmail.getText().toString().trim();
    if (email.isEmpty()) {
        editTextEmail.setError("Email is required");
        editTextEmail.requestFocus();
        return;
    }

    new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                String json = "{\"email\":\"" + email + "\"}";
                RequestBody body = RequestBody.create(json, JSON);
                Request request = new Request.Builder()
                        .url("http://"+ip+":8081/api/reset-password")
                        .post(body)
                        .build();
                Log.d(TAG, "Sending request to: " + request.url());
                Log.d(TAG, "body: " + json);
                try (Response response = client.newCall(request).execute()) {
                    String responseBody = response.body().string();
                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Xử lý khi reset password thành công
                                Toast.makeText(ForgotPasswordActivity.this, "Check your email to reset password", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Xử lý khi reset password thất bại
                                Toast.makeText(ForgotPasswordActivity.this, "Failed to reset password", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Reset password failed: " + e.getMessage());
            }
        }
    }).start();
}
}
