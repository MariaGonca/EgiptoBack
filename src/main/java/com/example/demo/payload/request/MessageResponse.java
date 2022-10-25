package com.example.demo.payload.request;

// Imprime el token 
public class MessageResponse {
	
	private String message;
	
	public MessageResponse(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
