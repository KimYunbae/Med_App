package com.android.medicinenotification.signup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.medicinenotification.databinding.ActivitySigUpBinding;
import com.android.medicinenotification.login.ActivityLogin;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivitySignUp extends AppCompatActivity {
    private ActivitySigUpBinding binding;
    private List<String> itemList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        itemClick();
        itemList = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m"
                , "n"
                ,"o"
                ,"p"
                ,"q"
                ,"r"
                ,"s"
                ,"t"
                ,"u"
                ,"v"
                ,"w"
                ,"x"
                ,"y"
                ,"z"
                ,"1"
                ,"2"
                ,"3"
                ,"4"
                ,"5"
                ,"6"
                ,"7"
                ,"8"
                ,"9"
                ,"0");

    }

    private void itemClick() {
        //로그인 버튼 클릭시 로그인 화면으로 이동
        binding.tvCreate.setOnClickListener(v -> {
            String id = binding.edEmail.getText().toString();
            String pw = binding.edPassword.getText().toString();
            String pwCheck = binding.edPasswordCheck.getText().toString();
            String role = binding.rgType.getCheckedRadioButtonId() == binding.rbPatient.getId() ? "환자" : "보호자";
            String code = "";


            if (role == "환자") {
                code = createRandomCode();
            }


            if (id.equals("") || pw.equals("") || pwCheck.equals("")) {
                Toast.makeText(ActivitySignUp.this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pw.equals(pwCheck)) {
                Toast.makeText(ActivitySignUp.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }


            Response.Listener<String> response = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("##INFO", "onResponse(): response = " + response);

                    try {
                        JSONObject object = new JSONObject(response);
                        String resBoolean = object.getString("success");
                        if (resBoolean.equals("false")) {
                            Toast.makeText(ActivitySignUp.this, "회원가입 실패 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    if (response.contains("success")) {
                        Toast.makeText(ActivitySignUp.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ActivitySignUp.this, ActivityLogin.class);
                        startActivity(i);
                        finish();
                    } else {
                        Log.i("##INFO", "onResponse(): response = " + response);
                        Log.i("##INFO", "onResponse(): 회원가입 실패");
                    }
                }
            };

            SignUpRequest signUpRequest = new SignUpRequest(id, role,pw,code, response);
            RequestQueue q = Volley.newRequestQueue(ActivitySignUp.this);
            q.add(signUpRequest);
        });
    }

    private String createRandomCode() {
        int randomNumber = (int) (Math.random() * 10)+1;
        String code = "";
        for (int i = 0; i < randomNumber; i++) {
            code += itemList.get((int) (Math.random() * itemList.size()));
        }
        return code;
    }
}
