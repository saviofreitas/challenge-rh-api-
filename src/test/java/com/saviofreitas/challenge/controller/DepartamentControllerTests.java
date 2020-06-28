package com.saviofreitas.challenge.controller;

import static io.restassured.RestAssured.given;

import java.util.Arrays;

import org.assertj.core.api.SoftAssertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.saviofreitas.challenge.model.Departament;
import com.saviofreitas.challenge.repository.DepartamentRepository;
import com.saviofreitas.challenge.security.AccountCredentials;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@TestPropertySource("file:src/test/resources/application.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DepartamentControllerTests extends AbstractTestNGSpringContextTests {

	private static final String BASE_URL = "/api/departaments";
	
	@Autowired
	private DepartamentRepository departamentRepository;

	private RequestSpecification specification;

	private Departament departament;
	
	@LocalServerPort
	private int port;

	@BeforeClass
	public void authorization() {
		AccountCredentials credentials = new AccountCredentials();
		credentials.setUsername("ibyte");
		credentials.setPassword("password");

		String token = given().basePath("/api/authenticate").port(port)
				.contentType("application/json").body(credentials)
				.when().post().then()
				.statusCode(200).extract().body().jsonPath().getString("token");

		specification = new RequestSpecBuilder().addHeader("Authorization", "Bearer " + token)
				.setBasePath(BASE_URL).setPort(port)
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	@BeforeMethod
	public void insertDepartament() {
		departament = new Departament("Secretaria");
		given().spec(specification).contentType("application/json")
		.body(departament).when().post().then().statusCode(201);
	}
	
	@AfterMethod
	public void cleanUp() {
	    departamentRepository.deleteAll();
	}
	
	private Departament retrieveDepartament() {
	    Departament retrievedDepartament =
	        Arrays.stream(given().spec(specification)
	                    .when().get()
	                    .then().statusCode(200)
	                    .extract().as(Departament[].class))
	            .reduce((first, second) -> second)
	            .orElse(null);

	    return retrievedDepartament;
	}

	/**
	 * GET /departaments
	 */
	@Test
	public void findAll() {
		Departament departament = retrieveDepartament();
		Departament[] expected = { departament };
		
		Departament[] actual = given().spec(specification)
                					.when().get()
            						.then().statusCode(200)
            						.extract().as(Departament[].class);
		
		SoftAssertions assertions = new SoftAssertions();
	    assertions.assertThat(actual).isEqualTo(expected);
	    assertions.assertAll();
	}
	
	/**
	 * Without token<br/>
	 * GET /departaments
	 */
	@Test
	public void unauthorizedWhenFindAllWithoutToken() {
		given().basePath(BASE_URL).port(port).when().get().then().statusCode(401);
	}

	/**
	 * POST /departaments
	 */
	@Test
	public void create() {
		Departament retrievedDepartament = retrieveDepartament();
	    assertDepartament(retrievedDepartament, departament);
	}

	/**
	 * PUT /departaments/{id}
	 */
	@Test
	public void update() {
		String updatedDescription = "Descrição atualizada";
	    Departament updatedDepartament = new Departament(updatedDescription);

	    Departament retrievedDepartament = retrieveDepartament();

	    given()
	        .spec(specification)
	        .contentType("application/json")
	        .when()
	        .body(updatedDepartament)
	        .put(String.format("%s", retrievedDepartament.getId()))
	        .then()
	        .statusCode(200);

	    retrievedDepartament = retrieveDepartament();

	    assertDepartament(retrievedDepartament, updatedDepartament);
	}

	/**
	 * DELETE /departaments/{id}
	 */
	@Test
	public void destroy() {
		Departament retrieved = retrieveDepartament();

	    given().spec(specification).when()
        	.delete(String.format("%s", retrieved.getId()))
	        .then().statusCode(200);

	    retrieved = retrieveDepartament();

	    SoftAssertions assertions = new SoftAssertions();
	    assertions.assertThat(retrieved).isNull();
	    assertions.assertAll();
	}
	
	private void assertDepartament(Departament actual, Departament expected) {
	    SoftAssertions assertions = new SoftAssertions();
	    assertions.assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
	    assertions.assertThat(actual.getId()).isGreaterThan(0);
	    assertions.assertAll();
	}
}
