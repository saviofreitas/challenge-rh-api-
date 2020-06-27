package com.saviofreitas.challenge.controller;

import static com.saviofreitas.challenge.util.TestUtil.mapToJson;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.saviofreitas.challenge.model.Departament;
import com.saviofreitas.challenge.model.Person;
import com.saviofreitas.challenge.repository.PersonRepository;

@WebMvcTest(PersonController.class)
public class PersonControllerTests {
	
	private static final String BASE_URL = "/people";

	@Autowired
    private MockMvc mockMvc;
	
	@MockBean
	private PersonRepository personRepository;

	@Test
	public void findAll() throws Exception {		
		Person person = getPerson();
		person.setId(1L);
		
		List<Person> people = Arrays.asList(person);
		
		given(personRepository.findAll()).willReturn(people);
		
		this.mockMvc.perform(get(BASE_URL))
				.andExpect(status().isOk())
				.andExpect(content().json(people.toString()));
	}
	
	@Test
	public void create() throws Exception {
		Person person = getPerson();
		String json = mapToJson(person);

		mockMvc.perform(post(BASE_URL)
						.content(json)
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
						.characterEncoding("utf-8"))
				.andExpect(status().isCreated());

		verify(personRepository, times(1)).save(person);
	}

	@Test
	public void findById() throws Exception {
		Person person = getPerson();
		person.setId(1L);
		given(personRepository.findById(1L)).willReturn(Optional.of(person));
		this.mockMvc.perform(get(BASE_URL + "/1")).andExpect(status().isOk())
				.andExpect(content().json(person.toString()));
	}
	
	@Test
	public void findByDepartamentId() throws Exception {
		Person p1 = getPerson();
		p1.setId(1L);
		
		Person p2 = getPerson();
		p2.setId(2L);
		p2.setEmail("p2@email.com");
		
		Person p3 = getPerson();
		p3.setId(3L);
		p3.setEmail("p3@email.com");
		
		List<Person> peopleByDepartament = Arrays.asList(p1, p2, p3);
		
		given(personRepository.findByDepartamentId(1L)).willReturn(peopleByDepartament);
		this.mockMvc.perform(get(BASE_URL + "/departament/1"))
		.andExpect(status().isOk())
		.andExpect(content().json(peopleByDepartament.toString()));
		
		List<Person> emptyList = new ArrayList<Person>();
		this.mockMvc.perform(get(BASE_URL + "/departament/2"))
		.andExpect(status().isOk())
		.andExpect(content().json(emptyList.toString()));
	}

	@Test
	public void update() throws Exception {
		Person person = getPerson();
		person.setId(1L);
		given(personRepository.findById(1L)).willReturn(Optional.of(person));

		Person personUpdate = person;
		personUpdate.setEmail("other@email.com");
		String jsonUpdate = mapToJson(personUpdate);

		this.mockMvc.perform(put(BASE_URL + "/1")
							.content(jsonUpdate)
							.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
							.characterEncoding("utf-8"))
					.andExpect(status().isOk());
		
		verify(personRepository, times(1)).save(personUpdate);
		
		this.mockMvc.perform(put(BASE_URL + "/2")
					.content(jsonUpdate)
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
					.characterEncoding("utf-8"))
			.andExpect(status().is(404));
	}
	
	@Test
	public void destroy() throws Exception {
		Person person = getPerson();
		person.setId(1L);
		given(personRepository.findById(1L)).willReturn(Optional.of(person));

		this.mockMvc.perform(delete(BASE_URL + "/1"))
					.andExpect(status().isOk());
		
		verify(personRepository, times(1)).deleteById(1L);
		
		this.mockMvc.perform(put(BASE_URL + "/2"))
					.andExpect(status().is(404));
	}
	
	private Person getPerson() {
		Person person = new Person();
		person.setFirstName("John");
		person.setLastName("Doe");
		person.setEmail("johndoe@email.com");
		
		Departament departament = new Departament(1L, "Secretaria");
		person.setDepartament(departament);
		
		return person;
	}
}
