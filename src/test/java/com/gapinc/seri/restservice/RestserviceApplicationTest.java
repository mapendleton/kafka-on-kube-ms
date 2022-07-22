package com.gapinc.seri.restservice;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.gapinc.seri.restservice.service.SessionHandler;

@SpringBootTest
class RestserviceApplicationTest {

    @Autowired
    private SessionHandler handler;

	@Test
	void contextLoads() {
        assertNotNull(handler);
	}

}
