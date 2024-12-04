package com.android.medicinenotification.base;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class BaseRequest extends StringRequest{

    //서버 url 설정(php파일 연동)
    final static private String BASE_URL = "http://3.34.8.198/";
    String newURL;
    protected Map<String, String> map;

    public BaseRequest(int method, String url, Response.Listener<String> listener){
        super(method, BASE_URL+url+".php", listener, null);
        newURL = BASE_URL+url+".php";

        map = new HashMap<>();
    }

    public String getNewURL() {
        return newURL;
    }


    @Nullable
    @Override
    protected Map<String, String> getParams() {
        return map;
    }
}
