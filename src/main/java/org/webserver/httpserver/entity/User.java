package org.webserver.httpserver.entity;

import com.fasterxml.jackson.databind.ObjectMapper;

public class User {

    private int iduser;
    private String fullName;
    private String password;
    private String email;
    private String address;
    private String phoneNumber;


    public String getIduser() {
        return String.valueOf(iduser);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullname) {
        this.fullName = fullname;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public User() {
    }

    public User(String iduser, String fullname, String password, String email, String phonenumber, String address) {
        this.fullName = fullname;
        this.password = password;
        this.email = email;
        this.phoneNumber = phonenumber;
        this.address = address;
    }

    public User(String fullname, String email, String phonenumber, String address) {
        this.fullName = fullname;
        this.email = email;
        this.phoneNumber = phonenumber;
        this.address = address;
    }

    public User(String iduser, String fullname, String email, String phonenumber, String address) {
        this.iduser = Integer.parseInt(iduser);
        this.fullName = fullname;
        this.email = email;
        this.phoneNumber = phonenumber;
        this.address = address;
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
