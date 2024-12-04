package com.android.medicinenotification.main;

import com.android.medicinenotification.data.MedicineModel;

public interface ItemClickListener {

    void onItemDelete(int position, MedicineModel medicineModel);
    void onItemEdit(int position, MedicineModel medicineModel);
}
