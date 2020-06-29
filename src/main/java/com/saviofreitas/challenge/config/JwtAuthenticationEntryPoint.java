package com.saviofreitas.challenge.config;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<String> excludeUrlPatterns = Arrays.asList(
			"/", 
			"/index.html", 
			"/swagger-ui.css", 
			"/swagger-ui.js", 
			"/swagger-ui.js.map", 
			"/swagger-ui-bundle.js",
			"/swagger-ui-bundle.js.map",
			"/swagger-ui-standalone-preset.js",
			"/swagger-ui-standalone-preset.js.map", 
			"/swagger.json");

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
//		if(!request.getServletPath().contains("docs/")) {
//		}
		if(!excludeUrlPatterns.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()))) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");						
		}
	}

}
