package com.srm.coresecurity.security.domain;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

public class LoginResponse {
	
	private String status;
	
	private String message;
	
	private OAuth2AccessToken oAuth2AccessToken;
	
	

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the oAuth2AccessToken
	 */
	public OAuth2AccessToken getoAuth2AccessToken() {
		return oAuth2AccessToken;
	}

	/**
	 * @param oAuth2AccessToken the oAuth2AccessToken to set
	 */
	public void setoAuth2AccessToken(OAuth2AccessToken oAuth2AccessToken) {
		this.oAuth2AccessToken = oAuth2AccessToken;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

} 
