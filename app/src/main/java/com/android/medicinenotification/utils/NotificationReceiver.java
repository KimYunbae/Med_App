package com.android.medicinenotification.utils;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.android.medicinenotification.R;
import com.android.medicinenotification.data.MedicineModel;
import com.android.medicinenotification.main.ActivityMain;
import com.android.medicinenotification.main.request.GetMedicineRequest;
import com.android.medicinenotification.main.request.GetSpecificMedicineRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String channelID = intent.getStringExtra("channelID");
        if (channelID == null) {
            channelID = "channel1"; // 기본값은 "channel1"
        }
        String notify = intent.getStringExtra("notificationID");
        int notificationID = Integer.parseInt(notify);
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(notificationID, builder.build());
        }

        executeSpecificMethod(notify, context, intent);

    }

    private void executeSpecificMethod(String notify, Context ctx, Intent intent) {
        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("##INFO", "executeSpecificMethod(): getData res = " + response);
                MedicineModel model = new MedicineModel();

                try {
                    Log.i("##INFO", "onResponse(): getData res = " + response);
                    JSONObject object = new JSONObject(response);
                    JSONArray mList = object.getJSONArray("response");

                    JSONObject obj = mList.getJSONObject(0);
                    model.setIndex(obj.getString("medicine_index"));
                    model.setMedicineName(obj.getString("medicine_name"));
                    model.setWhen(obj.getString("medicine_when"));
                    model.setDose(obj.getString("medicine_dose"));
                    model.setDoseDate(obj.getString("medicine_date"));
                    model.setDoseTime(obj.getString("medicine_time"));
                    model.setStatus(obj.getString("medicine_status"));


                    Log.i("##INFO", "onResponse(): model = " + model.getMedicineName() +"//  " + model.getStatus());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                new Thread() {
                    @Override
                    public void run() {
                        super.run();

                        try {
                            sleep(5000);
                            String channelID = intent.getStringExtra("channelID");
                            String title = intent.getStringExtra("title");

                            String notify = intent.getStringExtra("notificationID");

                            String tempStatus = model.getStatus();
                            String status = "";
                            if (Objects.equals(tempStatus, "false")) {
                                status = "복용하지 않았습니다";
                            } else {
                                status = "이미 복용 하였습니다";
                            }

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, channelID)
                                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                                    .setContentTitle(title)
                                    .setContentText("복용 유무 : " + status);

                            NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
                            if (manager != null) {
                                manager.notify(Integer.parseInt(notify), builder.build());
                            }

                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }.start();


            }
        };

        GetSpecificMedicineRequest getMedicineRequest = new GetSpecificMedicineRequest(notify, response);
        RequestQueue q = Volley.newRequestQueue(ctx);
        q.add(getMedicineRequest);


    }
}