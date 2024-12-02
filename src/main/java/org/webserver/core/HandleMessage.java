package org.webserver.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.javac.Main;
import org.webserver.dto.ApiConstructor;
import org.webserver.dto.request.ActiveRequest;
import org.webserver.dto.response.WebServiceResponse;
import org.webserver.dto.request.CreateWebServiceRequest;
import org.webserver.dto.request.LoginRequest;
import org.webserver.dto.request.WebServiceRequest;
import org.webserver.entity.User;
import org.webserver.repository.UserRepository;
import org.webserver.repository.WebServiceRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class HandleMessage {
    private JsonNode messageNode;
    private OutputStream clientOs;
    private InputStream clientIs;
    private ObjectMapper mapper;

    public HandleMessage(JsonNode messageNode, OutputStream clientOs, InputStream clientIs) {
        this.messageNode = messageNode;
        this.clientOs = clientOs;
        this.clientIs = clientIs;
        this.mapper = new ObjectMapper();
    }

    public void handleLogin() {
        try {
            LoginRequest loginRequest = mapper.treeToValue(messageNode, LoginRequest.class);

            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();
            User user = UserRepository.loginUser(username, password);

            ApiConstructor<String> api;
            String jsonResponse = "";
            if (user != null) {
                api = new ApiConstructor<>("login", "Success");
                jsonResponse = mapper.writeValueAsString(api);
            } else {
                api = new ApiConstructor<>("login", "Login failed");
                jsonResponse = mapper.writeValueAsString(api);
            }
            clientOs.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
            clientOs.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleGetAllWebServices() {
        try {
            WebServiceRequest webServiceRequest = mapper.treeToValue(messageNode, WebServiceRequest.class);
            String username = webServiceRequest.getUsername();

            ApiConstructor<List<WebServiceResponse>> api = new ApiConstructor<>();
            api.setAction("get all webservices");
            api.setMessage(WebServiceRepository.getAllWebServices(username));

            String jsonResponse = mapper.writeValueAsString(api);
            clientOs.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
            clientOs.flush();
            System.out.println("json webservice: " + jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCreateWebService() {
        try {
            CreateWebServiceRequest createWebServiceRequest = mapper.treeToValue(messageNode, CreateWebServiceRequest.class);

            String serviceName = createWebServiceRequest.getServiceName();
            String port = createWebServiceRequest.getPort();
            String subDomain = createWebServiceRequest.getSubDomain();
            String username = createWebServiceRequest.getUsername();

            boolean stop = false;
            DataInputStream in = new DataInputStream(clientIs);
            while (stop == false){
                long fileSize = in.readLong();
                byte[] fileData = new byte[(int) fileSize];
                in.readFully(fileData);

                // Đảm bảo thư mục nơi lưu tệp đã tồn tại, nếu chưa thì tạo mới
                File directory = new File("src/main/resources/jar");
                if (!directory.exists()) {
                    directory.mkdirs();  // Tạo thư mục nếu chưa tồn tại
                }

                // Tạo đối tượng File cho tệp đích
                File file1 = new File(directory, subDomain + ".jar");

                // Ghi dữ liệu vào tệp
                try (FileOutputStream fos = new FileOutputStream(file1)) {
                    fos.write(fileData);  // Ghi dữ liệu từ mảng byte vào tệp
                }

                System.out.println("File đã được lưu thành công: " + file1.getAbsolutePath());
                break;
            }


            boolean check = WebServiceRepository.createWebService(serviceName, port, subDomain, username);

            ApiConstructor<String> api;
            String jsonResponse = "";
            if (check) {
                api = new ApiConstructor<>("create webservice", "Success");
                jsonResponse = mapper.writeValueAsString(api);
            } else {
                api = new ApiConstructor<>("create webservice", "Failed");
                jsonResponse = mapper.writeValueAsString(api);
            }
            clientOs.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
            clientOs.flush();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleActive() {
        try {
            ActiveRequest activeRequest = mapper.treeToValue(messageNode, ActiveRequest.class);

            String subDomain = activeRequest.getSubDomain();


            boolean checkCallFile = callFileJarOfWebService(subDomain);
            ApiConstructor<String> api;
            String jsonResponse = "";
            if (checkCallFile) {
                WebServiceRepository.activeWebService(subDomain);
                api = new ApiConstructor<>("active webservice", "Success");
                jsonResponse = mapper.writeValueAsString(api);
            } else {
                api = new ApiConstructor<>("active webservice", "Failed");
                jsonResponse = mapper.writeValueAsString(api);
            }
            clientOs.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
            clientOs.flush();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean callFileJarOfWebService(String subDomain) throws IOException, InterruptedException {
        // Đọc file JAR từ resources
        InputStream jarStream = Main.class.getClassLoader().getResourceAsStream("jar/" + subDomain + ".jar");
        if (jarStream == null) {
            System.out.println("File JAR không tồn tại trong resources.");
            return false;
        }

        // Tạo file tạm thời để lưu JAR
        File tempJarFile = File.createTempFile(subDomain, ".jar");
        tempJarFile.deleteOnExit(); // Xóa file sau khi ứng dụng kết thúc

        // Ghi nội dung của file JAR từ resources ra file tạm thời
        // Sử dụng Files.copy để sao chép file JAR từ InputStream vào file tạm
        Files.copy(jarStream, tempJarFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        // Tạo ProcessBuilder để chạy file JAR từ file tạm thời
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", tempJarFile.getAbsolutePath());
        processBuilder.inheritIO(); // Để hiển thị output của quá trình chạy JAR lên console

        // Bắt đầu quá trình chạy file JAR
        Process process = processBuilder.start();
        System.out.println("Đang chạy server cho user1 từ file JAR trong resources.");

        // Xử lý khi process kết thúc
        process.waitFor();
        return true;
    }

//    public void receiveFileInChunks(int totalParts, InputStream clientIn, String outputFilePath) throws IOException {
//        // Mảng byte để lưu dữ liệu từng phần
//        Map<Integer, byte[]> chunks = new TreeMap<>(); // Sử dụng TreeMap để tự động sắp xếp các chunk theo index
//        boolean stop = false;
//
//
//        StringBuilder fullJsonRequest = new StringBuilder();
//        byte[] buffer = new byte[1024 * 3];
//        int bytesRead;
//        while (stop == false) {
//            bytesRead = clientIn.read(buffer);
//            String data = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
//            fullJsonRequest.append(data);
//
//            String[] requests = fullJsonRequest.toString().split("\n");
//            for (int i = 0; i < requests.length - 1; i++) {
////                    System.out.println("json: " + requests[i]);
//                //           JSON => Api
//                JsonNode rootNode = mapper.readTree(requests[i]);
//                String action = rootNode.get("action").asText();
//                JsonNode messageNode = rootNode.get("message");
//
//                if (Objects.equals(action, "file content")) {
//                    FileContentRequest fileContentRequest = mapper.treeToValue(messageNode, FileContentRequest.class);
//                    int partIndex = fileContentRequest.getPart();
//                    String chunkBase64 = fileContentRequest.getFileContent();
//
//                    // Giải mã Base64
//                    byte[] chunk = Base64.getDecoder().decode(chunkBase64);
//
//                    // Lưu chunk vào Map
//                    chunks.put(partIndex, chunk);
//                    System.out.println("chunk size: " + chunks.size());
//                    // Lấy tập hợp các khóa
//                    Set<Integer> keys = chunks.keySet();
//
//                    System.out.println("chunk keys: " + keys.size());
//                    System.out.println("part index: " + partIndex);
//
//                }
//            }
//
////                Phần coòn lại
//            fullJsonRequest = new StringBuilder(requests[requests.length - 1]);
//            if (fullJsonRequest.toString().contains("End Of File")) {
//                stop = true;
//            }
//
//        }
//
//        System.out.println("total size: " + totalParts);
//        // Tạo file và ghi dữ liệu vào file
//        saveFileFromChunks(chunks, outputFilePath);
//        System.out.println("File received successfully and saved to " + outputFilePath);
//
//    }

//    private void saveFileFromChunks(Map<Integer, byte[]> chunks, String outputFilePath) throws IOException {
//        // Mở file đầu ra để ghi
//
//        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFilePath))) {
//            // Duyệt qua các phần tử trong TreeMap theo thứ tự tăng dần của khóa (Integer)
//            for (Map.Entry<Integer, byte[]> entry : chunks.entrySet()) {
//                byte[] chunkData = entry.getValue();
//                // Ghi từng phần dữ liệu vào file
//                bos.write(chunkData);
//            }
//        }
//    }


}
