package org.webserver.httpserver.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.webserver.http.HttpParser;
import org.webserver.http.HttpParsingException;
import org.webserver.http.HttpRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HandlerRequestTest {


    @Test
    public void test() throws HttpParsingException, IOException {
        HandlerRequest request = new HandlerRequest();
        HttpParser httpParser = new HttpParser();
        HttpRequest t = httpParser.parseHttpRequest(generateValidGETTestCase());
        request.handleGetRequest(t, OutputStream.nullOutputStream());
    }

    private InputStream generateValidGETTestCase() {

        String rawData = "GET /index.html HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "sec-ch-ua: \"Google Chrome\";v=\"129\", \"Not=A?Brand\";v=\"8\", \"Chromium\";v=\"129\"\r\n" +
                "sec-ch-ua-mobile: ?0\r\n" +
                "sec-ch-ua-platform: \"Windows\"\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Sec-Fetch-User: ?1\r\n" +
                "Sec-Fetch-Dest: document\r\n" +
                "Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                "Accept-Language: en,vi-VN;q=0.9,vi;q=0.8,fr-FR;q=0.7,fr;q=0.6,en-US;q=0.5\r\n" +
                "Cookie: ajs_anonymous_id=3045907f-3cd6-4648-b877-89cfd422ba9e; _streamlit_xsrf=2|7d14432b|07a5add7354388ddb643c4d81df6c4bd|1722589568; Idea-773c2672=bcb25554-8fe3-426b-8db7-5f6dae8beed8; fusra_user_info=7ba82341-1cbe-4481-8b89-1f4d88bf33d2/1/2024-09-15; fusra_session_id=1e388465-2ded-4d29-8e52-4d6359517899; .AspNetCore.Cookies=CfDJ8BOH88wseqlFoSRldP_NMc_mxYufLXsAQxUkagw6shjJwqOn0MjjG6vXyniqGfJB95k14BafiiNOYRiRW6dPBeBtGZXdYgfdqoLOgqlvaLgdoQYbgAeNNZFzQrSgkXNK-Xa2s8DfoN7eLwWo5TmqzpOB0KaeaWhU-U2Z_dTKbPcZcbF4-SepqsXEjR6r5FSjNKNUu9ovAxg0vPzSfJXA_FWUR1u8eSsXuEaN04ZCqUkwMIr7KdptxhnsyfSAarTimjr0sebzyvTzLgCKiU2miM5JumcWaRXpZJDpZ-S1ygDTV68dUFg9StIxJVvIoCwR4eohIPSXYhzKVAMM68NR1xTb_gt2yEXxaYycAn6l0q_qgk3kb-y9cZ3GXTVAGMXBZOIRgTfrdU0OgmiTYzq88n6KBIEkbTePHeOJ66KgUmXfbIW5NBTzXatd9wgBHrCz35erKgf1vyCK8vbaHX_gM5MrYaE8P8ushSDKVXdrkaXQiLYDQ8NxjAyig-Xatpp_0JLnh3my3s3deyJvX_G2oE98rRvuELUjDkPTEERElZcK_YSeQ4BpIpb-aK1ehqBthCe-hFiLU5k3eeMOUc7F7ClDB9RLesJlfHpE2hGnEpFzYVkPvUZc7USAHR-cRZXq94yz8YQD5VYwbU6GDObiV34WLBZ-zuo7vern-2LrICQ-; XAF_SizeMode=Medium; XAF_CurrentTheme=Purple\r\n" +
                "\r\n";

        System.out.println(rawData);

        InputStream is = new ByteArrayInputStream(
                rawData.getBytes(
                        StandardCharsets.UTF_8
                )
        );

        return is;
        }

}