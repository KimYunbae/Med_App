package com.android.medicinenotification.main;

import static android.provider.MediaStore.Files.FileColumns.PARENT;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.medicinenotification.data.DlgType;
import com.android.medicinenotification.data.MedicineModel;
import com.android.medicinenotification.data.UserType;
import com.android.medicinenotification.databinding.ActivityMainBinding;
import com.android.medicinenotification.login.LoginRequest;
import com.android.medicinenotification.login.UserInfoModify;
import com.android.medicinenotification.main.request.AddMedicineRequest;
import com.android.medicinenotification.main.request.DeleteMedicineRequest;
import com.android.medicinenotification.main.request.GetMedicineRequest;
import com.android.medicinenotification.main.request.ModifyMedicineRequest;
import com.android.medicinenotification.utils.NotificationReceiver;
import com.android.medicinenotification.utils.NotificationSchedule;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ActivityMain extends AppCompatActivity implements MedicineDialog.DlgResult, ItemClickListener {
    private ActivityMainBinding binding;
    private MedicineListAdapter adapter;

    private ArrayList<MedicineModel> medicineList;
    private ArrayList<MedicineModel> selectedDateMedicineList = new ArrayList<>();
    private String selectedDate;
    private String[] permissionList = {Manifest.permission.POST_NOTIFICATIONS};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setEvent();
        getData();
        requestPermission();
    }

    private void requestPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // SDK 30 이상
                requestPermissions(permissionList, 1001);

            } else {
                // SDK 30 미만
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // 권한이 없는 경우 권한 요청
                    ActivityCompat.requestPermissions(this, permissionList, 1001);
                }
            }
        } catch (IllegalArgumentException e) {
            Log.e("##ERROR", "requestPermission()   IllegalArgumentException : " + e);
        } catch (Exception e) {
            Log.e("##ERROR", "requestPermission()   Exception : " + e);
        }
    }

    private void init() {

        medicineList = new ArrayList<>();
        binding.reDetailDate.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MedicineListAdapter(medicineList, this);
        binding.reDetailDate.setAdapter(adapter);


        initDate();
        checkUserType();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initDate() {
        LocalDate localDate = LocalDate.now();
        String year = String.valueOf(localDate.getYear());
        String month = localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() : String.valueOf(localDate.getMonthValue());
        String day = localDate.getDayOfMonth() < 10 ? "0" + localDate.getDayOfMonth() : String.valueOf(localDate.getDayOfMonth());

        selectedDate = year + "-" + month + "-" + day;
    }

    /**
     * 보호자일 경우 환자와 연결이 됐는지 점검하는 함수
     */
    private void checkUserType() {
        String userType = getSharedPreferences("pref", MODE_PRIVATE).getString("role", "");
        String userCode = getSharedPreferences("pref", MODE_PRIVATE).getString("code", "");

        if (userType.equals("보호자")) {
            if (userCode.equals("")) {
                Toast.makeText(this, "환자와 연결이 되어있지 않습니다. 연결을 먼저 해주세요.", Toast.LENGTH_SHORT).show();

                // 연결이 되어있지 않을경우 연결 버튼 활성화
                binding.btnConnect.setVisibility(View.VISIBLE);
                binding.tvCode.setVisibility(View.GONE);
            }
        } else {
            // 사용자가 환자일 경우

            binding.tvCode.setVisibility(View.VISIBLE);
            binding.btnConnect.setVisibility(View.GONE);
            binding.tvCode.setText("환자 코드 : " + userCode);
        }
    }


    private void setEvent() {
        binding.calManageMedicine.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String temMonth = month < 10 ? "0" + (month + 1) : String.valueOf(month + 1);
            String temDay = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);

            selectedDate = year + "-" + temMonth + "-" + temDay;

            getMedicineDataForSelectedDate();
        });

        binding.imbtAddMedicine.setOnClickListener(v -> {
            MedicineDialog dialog = new MedicineDialog(this, DlgType.ADD);
            dialog.show();
        });

        binding.btnConnect.setOnClickListener(v -> {
            EditText et = new EditText(this);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("환자 코드 입력");
            builder.setView(et);
            builder.setPositiveButton("확인", (dialog, which) -> {
                String code = et.getText().toString();
                connectUser(code);
            });
            builder.setNegativeButton("취소", (dialog, which) -> {
                dialog.dismiss();
            });
            builder.show();

        });
    }

    private void connectUser(String code) {
        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("##INFO", "onResponse(): respone = " + response);
                    JSONObject object = new JSONObject(response);
                    String resBoolean = object.getString("success");
                    if (resBoolean.equals("false")) {
                        Toast.makeText(ActivityMain.this, "연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if (response.contains("success")) {
                    Toast.makeText(ActivityMain.this, "환자와 연결되었습니다.", Toast.LENGTH_SHORT).show();
                    getSharedPreferences("pref", MODE_PRIVATE).edit().putString("code", code).apply();
                    binding.btnConnect.setVisibility(View.GONE);

                    getData();
                } else {
                    Log.i("##INFO", "onResponse(): response = " + response);
                }
            }
        };

        String id = getSharedPreferences("pref", MODE_PRIVATE).getString("userid", "");
        Log.i("##INFO", "connectUser(): userId, code = " + id + ", " + code);
        UserInfoModify userInfoModify = new UserInfoModify(id, code, response);
        RequestQueue q = Volley.newRequestQueue(this);
        q.add(userInfoModify);
    }

    /**
     * 서버에서 데이터를 가져와 화면에 적용
     */
    private void getData() {
        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("##INFO", "onResponse(): getData res = " + response);
                    JSONObject object = new JSONObject(response);
                    JSONArray mList = object.getJSONArray("response");

                    medicineList.clear();
                    for (int i = 0; i < mList.length(); i++) {
                        JSONObject obj = mList.getJSONObject(i);
                        MedicineModel model = new MedicineModel();
                        model.setIndex(obj.getString("medicine_index"));
                        model.setMedicineName(obj.getString("medicine_name"));
                        model.setWhen(obj.getString("medicine_when"));
                        model.setDose(obj.getString("medicine_dose"));
                        model.setDoseDate(obj.getString("medicine_date"));
                        model.setDoseTime(obj.getString("medicine_time"));

                        medicineList.add(model);
                    }

                    Date today = new Date();
                    for (MedicineModel model : medicineList) {
                        String[] date = model.getDoseDate().split("-");
                        String[] time = model.getDoseTime().split(":");

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]), Integer.parseInt(time[0]), Integer.parseInt(time[1]));

                        if (today.getTime() > calendar.getTimeInMillis()) {
                            // 현재 시간이 약 복용 시간보다 크면 알람을 취소한다.
                            cancelAlarm(model);
                        } else {
                            // 현재 시간이 약 복용 시간보다 작으면 알람을 등록한다.
                            registerAlarm(model);
                        }
                    }


                    getMedicineDataForSelectedDate();


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        String code = getSharedPreferences("pref", MODE_PRIVATE).getString("code", "");
        GetMedicineRequest getMedicineRequest = new GetMedicineRequest(code, response);
        RequestQueue q = Volley.newRequestQueue(ActivityMain.this);
        q.add(getMedicineRequest);
    }

    /**
     * 가져온 데이터를 현재 클릭한 날짜에 맞게 필터링하여 리스트에 넣는다.
     */
    private void getMedicineDataForSelectedDate() {
        selectedDateMedicineList.clear();

        for (MedicineModel model : medicineList) {
            if (model.getDoseDate().equals(selectedDate)) {
                selectedDateMedicineList.add(model);
            }
        }

        adapter.setList(selectedDateMedicineList);


    }


    @Override
    public void onClickPositive(MedicineModel data, DlgType type) {

        if (type == DlgType.ADD) {
            addMedicine(data);
        } else if (type == DlgType.EDIT) {
            modifyMedicine(data);
        }
    }

    private void addMedicine(MedicineModel data) {
        registerAlarm(data);

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    String resBoolean = object.getString("success");
                    if (resBoolean.equals("false")) {
                        Toast.makeText(ActivityMain.this, "데이터 업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if (response.contains("success")) {
                    Toast.makeText(ActivityMain.this, "데이터가 정상적으로 업로드 되었습니다.", Toast.LENGTH_SHORT).show();
                    getData();
                } else {
                    Log.i("##INFO", "onResponse(): response = " + response);
                }
            }
        };

        String code = getSharedPreferences("pref", MODE_PRIVATE).getString("code", "");
        AddMedicineRequest addMedicineRequest = new AddMedicineRequest(code, data.getIndex(), data.getMedicineName(), data.getWhen(), data.getDose(), data.getDoseDate(), data.getDoseTime(),"false", response);
        RequestQueue q = Volley.newRequestQueue(ActivityMain.this);
        q.add(addMedicineRequest);
    }

    private void modifyMedicine(MedicineModel data) {
        cancelAlarm(data);
        registerAlarm(data);
        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    String resBoolean = object.getString("success");
                    if (resBoolean.equals("false")) {
                        Toast.makeText(ActivityMain.this, "데이터 수정에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if (response.contains("success")) {
                    Toast.makeText(ActivityMain.this, "데이터가 정상적으로 수정 되었습니다.", Toast.LENGTH_SHORT).show();
                    getData();
                } else {
                    Log.i("##INFO", "onResponse(): response = " + response);
                }
            }
        };


        String code = getSharedPreferences("pref", MODE_PRIVATE).getString("code", "");
        ModifyMedicineRequest modifyMedicineRequest = new ModifyMedicineRequest(code, data.getIndex(), data.getMedicineName(), data.getWhen(), data.getDose(), data.getDoseDate(), data.getDoseTime(), response);
        RequestQueue q = Volley.newRequestQueue(ActivityMain.this);
        q.add(modifyMedicineRequest);
    }


    private void registerAlarm(MedicineModel data) {
        // 알람 등록
        NotificationSchedule notificationSchedule = new NotificationSchedule(this);
        notificationSchedule.scheduleNotification(data);
    }

    private void cancelAlarm(MedicineModel data) {
        // 알람 취소
        try {
            int notificationID = Integer.parseInt(data.getIndex());
            NotificationSchedule notificationSchedule = new NotificationSchedule(this);
            notificationSchedule.cancelNotification(notificationID);
        }catch (Exception e) {
            Log.e("##ERROR", "cancelAlarm: error = "+e);
        }
    }


    @Override
    public void onItemDelete(int position, MedicineModel medicineModel) {
        cancelAlarm(medicineModel);

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    String resBoolean = object.getString("success");
                    if (resBoolean.equals("false")) {
                        Toast.makeText(ActivityMain.this, "데이터 삭제 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if (response.contains("success")) {
                    Toast.makeText(ActivityMain.this, "데이터가 정상적으로 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                    getData();
                } else {
                    Log.i("##INFO", "onResponse(): response = " + response);
                }
            }
        };

        DeleteMedicineRequest deleteMedicineRequest = new DeleteMedicineRequest(medicineModel.getIndex(), response);
        RequestQueue q = Volley.newRequestQueue(ActivityMain.this);
        q.add(deleteMedicineRequest);

    }

    @Override
    public void onItemEdit(int position, MedicineModel medicineModel) {
        MedicineDialog medicineDialog = new MedicineDialog(this, DlgType.EDIT);
        medicineDialog.setMedicineInfo(medicineModel);
        medicineDialog.show();
    }
}
