package org.webserver.httpserver.repository;

import org.webserver.httpserver.entity.User;
import org.webserver.httpserver.entity.UserUpdate;
import org.webserver.httpserver.repository.config.ConfigurationDB;

import java.sql.*;

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

    public static boolean existByEmail(String email){
        try (Connection conn = connect()) {
            String checkEmailQuery = "SELECT COUNT(*) FROM user WHERE email = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkEmailQuery)) {
                checkStmt.setString(1, email);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return true; // True nếu email đã tồn tại
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

    public static boolean updateUser(UserUpdate userUpdate, String email){
        String fullName = userUpdate.getFullName();
        String newPassword = userUpdate.getNewPassword();
        String address = userUpdate.getAddress();
        String phoneNumber = userUpdate.getPhoneNumber();

        try (Connection conn = connect()) {


            String query = "UPDATE user SET  fullName=?, password=?, phoneNumber=?, address=? WHERE email=?";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, fullName);
            pstmt.setString(2, newPassword);
            pstmt.setString(3, phoneNumber);
            pstmt.setString(4, address);
            pstmt.setString(5, email);

            return pstmt.executeUpdate() > 0;
        }catch (SQLIntegrityConstraintViolationException e){
            System.out.println("loi update");
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteUser(String email){
        try (Connection conn = connect()) {
            String sql = "DELETE FROM user WHERE email=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);

            return pstmt.executeUpdate() > 0;
        }catch (SQLIntegrityConstraintViolationException e){
            System.out.println("loi delete");
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hàm lấy danh sách tài khoản từ MySQL
//    public static List<User> loadUsers() {
//
//        try (Connection conn = connect()) {
//
//            List<User> users = new ArrayList<>();
//
//            String query = "SELECT email,ip,jointime FROM user";
//            PreparedStatement pstmt = conn.prepareStatement(query);
//            ResultSet rs = pstmt.executeQuery();
//
//            while (rs.next()) {
//                String email = rs.getString("email");
//                String ip = rs.getString("ip");
//                String jointime = rs.getString("jointime");
//
//                users.add(new User(email, jointime, ip));
//
//            }
//
//            return users;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

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
