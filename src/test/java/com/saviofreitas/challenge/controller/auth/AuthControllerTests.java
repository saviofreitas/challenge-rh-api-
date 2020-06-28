package com.saviofreitas.challenge.controller.auth;

import static io.restassured.RestAssured.given;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.saviofreitas.challenge.security.AccountCredentials;

@TestPropertySource("file:src/test/resources/application.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTests extends AbstractTestNGSpringContextTests {

	private static final String BASE_URL = "/api/authenticate";
	
	@LocalServerPort
	private int port;

	@Test
	public void authenticate() throws Exception {
		AccountCredentials credentials = new AccountCredentials();
		credentials.setUsername("ibyte");
		credentials.setPassword("password");

		given().basePath(BASE_URL).port(port).contentType("application/json")
			.body(credentials).when().post()
			.then().statusCode(200);
	}
	
	@Test
	public void authenticatinFailed() {
		AccountCredentials credentials = new AccountCredentials();
		credentials.setUsername("folgado");
		credentials.setPassword("123456");

		given().basePath(BASE_URL).port(port).contentType("application/json")
			.body(credentials).when().post()
			.then().statusCode(401);
	}
}
