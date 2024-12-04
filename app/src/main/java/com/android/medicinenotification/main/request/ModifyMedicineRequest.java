package com.android.medicinenotification.main.request;

import android.util.Log;

import androidx.annotation.Nullable;

import com.android.medicinenotification.base.BaseRequest;
import com.android.volley.Response;

import java.util.Map;

public class ModifyMedicineRequest extends BaseRequest {

    public ModifyMedicineRequest(String code, String index, String name, String when, String dose, String date, String time, Response.Listener<String> listener) {
        super(Method.POST, "ModifyMedicine", listener);

        map.put("code", code);
        map.put("index", index);
        map.put("name", name);
        map.put("when", when);
        map.put("dose", dose);
        map.put("date", date);
        map.put("time", time);
        Log.i("##INFO", "ModifyMedicineRequest():  userid: " + code + ", index: " + index + ", name: " + name + ", when: " + when + ", dose: " + dose + ", date: " + date + ", time: " + time);

    }

    @Nullable
    @Override
    protected Map<String, String> getParams() {
        return map;
    }
}
