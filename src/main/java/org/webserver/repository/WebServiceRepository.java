package org.webserver.repository;

import org.webserver.dto.reponse.WebServiceResponse;
import org.webserver.entity.WebService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WebServiceRepository {
    private static Connection connect()  {
        try{
            return DriverManager.getConnection(ConfigurationDB.DB_URL, ConfigurationDB.USER, ConfigurationDB.PASSWORD);
        }catch ( Exception e){
            e.printStackTrace();
            return null;
        }
    }


    // Hàm lấy danh sách tài khoản từ MySQL
    public static List<WebServiceResponse> getAllWebServices(String username) throws Exception {
        try (Connection connection = connect()) {
            String query = "SELECT * FROM webservices WHERE username = ?";
            PreparedStatement ps = connection.prepareStatement(query);

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                List<WebServiceResponse> webServiceResponses = new ArrayList<>();

                // Duyệt qua các kết quả trả về từ cơ sở dữ liệu
                while (rs.next()) {
                    WebServiceResponse webServiceResponse = new WebServiceResponse();
                    webServiceResponse.setServiceName(rs.getString("serviceName"));
                    webServiceResponse.setStatus(rs.getString("status"));
                    webServiceResponse.setIPHost(rs.getString("IPHost"));
                    webServiceResponse.setPort(rs.getInt("port"));
                    webServiceResponse.setSubDomain(rs.getString("subDomain"));
                    webServiceResponse.setUsername(rs.getString("username"));

                    webServiceResponses.add(webServiceResponse);
                }

                // Trả về danh sách các web service
                return webServiceResponses;
            }
        }
    }

    public static boolean createWebService(String serviceName, String port, String subDomain, String username){
        try (Connection connection = connect()) {
            String query = "INSERT INTO webservices (serviceName, status, IPHost, port, subDomain, username) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(query);

            ps.setString(1, serviceName);
            ps.setString(2, "Stopped");
            ps.setString(3, "30.172.168.9");
            ps.setString(4, port);
            ps.setString(5, subDomain);
            ps.setString(6, username);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean existBySimilarInformation(String serviceName, String subDomain, String port){
        try (Connection connection = connect()) {
            String query = "SELECT * FROM webservices WHERE serviceName = ? OR subDomain = ? OR port = ?";
            PreparedStatement ps = connection.prepareStatement(query);

            ps.setString(1, serviceName);
            ps.setString(2, subDomain);
            ps.setString(3, port);

            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
