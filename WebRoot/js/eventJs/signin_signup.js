import { signin_url, signup_url, URLIndex } from "./common.js";

$(document).ready(function () {
// ======================= SIGN UP =======================
  $("#form_signup").submit(function (e) {
    e.preventDefault();

    const fullName = $("#signup_fullname").val();
    const email = $("#signup_email").val();
    const phoneNumber = $("#signup_phonenumber").val();
    const address = $("#signup_address").val();
    const password = $("#signup_password").val();
    const re_password = $("#signup_password2").val();

    const dataToSend =
    {
              fullName: fullName,
              password: password,
              email: email,
              address: address,
              phoneNumber: phoneNumber
    }
    const jsonString = JSON.stringify(dataToSend, null, 2);
    if (re_password != password) {
      alert("Password not match");
    } else {
    console.log("JSON to be sent:", dataToSend);
      $.ajax({
        type: "POST",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        url: signup_url,
        data: jsonString,
        success: function (response) {
          console.log(response);
          if(response.message == "Success"){
            alert("Đăng ký thành công, vui lòng đăng nhập!");
          }else if(response.message == "Email has already existed"){
            alert("Email đã tồn tại");
          }

        },
        error: function (error) {
          console.log(error);
          alert("Có lỗi xảy ra");
        },
      });
    }
  });

// ======================= SIGN UP =======================
  $("#form_signin").submit(function (e) {
    e.preventDefault();

    const email = $("#signin_email").val();
    const password = $("#signin_password").val();

    $.ajax({
      type: "POST",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      url: signin_url,
      data: JSON.stringify({ email, password }),
      success: function (response) {
        if(response.message == "Success"){
            localStorage.setItem("User", JSON.stringify(response.result));
            alert("Đăng nhập thành công");
            window.location.href = URLIndex;
        }else if (response.message == "Email or password was wrong!"){
            alert("Email hoặc mật khẩu sai");
        }
      },
      error: function (error) {
        console.log(error);
        alert("Có lỗi xảy ra");
      },
    });
  });
});
