package com.android.medicinenotification.main.request;

import android.util.Log;

import androidx.annotation.Nullable;

import com.android.medicinenotification.base.BaseRequest;
import com.android.volley.Response;

import java.util.Map;

public class AddMedicineRequest extends BaseRequest {

    public AddMedicineRequest(String code,String index, String name,String when, String dose,String date,String time,String status, Response.Listener<String> listener) {
        super(Method.POST,"AddMedicineTask",listener);

        map.put("code", code);
        map.put("index", index);
        map.put("name", name);
        map.put("when", when);
        map.put("dose", dose);
        map.put("date", date);
        map.put("time", time);
        map.put("status",status);

    }

    @Nullable
    @Override
    protected Map<String, String> getParams() {
        return map;
    }
}
