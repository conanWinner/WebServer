import { URLIndex, change_info_url, delete_user_url } from "./common.js";
import {logout} from "./checkLogin.js";

$(document).ready(function () {
  $("#form_change_info").submit(function (e) {
    e.preventDefault();

    const urlForChanging = change_info_url($("#email").val());
    const urlForDeleting = delete_user_url($("#email").val());
    // Xác định nút nào được nhấn
    var action = $(this).find('input[type="submit"]:focus').val();

    const fullName = $("#fullName").val();
    const phoneNumber = $("#phoneNumber").val();
    const address = $("#address").val();
    const oldPassword = $("#password").val();
    const newPassword = $("#new-password").val();
    const newPasswordRetype = $("#new-password-retype").val();

    if (action === "Send") {
    
      if (
        oldPassword === "" ||
        newPassword === "" ||
        newPasswordRetype === ""
      ) {
        alert("Có trường đang để trống!");
        return;
      }

      const dataToSend = {
        fullName: fullName,
        oldPassword: oldPassword,
        newPassword: newPassword,
        address: address,
        phoneNumber: phoneNumber,
      };

      const dataToSaveInSessionStorage = {
        fullName: fullName,
        email: $("#email").val(),
        phoneNumber: phoneNumber,
        address: address,
      };

      const jsonString = JSON.stringify(dataToSend, null, 2);
      const jsonStringToSaveInSessionStorage = JSON.stringify(
        dataToSaveInSessionStorage,
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
          url: urlForChanging,
          data: jsonString,
          success: function (response) {
            const message = response.message;
            if (message == "Success") {
              alert("Thay đổi thông tin thành công!");
              sessionStorage.setItem("User", jsonStringToSaveInSessionStorage);
              window.location.href = URLIndex;
            }
          },
          error: function (error) {
            const errorResponse = JSON.parse(error.responseText);
            alert("Error: " + errorResponse.message);
          },
        });
      }
    } else if (action === "Delete account") {
      if (oldPassword === "") {
        alert("Vui lòng nhập mật khẩu!");
        return;
      }else{
        const dataToSend = {
          password: oldPassword,
        };
        const jsonString = JSON.stringify(dataToSend, null, 2);

        $.ajax({
          type: "DELETE",
          contentType: "application/json; charset=utf-8",
          dataType: "json",
          url: urlForDeleting,
          data: jsonString,
          success: function (response) {
            const message = response.message;
            if (message == "Success") {
              alert("Xóa tài khoản thành công");
              logout();
              window.location.href = URLIndex;
            }
          },
          error: function (error) {
            const errorResponse = JSON.parse(error.responseText);
            alert("Error: " + errorResponse.message);
          },
        });
      }
    }
  });
});
