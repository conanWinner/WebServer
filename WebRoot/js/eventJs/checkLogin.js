function login(user) {

  if (user) {
      console.log("user", user);  // In ra đối tượng user

      // Truy cập các thuộc tính
      const fullName = user.fullName;
      const email = user.email;
      const phoneNumber = user.phoneNumber;
      const address = user.address;

      // In ra các thông tin
      console.log("Họ và tên:", fullName);
      console.log("Email:", email);
      console.log("Số điện thoại:", phoneNumber);
      console.log("Địa chỉ:", address);
  } else {
      console.log("Không tìm thấy thông tin người dùng trong localStorage.");
  }
  return user ? user : null;
}

function logout() {
  localStorage.removeItem("User");
}

$(document).ready(function () {
  const _user = localStorage.getItem("User");
  const user = JSON.parse(JSON.parse(_user));

  if (login(user) != null) {
    //   $(".icon_user").css("display", "inline");
    $("#btn_register_nav").css("display", "none");
    $("#btn_login_nav").css("display", "none");
  }

  // ==================== btn logout =================
  $("#btn-log-out").on("click", function () {
    $(".icon_user").css("display", "none");
    $(".btn-sign-in").css("display", "inline");
    logout();
    //   window.location.href = URLIndex;
  });

//  ============== Fill in personal information ===============
    const fullName = user.fullName;
    const email = user.email;
    const phoneNumber = user.phoneNumber;
    const address = user.address;

     $("#fullName").val(fullName);
     $("#email").val(email);
     $("#phoneNumber").val(phoneNumber);
     $("#address").val(address);

});
