package com.android.medicinenotification.main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;


import com.android.medicinenotification.data.DlgType;
import com.android.medicinenotification.data.MedicineModel;
import com.android.medicinenotification.databinding.DialogMedicineBinding;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;

public class MedicineDialog {

    private Context mContext;
    private DialogMedicineBinding binding;
    Dialog dlg;
    DlgResult dlgResult;
    DlgType type;
    private MedicineModel mData = new MedicineModel();

    //아침,점심,저녁
    private ArrayList<String> whenItems = new ArrayList<String>() {{
        add("아침");
        add("점심");
        add("저녁");
    }};


    @SuppressLint("NewApi")
    public MedicineDialog(Context context, DlgType type) {
        mContext = context;
        this.type = type;
        dlgResult = (ActivityMain) context;

        binding = DialogMedicineBinding.inflate(LayoutInflater.from(mContext));

        dlg = new Dialog(mContext);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dlg.setCanceledOnTouchOutside(true);
        dlg.setCancelable(true);

        dlg.setContentView(binding.getRoot());


/**
 * view click
 */
        binding.btCancel.setOnClickListener(v -> {
            dismiss();
        });
        binding.btRegister.setOnClickListener(v -> {
            try {
                String ex = (String) binding.tvDoseDate.getText().toString();
                if (ex.trim().equals("")) {
                    Toast.makeText(mContext, "복용일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String reg = (String) binding.tvDoseTime.getText().toString();
                if (reg.trim().equals("")) {
                    Toast.makeText(mContext, "복용시간을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String name = (String) binding.edMedicineName.getText().toString();
                if (name.trim().equals("")) {
                    Toast.makeText(mContext, "약 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String dose = (String) binding.edDose.getText().toString();
                if (dose.trim().equals("")) {
                    Toast.makeText(mContext, "복용량을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                MedicineModel m = getMedicineData();
                dlgResult.onClickPositive(m,type);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            dlg.dismiss();
        });

        binding.tvDoseDate.setOnClickListener(v -> {
            binding.calDate.setVisibility(View.VISIBLE);

        });

        binding.tvDoseTime.setOnClickListener(v -> {
            binding.tpTime.setVisibility(View.VISIBLE);

        });

        binding.btTimeOk.setOnClickListener(v -> {
            binding.tpTime.setVisibility(View.GONE);
            binding.tvDoseTime.setText(binding.tpTime.getHour() + ":" + binding.tpTime.getMinute());
        });

        binding.calDate.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                binding.tvDoseDate.setText(i + "-" + checkLength(i1 + 1) + "-" + checkLength(i2));
                binding.calDate.setVisibility(View.GONE);
            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, whenItems);

        binding.spWhen.setAdapter(arrayAdapter);
    }

    public void show() {
        dlg.show();
    }

    public void dismiss() {
        dlg.dismiss();
    }

    public void setMedicineInfo(MedicineModel mData) {
        this.mData = mData;

        binding.edMedicineName.setText(mData.getMedicineName());
        binding.edDose.setText(mData.getDose());
        binding.tvDoseDate.setText(mData.getDoseDate());
        binding.tvDoseTime.setText(mData.getDoseTime());
    }


    public MedicineModel getMedicineData() throws ParseException {
        MedicineModel mData = new MedicineModel();

        if (Objects.equals(this.mData.getIndex(), "null")) {
            Long tempTime = System.currentTimeMillis();
            String index = tempTime.toString().substring(tempTime.toString().length() - 6);

            mData.setIndex(index);
        } else {
            mData.setIndex(this.mData.getIndex());
        }

        mData.setMedicineName(binding.edMedicineName.getText().toString());
        mData.setWhen(binding.spWhen.getSelectedItem().toString());
        mData.setDose(binding.edDose.getText().toString());
        mData.setDoseDate(binding.tvDoseDate.getText().toString());
        mData.setDoseTime(binding.tvDoseTime.getText().toString());

        return mData;
    }


    private String checkLength(int t) {
        String time = String.valueOf(t);

        if (time.length() < 2) {
            time = "0" + time;
        }

        return time;
    }


    /**
     * Dialog의 버튼 이벤트를 처리하기 위한 인터페이스
     */
    public interface DlgResult {

        /**
         * Dialog의 추가 버튼을 눌렀을 때 호출되는 메소드
         * 반환값은 FoodModel이지만 값은 이름, 등록날짜, 등록장소만 있음.
         */
        void onClickPositive(MedicineModel data,DlgType type);

    }
}









































