import { signin_url, signup_url, URLIndex } from "./common.js";

$(document).ready(function () {
  $("#form_signup").submit(function (e) {
    e.preventDefault();

    const fullname = $("#signup_fullname").val();
    const username = $("#signup_username").val();
    const email = $("#signup_email").val();
    const phonenumber = $("#signup_phonenumber").val();
    const address = $("#signup_address").val();
    const password = $("#signup_password").val();
    const re_password = $("#signup_password2").val();

    if (re_password != password) {
      alert("Password not match");
    } else {
      $.ajax({
        type: "POST",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        url: signup_url,
        data: JSON.stringify({
          fullname,
          username,
          email,
          phonenumber,
          password,
          address,
        }),
        success: function (response) {
          alert("Đăng ký thành công, vui lòng đăng nhập!");
          console.log(response);
        },
        error: function (error) {
          console.log(error);
          alert("Có lỗi xảy ra");
        },
      });
    }
  });

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
        alert("Đăng nhập thành công");
        localStorage.setItem("iduser", response.result.iduser);
        window.location.href = URLIndex;
      },
      error: function (error) {
        console.log(error);
        alert("Có lỗi xảy ra");
      },
    });
  });
});
