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

    const dataToSend = {
      fullName: fullName,
      password: password,
      email: email,
      address: address,
      phoneNumber: phoneNumber,
    };
    const jsonString = JSON.stringify(dataToSend, null, 2);
    if (re_password != password) {
      alert("Mật khẩu không trùng khớp");
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
          if (response.message == "Success") {
            alert("Đăng ký thành công, vui lòng đăng nhập!");
          }
        },
        error: function (error) {
          const errorResponse = JSON.parse(error.responseText);
          alert("Error: " + errorResponse.message);
        },
      });
    }
  });

  // ======================= SIGN IN =======================
  $("#form_signin").submit(function (e) {
    e.preventDefault();

    const email = $("#signin_email").val();
    const password = $("#signin_password").val();
    console.log(email);
    console.log(password);

    $.ajax({
      type: "POST",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      url: signin_url,
      data: JSON.stringify({ email, password }),
      success: function (response) {
        if (response.message == "Success") {
          const user = JSON.parse(response.result);
          sessionStorage.setItem("User", JSON.stringify(user, null, 2));
          alert("Đăng nhập thành công");
          window.location.href = URLIndex;
        }
      },
      error: function (error) {
        console.log("error là: " + error)
        const errorResponse = JSON.parse(error.responseText);
        alert("Error: " + errorResponse.message);
      },
    });
  });
});
