package org.webserver.repository;

import org.webserver.dto.reponse.WebServiceResponse;
import org.webserver.entity.WebService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WebServiceRepository {
    private static Connection connect() throws Exception {
        return DriverManager.getConnection(ConfigurationDB.DB_URL, ConfigurationDB.USER, ConfigurationDB.PASSWORD);
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
}
