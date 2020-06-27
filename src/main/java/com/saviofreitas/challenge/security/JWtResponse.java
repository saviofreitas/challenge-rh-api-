package com.saviofreitas.challenge.security;

import java.io.Serializable;

public class JWtResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String token;

	public JWtResponse(String token) {
		this.token = token;
	}

	public String getToken() {
		return this.token;
	}
}
