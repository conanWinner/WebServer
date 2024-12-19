package org.webserver.httpserver.repository;

import org.webserver.httpserver.entity.User;
import org.webserver.httpserver.entity.UserUpdate;
import org.webserver.httpserver.exception.ErrorCode;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public UserRepository() {
    }

    // Kết nối đến MySQL
    private static Connection connect() throws Exception {
        return DriverManager.getConnection(ConfigurationDB.DB_URL, ConfigurationDB.USER, ConfigurationDB.PASSWORD);
    }


    // Saving user
    public static boolean saveUser(String fullname, String password, String email, String phonenumber, String address) {
        try (Connection conn = connect()) {

            String query = "INSERT INTO user (fullName, password, email, phoneNumber, address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, fullname);
            pstmt.setString(2, password);
            pstmt.setString(3, email);
            pstmt.setString(4, phonenumber);
            pstmt.setString(5, address);

            pstmt.executeUpdate();
            return true;
        }catch (SQLIntegrityConstraintViolationException e){
            System.out.println("loi sign up");
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean existByIduser(String iduser){
        try (Connection conn = connect()) {
            String _id = "SELECT COUNT(*) FROM user WHERE iduser = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(_id)) {
                checkStmt.setString(1, iduser);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return true; // True nếu id đã tồn tại
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //Find user by id
    public static User findById(String id) throws Exception {
        try (Connection connection = connect()) {
            String query = "select * from user where id = ?";
            PreparedStatement ps = connection.prepareStatement(query);

            ps.setString(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String fullname = rs.getString("fullname");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String phonenumber = rs.getString("phonenumber");
                String address = rs.getString("address");

                return new User(fullname, password, email, phonenumber, address);

            }
        }

        return null;
    }

    // Check login
    public static User loginUser(String email, String password) throws Exception {
        try (Connection connection = connect()) {
//            String query = "select iduser, password from user where email = ?";
            String query = "SELECT * FROM user WHERE email = ? AND password = ?";
            PreparedStatement ps = connection.prepareStatement(query);

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String fullName = rs.getString("fullName");
                String emailR = rs.getString("email");
                String phoneNumber = rs.getString("phoneNumber");
                String address = rs.getString("address");

                return new User(fullName, emailR, phoneNumber, address);
            }
        }
        return null;
    }

    public static boolean updateUser(UserUpdate userUpdate, String iduser){
        String fullName = userUpdate.getFullName();
        String email = userUpdate.getEmail();
        String address = userUpdate.getAddress();
        String phoneNumber = userUpdate.getPhoneNumber();

        try (Connection conn = connect()) {


            String query = "UPDATE user SET  fullName=?, email=?, phoneNumber=?, address=? WHERE iduser=?";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, fullName);
            pstmt.setString(2, email);
            pstmt.setString(3, phoneNumber);
            pstmt.setString(4, address);
            pstmt.setString(5, iduser);

            return pstmt.executeUpdate() > 0;
        }catch (SQLIntegrityConstraintViolationException e){
            System.out.println("Error update");
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteUser(int iduser){
        try (Connection conn = connect()) {
            String sql = "DELETE FROM user WHERE iduser=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, iduser);

            return pstmt.executeUpdate() > 0;
        }catch (SQLIntegrityConstraintViolationException e){
            System.out.println("Error delete");
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hàm lấy danh sách tài khoản từ MySQL
    public static List<User> getAllUsers() {

        try (Connection conn = connect()) {

            List<User> users = new ArrayList<>();

            String query = "SELECT iduser,fullname,email,phonenumber,address FROM user";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String iduser = rs.getString("iduser");
                String fullname = rs.getString("fullname");
                String email = rs.getString("email");
                String address = rs.getString("address");
                String phonenumber = rs.getString("phonenumber");

                users.add(new User(iduser,fullname,email,phonenumber,address));

            }

            return users;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //    ======================= Saving email ======================
//    public static void saveEmailMessage(String email, String title, String content, String emailreceivedfrom) {
//        try (Connection conn = connect()) {
//            String query = "INSERT INTO formemail (email, emailtitle, emailcontent, emailreceivedfrom) VALUES (?, ?, ?, ?)";
//            PreparedStatement pstmt = conn.prepareStatement(query);
//
//            pstmt.setString(1, email);
//            pstmt.setString(2, title);
//            pstmt.setString(3, content);
//            pstmt.setString(4, emailreceivedfrom);
//
//            pstmt.executeUpdate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    //get ALL email in DB
//    public static List<FormEmail> getEmailMessage(String emailPersonal) {
//        try (Connection conn = connect()) {
//            List<FormEmail> ls = new ArrayList<>();
//
//            String query = """
//                           select user.email, formemail.emailtitle, formemail.emailcontent, formemail.emailreceivedfrom
//                           from user
//                           join formemail on user.email = formemail.email
//                           where user.email = ?
//                           """
//                    ;
//            PreparedStatement pstmt = conn.prepareStatement(query);
//            pstmt.setString(1, emailPersonal);
//
//            ResultSet rs = pstmt.executeQuery();
//
//            while (rs.next()) {
//                String _email = rs.getString("email");
//                String _title = rs.getString("emailtitle");
//                String _content = rs.getString("emailcontent");
//                String _receivedfrom = rs.getString("emailreceivedfrom");
//
//                ls.add(new FormEmail(_email, _title, _content, _receivedfrom));
//            }
//
//            return ls;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


}
