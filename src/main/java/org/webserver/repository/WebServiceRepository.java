package org.webserver.repository;

import org.webserver.entity.WebService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WebServiceRepository {
    private static Connection connect() throws Exception {
        return DriverManager.getConnection(ConfigurationDB.DB_URL, ConfigurationDB.USER, ConfigurationDB.PASSWORD);
    }


    // Hàm lấy danh sách tài khoản từ MySQL
    public static List<WebService> loadWebService() {

        try (Connection conn = connect()) {

            List<WebService> webServices = new ArrayList<>();

            String query = "SELECT * FROM webservice";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String serviceName = rs.getString("serviceName");
                String status = rs.getString("status");
                String IPHost = rs.getString("IPHost");
                String port = rs.getString("port");
                String subDomain = rs.getString("subDomain");
                String username = rs.getString("username");

                webServices.add(new WebService(serviceName, status, IPHost, port, subDomain, username));

            }

            return webServices;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
