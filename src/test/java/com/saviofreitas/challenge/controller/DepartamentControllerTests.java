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
import com.saviofreitas.challenge.repository.DepartamentRepository;

@WebMvcTest(DepartamentController.class)
public class DepartamentControllerTests {

	private static final String BASE_URL = "/departaments";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DepartamentRepository departamentRepository;

	@Test
	public void findAll() throws Exception {

		Departament departament = new Departament(1L, "Secretaria");

		List<Departament> departaments = Arrays.asList(departament);

		given(departamentRepository.findAll()).willReturn(departaments);

		this.mockMvc.perform(get(BASE_URL)).andExpect(status().isOk())
				.andExpect(content().json(departaments.toString()));
	}

	@Test
	public void create() throws Exception {

		Departament departament = new Departament();
		departament.setDescription("Secretaria");
		String json = mapToJson(departament);

		mockMvc.perform(post(BASE_URL)
						.content(json)
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
						.characterEncoding("utf-8"))
				.andExpect(status().isCreated());

		verify(departamentRepository, times(1)).save(departament);
	}

	@Test
	public void findById() throws Exception {
		Departament secretaria = new Departament(1L, "Secretaria");
		given(departamentRepository.findById(1L)).willReturn(Optional.of(secretaria));
		this.mockMvc.perform(get(BASE_URL + "/1"))
					.andExpect(status().isOk())
					.andExpect(content().json(secretaria.toString()));
	}

	@Test
	public void update() throws Exception {
		Departament secretaria = new Departament(1L, "Secretaria");
		given(departamentRepository.findById(1L)).willReturn(Optional.of(secretaria));

		Departament manager = new Departament(1l, "Gerência");
		String jsonUpdate = mapToJson(manager);

		this.mockMvc.perform(put(BASE_URL + "/1")
							.content(jsonUpdate)
							.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
							.characterEncoding("utf-8"))
					.andExpect(status().isOk());
		
		verify(departamentRepository, times(1)).save(manager);
	}
	
	@Test
	public void destroy() throws Exception {
		Departament secretaria = new Departament(1L, "Secretaria");
		given(departamentRepository.findById(1L)).willReturn(Optional.of(secretaria));

		this.mockMvc.perform(delete(BASE_URL + "/1"))
					.andExpect(status().isOk());
		
		verify(departamentRepository, times(1)).deleteById(1L);
	}
}
