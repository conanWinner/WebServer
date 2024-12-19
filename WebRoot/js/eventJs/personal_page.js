import {
  URLIndex,
  change_info_url,
  delete_user_url,
  getAllUsers,
} from "./common.js";

$(document).ready(async function () {
  // State management
  let users = new Map();

  // API functions
  async function fetchUsers() {
    try {
      const response = await fetch(getAllUsers);
      if (!response.ok) throw new Error("Failed to fetch users");
      const data = await response.json();
      setUsers(data);
      renderUsers();
    } catch (error) {
      console.error("Error fetching users:", error);
      showError("Could not fetch users. Please try again later.");
    }
  }

  function setUsers(data) {
    data.forEach((user) => users.set(user.iduser, user));
    console.log(users);
  }

  async function updateUser(iduser, updatedData) {
    try {
      const response = await fetch(`${change_info_url}/${iduser}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(updatedData),
      });
      if (!response.ok) throw new Error("Failed to update user");
      const result = await response.json();

      if (result.message === "Success") {
        users.set(iduser, { ...users.get(iduser), ...updatedData });
        updateRowById(iduser, updatedData);
        alert("User updated successfully!");
      } else {
        throw new Error(result.message);
      }
    } catch (error) {
      console.error("Error updating user:", error);
      showError("Could not update user. Please try again later.");
    }
  }

  async function deleteUser(iduser) {
    try {
      const response = await fetch(`${delete_user_url}/${iduser}`, {
        method: "DELETE",
      });
      if (!response.ok) throw new Error("Failed to delete user");

      users.delete(iduser);
      deleteRowById(iduser);
      alert("User deleted successfully!");
    } catch (error) {
      console.error("Error deleting user:", error);
      showError("Could not delete user. Please try again later.");
    }
  }

  // UI rendering functions
  function renderUsers() {
    const tbody = $("#userTableBody");
    tbody.empty();
    users.forEach((user) => {
      tbody.append(createUserRow(user));
    });
  }

  function createUserRow(user) {
    return `
      <tr data-iduser="${user.iduser}">
        <td class="td-id">${user.iduser}</td>
        <td class="td-fullname">${user.fullname}</td>
        <td class="td-email">${user.email}</td>
        <td class="td-phonenumber">${user.phonenumber}</td>
        <td class="td-address">${user.address}</td>
        <td>
          <button class="btn btn-sm btn-info btn-edit" data-iduser="${user.iduser}">Edit</button>
          <button class="btn btn-sm btn-danger btn-delete" data-iduser="${user.iduser}">Delete</button>
        </td>
      </tr>
    `;
  }

  function updateRowById(iduser, updatedData) {
    const $row = $(`#userTableBody tr[data-iduser="${iduser}"]`);
    $row.find(".td-fullname").text(updatedData.fullname);
    $row.find(".td-phonenumber").text(updatedData.phonenumber);
    $row.find(".td-address").text(updatedData.address);
  }

  function deleteRowById(iduser) {
    $(`#userTableBody tr[data-iduser="${iduser}"]`).remove();
  }

  function showError(message) {
    const errorContainer = $("#errorMessage");
    errorContainer.text(message).show();
  }

  // Event handlers
  $(document).on("click", ".btn-edit", function () {
    const $row = $(this).closest("tr");
    const iduser = $row.data("iduser");

    const user = users.get(iduser + "");

    console.log(user);

    // Convert cells to inputs
    $row
      .find(".td-fullname")
      .html(
        `<input type="text" class="form-control form-control-sm" name="fullname" value="${user.fullname}">`
      );
    $row
      .find(".td-email")
      .html(
        `<input type="text" class="form-control form-control-sm" name="email" value="${user.email}">`
      );
    $row
      .find(".td-phonenumber")
      .html(
        `<input type="text" class="form-control form-control-sm" name="phonenumber" value="${user.phonenumber}">`
      );
    $row
      .find(".td-address")
      .html(
        `<input type="text" class="form-control form-control-sm" name="address" value="${user.address}">`
      );

    // Change buttons
    $(this)
      .removeClass("btn-info btn-edit")
      .addClass("btn-success btn-save btn-primary")
      .text("Save");
    $row
      .find(".btn-delete")
      .removeClass("btn-danger btn-delete")
      .addClass("btn-secondary btn-cancel")
      .text("Cancel");
  });

  $(document).on("click", ".btn-save", function () {
    const $row = $(this).closest("tr");
    const iduser = $row.data("iduser");

    const updatedData = {
      fullName: $row.find('input[name="fullname"]').val(),
      email: $row.find('input[name="email"]').val(),
      phoneNumber: $row.find('input[name="phonenumber"]').val(),
      address: $row.find('input[name="address"]').val(),
    };

    updateUser(iduser, updatedData);

    // Restore text cells and buttons
    $row.find(".td-fullname").text(updatedData.fullname);
    $row.find(".td-email").text(updatedData.email);
    $row.find(".td-phonenumber").text(updatedData.phonenumber);
    $row.find(".td-address").text(updatedData.address);

    $(this)
      .removeClass("btn-success btn-save btn-primary")
      .addClass("btn-info btn-edit")
      .text("Edit");
    $row
      .find(".btn-cancel")
      .removeClass("btn-secondary btn-cancel")
      .addClass("btn-danger btn-delete")
      .text("Delete");
  });

  $(document).on("click", ".btn-cancel", function () {
    const $row = $(this).closest("tr");
    const iduser = $row.data("iduser");
    const user = users.get(iduser + "");

    // Restore text cells
    $row.find(".td-fullname").text(user.fullname);
    $row.find(".td-phonenumber").text(user.phonenumber);
    $row.find(".td-address").text(user.address);

    // Restore buttons
    $row
      .find(".btn-save")
      .removeClass("btn-success btn-save")
      .addClass("btn-info btn-edit")
      .text("Edit");
    $(this)
      .removeClass("btn-secondary btn-cancel")
      .addClass("btn-danger btn-delete")
      .text("Delete");
  });

  $(document).on("click", ".btn-delete", function () {
    const iduser = $(this).closest("tr").data("iduser");
    if (confirm("Are you sure you want to delete this user?")) {
      deleteUser(iduser);
    }
  });

  // Initialize
  await fetchUsers();
});
