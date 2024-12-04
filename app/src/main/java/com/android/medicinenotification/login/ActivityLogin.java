package com.android.medicinenotification.login;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.medicinenotification.databinding.ActivityLoginBinding;
import com.android.medicinenotification.main.ActivityMain;
import com.android.medicinenotification.signup.ActivitySignUp;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityLogin extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= 32) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED)
                return;
            ActivityResultLauncher<String> launcher = registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(), isGranted -> {

                    }
            );
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }

        itemClick();

//        //region ---- Test Section  ---
//
//        binding.edEmail.setText("tester");
//        binding.edPassword.setText("aaa");
//        binding.tvLogin.performClick();
//        //endregion
    }

    private void itemClick() {
        binding.tvLogin.setOnClickListener(view -> {
            String id = binding.edEmail.getText().toString();
            String pw = binding.edPassword.getText().toString();

            Response.Listener<String> res = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("##INFO", "onResponse(): res = " + response);
                    JSONObject res = null;
                    try {
                        res = new JSONObject(response);
                        String resBoolean = res.getString("success");
                        if (resBoolean.equals("true")) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                String id = obj.getString("id");
                                String role = obj.getString("role");
                                String code = obj.getString("code");

                                Log.i("##INFO", "onResponse(): id = " + id + ", role = " + role + ", code = " + code);

                                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("userid", id);
                                editor.putString("role", role);
                                editor.putString("code", code);
                                editor.apply();

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            Toast.makeText(ActivityLogin.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(ActivityLogin.this, ActivityMain.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(ActivityLogin.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            };


            LoginRequest loginRequest = new LoginRequest(id, pw, res);
            RequestQueue q = Volley.newRequestQueue(this);
            q.add(loginRequest);

        });


        /**
         * 회원가입 버튼 클릭시 화면 이동
         */
        binding.tvSignUp.setOnClickListener(v -> {
            Intent i = new Intent(ActivityLogin.this, ActivitySignUp.class);
            startActivity(i);
            finish();
        });
    }
}


























