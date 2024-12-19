package org.webserver.httpserver.entity;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UserUpdate {
    private String fullName;
    private String email;
    private String address;
    private String phoneNumber;


    public UserUpdate() {
    }

    public UserUpdate(String fullName, String email, String address, String phoneNumber) {
        this.fullName = fullName;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

