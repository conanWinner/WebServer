package org.webserver.util;

import org.webserver.httpserver.core.io.WebRootHandler;
import org.webserver.httpserver.core.io.WebRootNotFoundException;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    public static boolean exist(String fileName) {

        try {
            WebRootHandler webRootHandler = new WebRootHandler(fileName);
        } catch (WebRootNotFoundException e) {
            return false;
        }
        return true;
    }

    public static String probeContentType(String fileName) {
        final String[] tokens = fileName.split("\\.");
        final String extension = tokens[tokens.length - 1].toLowerCase();

        return switch (extension) {
            case "html" -> "text/html";
            case "txt" -> "text/plain";
            case "css" -> "text/css";
            case "gif" -> "image/gif";
            case "jpg", "jpeg" -> "image/jpeg";
            case "js" -> "application/javascript";
            case "json" -> "application/json";
            case "mp4" -> "video/mp4";
            case "png" -> "image/png";
            case "woff" -> "font/woff";
            case "woff2" -> "font/woff2";
            case "ttf" -> "font/ttf";
            case "eot" -> "application/vnd.ms-fontobject";
            case "svg" -> "image/svg+xml";
            default -> "application/octet-stream";  // MIME mặc định cho các file không xác định
        };
    }


    public static InputStream getInputStream(String fileName) {
        try {
            WebRootHandler webRootHandler = new WebRootHandler(fileName);
        } catch (WebRootNotFoundException e) {
        }

        try {
            // Đọc nội dung file HTML thành chuỗi
            String htmlContent = readHtmlFile(fileName);

            // Chuyển chuỗi HTML thành InputStream
            InputStream data = new ByteArrayInputStream(
                    htmlContent.getBytes(StandardCharsets.UTF_8)
            );

            // Kiểm tra xem InputStream có được tạo thành công không
            System.out.println("InputStream created: " + (data != null));

            return data;


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    // Phương thức đọc file HTML thành chuỗi
    private static String readHtmlFile(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        }
        return contentBuilder.toString();
    }

//    public static void saveFile(String fileName, String base64) {
//        final String filePath = Constants.FILES_CANONICAL_PATH + "uploaded/" + fileName;
//        final File physicalFile = new File(filePath);
//
//        try {
//            physicalFile.getParentFile().mkdirs();
//            physicalFile.createNewFile();
//            try (OutputStream stream = new FileOutputStream(physicalFile)) {
//                if (base64.contains(",")) {
//                    base64 = base64.split(",")[1];
//                }
//
//                stream.write(Base64.getMimeDecoder().decode(base64));
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }
}
