package com.android.medicinenotification.login;

import androidx.annotation.Nullable;

import com.android.medicinenotification.base.BaseRequest;
import com.android.volley.Response;

import java.util.Map;

public class UserInfoRequest extends BaseRequest {

    public UserInfoRequest(Response.Listener<String> listener) {
        super(Method.POST,"GetUserInfo",listener);
    }

    @Nullable
    @Override
    protected Map<String, String> getParams() {
        return super.getParams();
    }
}
