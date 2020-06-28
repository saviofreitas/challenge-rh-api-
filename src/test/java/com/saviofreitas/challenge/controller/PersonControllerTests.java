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
import com.saviofreitas.challenge.model.Person;
import com.saviofreitas.challenge.repository.DepartamentRepository;
import com.saviofreitas.challenge.repository.PersonRepository;
import com.saviofreitas.challenge.security.AccountCredentials;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@TestPropertySource("file:src/test/resources/application.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonControllerTests extends AbstractTestNGSpringContextTests {
	
	private static final String BASE_URL = "/api/people";
	
	@Autowired
	private DepartamentRepository departamentRepository;
	
	@Autowired
	private PersonRepository personRepository;

	private RequestSpecification specification;
	
	private Person person;
	
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
		Departament departament = departamentRepository.saveAndFlush(new Departament("Secretaria"));
		
		person = new Person("Virgulino", "Ferreira", "virgulino@lampiao.com", departament);
		
		given().spec(specification).contentType("application/json")
		.body(person).when().post().then().statusCode(201);
	}
	
	@AfterMethod
	public void cleanUp() {
	    personRepository.deleteAll();
	    departamentRepository.deleteAll();
	}
	
	/**
	 * Without token<br/>
	 * GET /people
	 */
	@Test
	public void unauthorizedWhenFindAllWithoutToken() {
		given().basePath(BASE_URL).port(port).when().get().then().statusCode(401);
	}
	
	/**
	 * GET /people
	 */
	@Test
	public void findAll() {
		Person person = retrievePerson();
		Person[] expected = { person };
		
		Person[] actual = given().spec(specification)
                					.when().get()
            						.then().statusCode(200)
            						.extract().as(Person[].class);
		
		SoftAssertions assertions = new SoftAssertions();
	    assertions.assertThat(actual).isEqualTo(expected);
	    assertions.assertAll();
	}
	
	/**
	 * GET /people/departament/{departamentId}
	 */
	@Test
	public void findByDepartament() {
		Departament manager = departamentRepository.saveAndFlush(new Departament("Diretoria"));
		Person p1 = personRepository.saveAndFlush(new Person("P1", "P1", "p1@email.com", manager));
		Person p2 = personRepository.saveAndFlush(new Person("P2", "P2", "p2@email.com", manager));
		Person p3 = personRepository.saveAndFlush(new Person("P3", "P3", "p3@email.com", manager));
		
		Person[] expectedPeopleManager = { p1, p2, p3 };
		
		Departament financial = departamentRepository.saveAndFlush(new Departament("Financeiro"));
		Person p4 = personRepository.saveAndFlush(new Person("P4", "P4", "p4@email.com", financial));
		Person p5 = personRepository.saveAndFlush(new Person("P4", "P5", "p5@email.com", financial));
		
		Person[] expectedPeopleFinancial = { p4, p5 };
		
		Person[] actualManager = given().spec(specification).basePath(BASE_URL + "/departament/" + manager.getId())
                					.when().get()
            						.then().statusCode(200)
            						.extract().as(Person[].class);
		
		Person[] actualFinancial = given().spec(specification).basePath(BASE_URL + "/departament/" + financial.getId())
				.when().get()
				.then().statusCode(200)
				.extract().as(Person[].class);
		
		SoftAssertions assertions = new SoftAssertions();
	    assertions.assertThat(actualManager).isEqualTo(expectedPeopleManager);
	    assertions.assertThat(actualFinancial).isEqualTo(expectedPeopleFinancial);
	    assertions.assertThat(actualFinancial).isNotEqualTo(actualManager);
	    assertions.assertAll();
	}

	/**
	 * POST /people
	 */
	@Test
	public void create() {
		Person retrievedPerson = retrievePerson();
	    assertPerson(retrievedPerson, person);
	}
	
	/**
	 * PUT /people/{id}
	 */
	@Test
	public void update() {
		Departament manager = departamentRepository.saveAndFlush(new Departament("Diretoria"));
	    Person updatedPerson = new Person("Virgulino", "Ferreira", "virgulino@lampiao.com", manager);
	    updatedPerson.setEmail("outro@email.com");
	    
	    Person retrievedPerson = retrievePerson();

	    given().spec(specification).contentType("application/json")
	        .when().body(updatedPerson).put(String.format("%s", retrievedPerson.getId()))
	        .then().statusCode(200);

	    retrievedPerson = retrievePerson();

	    assertPerson(retrievedPerson, updatedPerson);
	}
	
	/**
	 * DELETE /people/{id}
	 * @throws Exception
	 */
	@Test
	public void destroy() throws Exception {
		Person retrieved = retrievePerson();

	    given().spec(specification).when()
        	.delete(String.format("%s", retrieved.getId()))
	        .then().statusCode(200);

	    retrieved = retrievePerson();

	    SoftAssertions assertions = new SoftAssertions();
	    assertions.assertThat(retrieved).isNull();
	    assertions.assertAll();
	}
	
	private Person retrievePerson() {
	    Person retrievedPerson = Arrays.stream(given().spec(specification)
	    										.when().get().then().statusCode(200)
	    										.extract().as(Person[].class))
	            						.reduce((first, second) -> second)
	            						.orElse(null);
	    return retrievedPerson;
	  }
	
	private void assertPerson(Person actual, Person expected) {
	    SoftAssertions assertions = new SoftAssertions();
	    assertions.assertThat(actual.getFullName()).isEqualTo(expected.getFullName());
	    assertions.assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
	    assertions.assertThat(actual.getDepartament()).isEqualTo(expected.getDepartament());
	    assertions.assertThat(actual.getId()).isGreaterThan(0);
	    assertions.assertAll();
	}
	
}
