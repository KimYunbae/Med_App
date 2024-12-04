package com.android.medicinenotification.login;

import androidx.annotation.Nullable;

import com.android.medicinenotification.base.BaseRequest;
import com.android.volley.Response;

import java.util.Map;

public class UserInfoModify extends BaseRequest {

    public UserInfoModify(String id, String code, Response.Listener<String> listener) {
        super(Method.POST,"UserInfoModify",listener);
        map.put("id", id);
        map.put("code", code);
    }

    @Nullable
    @Override
    protected Map<String, String> getParams() {
        return super.getParams();
    }
}
