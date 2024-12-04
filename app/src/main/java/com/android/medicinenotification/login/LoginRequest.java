package com.android.medicinenotification.login;

import androidx.annotation.Nullable;

import com.android.medicinenotification.base.BaseRequest;
import com.android.volley.Response;

import java.util.Map;

public class LoginRequest extends BaseRequest {

    public LoginRequest(String id, String pw, Response.Listener<String> listener) {
        super(Method.POST,"UserLogin",listener);

        map.put("id", id);
        map.put("password", pw);
    }

    @Nullable
    @Override
    protected Map<String, String> getParams() {
        return super.getParams();
    }
}
