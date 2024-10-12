package org.webserver.httpserver.core.io;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.engine.Constants;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WebRootHandlerTest {

    @BeforeAll
    public void beforeAll() {

    }

    @Test
    void constructorGoodPath() {
        try {
//            String t = Constants.class.getResource();
            WebRootHandler webRootHandler = new WebRootHandler("src/main/java/org/webserver/common");
        } catch (WebRootNotFoundException e) {
            fail(e);
        }
    }

}
