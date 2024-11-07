package org.webserver.repository;

import org.webserver.entity.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserRepository {
    private static Connection connect() throws Exception {
        return DriverManager.getConnection(ConfigurationDB.DB_URL, ConfigurationDB.USER, ConfigurationDB.PASSWORD);
    }

    // Check login
    public static User loginUser(String username, String password) throws Exception {
        try (Connection connection = connect()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement ps = connection.prepareStatement(query);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String usernameR = rs.getString("username");
                String passwordR = rs.getString("password");
                return new User(usernameR, passwordR);
            }
        }
        return null;
    }
}
