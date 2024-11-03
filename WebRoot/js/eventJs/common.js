const URL = "https://localhost:8090";

export const signup_url = URL + "/api/users";
export const signin_url = URL + "/api/login";
export const change_info_url = (emailUser) =>
  URL + `/api/users/${encodeURIComponent(emailUser)}`;
export const delete_user_url = (emailUser) =>
  URL + `/api/users/${encodeURIComponent(emailUser)}`;

export const URLIndex = URL;
