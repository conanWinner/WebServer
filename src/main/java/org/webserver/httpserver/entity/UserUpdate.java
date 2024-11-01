package org.webserver.httpserver.entity;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UserUpdate {
    private String fullName;
    private String oldPassword;
    private String newPassword;
    private String address;
    private String phoneNumber;


    public UserUpdate() {
    }

    public UserUpdate(String fullName, String oldPassword, String newPassword, String address, String phoneNumber) {
        this.fullName = fullName;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Phương thức chuyển đổi sang JSON
    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}"; // Trả về một chuỗi JSON rỗng trong trường hợp lỗi
        }
    }
}

