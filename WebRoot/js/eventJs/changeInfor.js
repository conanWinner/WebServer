import { change_info_url } from "./common.js";

$(document).ready(function () {
  $("#form_change_info").submit(function (e) {
    e.preventDefault();

    const url = change_info_url($("#email").val());
    const fullName = $("#fullName").val();
    const phoneNumber = $("#phoneNumber").val();
    const address = $("#address").val();
    const oldPassword = $("#password").val();
    const newPassword = $("#new-password").val();
    const newPasswordRetype = $("#new-password-retype").val();

    const dataToSend = {
      fullName: fullName,
      oldPassword: oldPassword,
      newPassword: newPassword,
      address: address,
      phoneNumber: phoneNumber,
    };

    const dataToSaveInLocalStorage = {
      fullName: fullName,
      email: $("#email").val(),
      phoneNumber: phoneNumber,
      address: address,
    };

    const jsonString = JSON.stringify(dataToSend, null, 2);
    const jsonStringToSaveInLocalStorage = JSON.stringify(
      dataToSaveInLocalStorage,
      null,
      2
    );

    if (newPassword != newPasswordRetype) {
      alert("Mật khẩu mới không trùng khớp");
    } else {
      console.log(jsonString);
      $.ajax({
        type: "PUT",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        url: url,
        data: jsonString,
        success: function (response) {
          const message = response.message;
          if (message == "Success") {
            alert("Thay đổi thông tin thành công!");
            localStorage.setItem("User", jsonStringToSaveInLocalStorage);
          } else if (message == "Email not exist") {
            alert("Email không tồn tại!");
          } else if (message == "Your Old Password was wrong") {
            alert("Password không chính xác");
          }
        },
        error: function (error) {
          const errorResponse = JSON.parse(error.responseText);
          alert("Lỗi: " + errorResponse.message);
        },
      });
    }
  });
});
