package org.webserver.httpserver.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class UserRepository {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/webserver";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public UserRepository() {
    }


    // Kết nối đến MySQL
    private static Connection connect() throws Exception {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    // Hàm lưu tài khoản vào MySQL
    public static void saveUser(String username, String password) {
        try (Connection conn = connect()) {
            String query = "INSERT INTO user (username, password) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
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
