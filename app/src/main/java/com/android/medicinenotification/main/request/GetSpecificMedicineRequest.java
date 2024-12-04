package com.android.medicinenotification.main.request;

import androidx.annotation.Nullable;

import com.android.medicinenotification.base.BaseRequest;
import com.android.volley.Response;

import java.util.Map;

public class GetSpecificMedicineRequest extends BaseRequest {

    public GetSpecificMedicineRequest(String index, Response.Listener<String> listener) {
        super(Method.POST,"GetSpecificMedicines",listener);

        map.put("index", index);

    }

    @Nullable
    @Override
    protected Map<String, String> getParams() {
        return map;
    }
}
