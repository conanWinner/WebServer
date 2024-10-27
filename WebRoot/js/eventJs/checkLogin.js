function login() {
  const _iduser = localStorage.getItem("iduser");
  console.log(_iduser);
  return _iduser ? _iduser : null;
}

function logout() {
  localStorage.removeItem("iduser");
}

$(document).ready(function () {
  if (login() != null) {
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
});
