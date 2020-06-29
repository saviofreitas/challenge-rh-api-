package com.saviofreitas.challenge.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.saviofreitas.challenge.service.JwtUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private JWTUtil jwtUtil;

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	private List<String> excludeUrlPatterns = Arrays.asList("/", "/index.html", "/swagger-ui.css",
			"/swagger-ui.js", "/swagger-ui.js.map", "/swagger-ui-bundle.js",
			"/swagger-ui-bundle.js.map", "/swagger-ui-standalone-preset.js",
			"/swagger-ui-standalone-preset.js.map", "/swagger.json");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");

		String username = null;
		String jwtToken = null;

		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				username = jwtUtil.getUsernameFromToken(jwtToken);
			} catch (IllegalArgumentException e) {
				logger.error("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				logger.error("JWT Token has expired");
			}
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

			if (jwtUtil.isValidToken(jwtToken, userDetails)) {
				UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities());
				upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(upat);
			}
		}
		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return excludeUrlPatterns.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
	}
}
