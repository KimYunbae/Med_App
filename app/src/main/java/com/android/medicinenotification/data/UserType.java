package com.android.medicinenotification.data;

public enum UserType {
    PATIENT("환자"),
    PARENT("보호자");

    private String type;

    UserType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
