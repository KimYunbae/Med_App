package com.android.medicinenotification.signup;

import android.util.Log;

import androidx.annotation.Nullable;

import com.android.medicinenotification.base.BaseRequest;
import com.android.volley.Response;

import java.util.Map;

public class SignUpRequest extends BaseRequest {

    public SignUpRequest(String id,String role, String pw, String code, Response.Listener<String> listener) {
        super(Method.POST,"UserRegister",listener);

        map.put("id", id);
        map.put("role", role);
        map.put("password", pw);
        map.put("code", code);

        Log.i("##INFO", "SignUpRequest(): "+getNewURL());
    }

    @Nullable
    @Override
    protected Map<String, String> getParams() {
        return map;
    }
}
