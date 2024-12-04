package com.android.medicinenotification.main.request;

import androidx.annotation.Nullable;

import com.android.medicinenotification.base.BaseRequest;
import com.android.volley.Response;

import java.util.Map;

public class GetMedicineRequest extends BaseRequest {

    public GetMedicineRequest(String code, Response.Listener<String> listener) {
        super(Method.POST,"GetMedicines",listener);

        map.put("code", code);

    }

    @Nullable
    @Override
    protected Map<String, String> getParams() {
        return map;
    }
}
