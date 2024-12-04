package com.android.medicinenotification.data;

public class MedicineModel {

    String index;  //고유번호
    String medicineName;    //가변 데이터
    String when;   //가변 데이터
    String dose;    //가변 데이터
    String doseDate;    //가변 데이터
    String doseTime;   //가변 데이터
    String status ;   //가변 데이터

    public MedicineModel() {
    }

    public MedicineModel(String index, String medicineName, String dose, String doseDate, String doseTime, String status) {
        this.index = index;
        this.medicineName = medicineName;
        this.dose = dose;
        this.doseDate = doseDate;
        this.doseTime = doseTime;
        this.status = status;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public String getIndex() {
        return String.valueOf(index);
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getDoseDate() {
        return doseDate;
    }

    public void setDoseDate(String doseDate) {
        this.doseDate = doseDate;
    }

    public String getDoseTime() {
        return doseTime;
    }

    public void setDoseTime(String doseTime) {
        this.doseTime = doseTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MedicineModel{" +
                "index='" + index + '\'' +
                ", medicineName='" + medicineName + '\'' +
                ", dose='" + dose + '\'' +
                ", doseDate='" + doseDate + '\'' +
                ", doseTime='" + doseTime + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
