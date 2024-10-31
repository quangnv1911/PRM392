package com.example.pmg302_project;

import static android.content.ContentValues.TAG;


import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Intent;

import android.content.IntentSender;

import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.pmg302_project.Utils.COMMONSTRING;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private static final int REQ_ONE_TAP = 100;
    private OkHttpClient client = new OkHttpClient();
    private EditText editTextEmail, editTextPassword;
    Button landingPage;
    String ip = COMMONSTRING.ip;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId("58005151095-t5vfq2f2nle1bn8s2n9rt1qgg4atnjgd.apps.googleusercontent.com")
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .setAutoSelectEnabled(true)
                .build();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);

        TextWatcher loginTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String emailInput = editTextEmail.getText().toString().trim();
                String passwordInput = editTextPassword.getText().toString().trim();

                buttonLogin.setEnabled(!emailInput.isEmpty() && !passwordInput.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        editTextEmail.addTextChangedListener(loginTextWatcher);
        editTextPassword.addTextChangedListener(loginTextWatcher);
    }

    public void buttonGoogleSignIn(View view) {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {
                            startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                    null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d(TAG, e.getLocalizedMessage());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                String username = credential.getId();
                String password = credential.getPassword();
//                String fullName = credential.getGivenName();
                if (idToken != null) {
                    // Got an ID token from Google. Use it to authenticate
                    // with your backend.
                    Log.d(TAG, "Got ID token.");
                    loginToBackend(username, username);
                } else if (password != null) {
                    // Got a saved username and password. Use them to authenticate
                    // with your backend.
                    Log.d(TAG, "Got password.");
                }
            } catch (ApiException e) {
                Log.d(TAG, "Exception: " + e.getLocalizedMessage());
            }
        }
    }

    private void loginToBackend(String username, String fullName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MediaType JSON = MediaType.get("application/json; charset=utf-8");
                    String json = "{\"username\":\"" + username + "\", \"password\":\"password\", \"method\": \"google\", \"fullName\": \"" + fullName + "\"}";
                    RequestBody body = RequestBody.create(json, JSON);
                    Request request = new Request.Builder()
                            .url("http://" + ip + ":8081/api/login")
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
                                    try {
                                        JSONObject jsonObject = new JSONObject(responseBody);
                                        String token = jsonObject.getString("token");
                                        String refreshToken = jsonObject.getString("refreshToken");
                                        String serverFullName = jsonObject.optString("fullName", "");
                                        InMemoryStorage.save("token", token);
                                        InMemoryStorage.save("refreshToken", refreshToken);
                                        InMemoryStorage.save("username", username);

                                        Intent intent;
                                        if (serverFullName == null || serverFullName.isEmpty()) {
                                            intent = new Intent(MainActivity.this, RegisterUserActivity.class);
                                        } else {
                                            InMemoryStorage.save("fullName", serverFullName);
                                            intent = new Intent(MainActivity.this, HomePageActivity.class);
                                        }
                                        startActivity(intent);
                                        finish(); // Kết thúc MainActivity nếu không cần quay lại
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "Response code: " + response.code());
                                    Log.d(TAG, "Response body: " + responseBody);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Login failed: " + e.getMessage());
                }
            }
        }).start();
    }

    public void onLoginClicked(View view) {
        String userName = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (userName.isEmpty() || password.isEmpty()) {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show());
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    String json = "{\"username\":\"" + userName + "\", \"password\":\"" + password + "\", \"method\": \"other\"}";
                    RequestBody body = RequestBody.create(json, JSON);
                    Request request = new Request.Builder()
                            .url("http://" + ip + ":8081/api/login")
                            .post(body)
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
                                        String token = jsonObject.getString("token");
                                        String refreshToken = jsonObject.getString("refreshToken");
                                        String username = jsonObject.getString("username");
                                        String serverFullName = jsonObject.optString("fullName", "");
                                        InMemoryStorage.save("token", token);
                                        InMemoryStorage.save("refreshToken", refreshToken);
                                        InMemoryStorage.save("username", username);

                                        Intent intent;
                                        if (serverFullName.equals("null") || serverFullName.isEmpty()) {
                                            intent = new Intent(MainActivity.this, RegisterUserActivity.class);
                                        } else {
                                            InMemoryStorage.save("fullName", serverFullName);
                                            intent = new Intent(MainActivity.this, HomePageActivity.class);
                                        }
                                        startActivity(intent);
                                        finish();
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Login failed. Wrong username or password!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Login failed. Wrong username or password!", Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }

    public void onForgotPasswordClicked(View view) {
        Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }
}