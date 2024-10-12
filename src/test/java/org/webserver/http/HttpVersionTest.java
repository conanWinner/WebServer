package org.webserver.http;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import static org.junit.jupiter.api.Assertions.*;


public class HttpVersionTest {

    @Test
    void BestVersionMatches() throws BadHttpVersionException {

        HttpVersion httpVersion = HttpVersion.getBestCompatibleVersion("HTTP/1.1");

        assertEquals(HttpVersion.HTTP_1_1, httpVersion);

        Logger logger = org.slf4j.LoggerFactory.getLogger(HttpVersionTest.class);

        logger.info(String.valueOf(httpVersion));


    }

    @Test
    void BadVersionMatches() {

        HttpVersion httpVersion = null;
        try {
            httpVersion = HttpVersion.getBestCompatibleVersion("http/1.1");
            fail();
        } catch (BadHttpVersionException e) {
        }

    }

    @Test
    void BadVersionMatchesHigherVersion() throws BadHttpVersionException {

        HttpVersion httpVersion = HttpVersion.getBestCompatibleVersion("HTTP/1.2");

        assertNotNull(httpVersion);

        Logger logger = org.slf4j.LoggerFactory.getLogger(HttpVersionTest.class);

        assertEquals(HttpVersion.HTTP_1_1, httpVersion);

    }

}
