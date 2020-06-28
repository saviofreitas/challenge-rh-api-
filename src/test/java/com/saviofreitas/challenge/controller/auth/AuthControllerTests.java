package com.saviofreitas.challenge.controller.auth;

import static com.saviofreitas.challenge.util.TestUtil.mapToJson;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.saviofreitas.challenge.config.JwtAuthenticationEntryPoint;
import com.saviofreitas.challenge.security.AccountCredentials;
import com.saviofreitas.challenge.service.JwtUserDetailsService;

@WebMvcTest(AuthController.class)
@ActiveProfiles(profiles = "test")
public class AuthControllerTests {

	private static final String BASE_URL = "/authenticate";

	@Autowired
	private MockMvc mockMvc;

//	@Autowired
//	private JWTUtil jwtUtil;
	
	@MockBean
	private JwtUserDetailsService userDetailsService;
	
	@MockBean
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	
	@Test
	public void authenticate() throws Exception {

		AccountCredentials credentials = new AccountCredentials();
		credentials.setUsername("ibyte");
		credentials.setPassword("password");
		
		String json = mapToJson(credentials);

		mockMvc.perform(post(BASE_URL)
				.content(json)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8"))
				.andExpect(status().isOk());
		
		verify(userDetailsService, times(1)).loadUserByUsername("ibyte");
	}
	
	@Test
	public void authenticatinFailed() throws Exception {

		AccountCredentials credentials = new AccountCredentials();
		credentials.setUsername("folgado");
		credentials.setPassword("password");
		
		String json = mapToJson(credentials);

		mockMvc.perform(post(BASE_URL)
				.content(json)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8"))
				.andExpect(status().is(401));
		
		verify(userDetailsService, times(0)).loadUserByUsername("folgado");
	}
	



	
}
