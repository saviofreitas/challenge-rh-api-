package com.saviofreitas.challenge;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.saviofreitas.challenge.controller.DepartamentController;
import com.saviofreitas.challenge.controller.PersonController;

@SpringBootTest
class RestApplicationTests {

	@Autowired
	private DepartamentController departamentController;

	@Autowired
	private PersonController personController;

	@Test
	void contextLoads() {
		assertThat(departamentController).isNotNull();
		assertThat(personController).isNotNull();
	}

}
