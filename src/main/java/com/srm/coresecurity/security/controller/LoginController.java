/**
 * 
 */
package com.srm.coresecurity.security.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.srm.coreframework.i18n.CoreMessageSource;
import com.srm.coreframework.service.CommonService;
import com.srm.coreframework.util.CommonUtil;
import com.srm.coresecurity.security.domain.Login;
import com.srm.coresecurity.security.domain.LoginResponse;

/**
 *
 */
@RestController
public class LoginController {

	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private CoreMessageSource messageSource;

	@Autowired
	private CommonService commonService;
	
	@PostMapping("/core/oauth/token")
	public ResponseEntity<LoginResponse> login(@RequestHeader("auth") String auth, @RequestBody Login login,
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, String> parameters = new LinkedHashMap<String, String>();
		ResponseEntity<OAuth2AccessToken> res = null;

		parameters.put("username", login.getUsername());
		parameters.put("password", CommonUtil.decode(login.getPassword()));
		parameters.put("grant_type", login.getGrantType());

		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<String, String>();
		multiValueMap.setAll(parameters);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", auth);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		LoginResponse loginResponse=new LoginResponse();
		
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(multiValueMap,
				headers);

		String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath() + "/oauth/token";
		try {
			res = restTemplate.exchange(url, HttpMethod.POST, entity, OAuth2AccessToken.class);
		} catch (HttpClientErrorException ex) {
			ex.printStackTrace();
			LOG.info("Inside HttpClientErrorException");
			if (ex.getRawStatusCode() == 400) {
				loginResponse.setStatus(ex.getStatusCode().toString());
				loginResponse.setMessage(messageSource.getMessage("1005"));
				return new ResponseEntity<>(loginResponse,ex.getStatusCode());
				//throw new HttpClientErrorException(ex.getStatusCode(), messageSource.getMessage("1005"));
			} else if (ex.getRawStatusCode() == 401) {
				
				loginResponse.setStatus(ex.getStatusCode().toString());
				loginResponse.setMessage(messageSource.getMessage("1005"));
				return new ResponseEntity<>(loginResponse,ex.getStatusCode());
				//throw new HttpClientErrorException(ex.getStatusCode(), messageSource.getMessage("1006"));
			} else {
				loginResponse.setStatus(ex.getStatusCode().toString());
				loginResponse.setMessage(messageSource.getMessage("1007"));
				return new ResponseEntity<>(loginResponse,ex.getStatusCode());
				//throw new HttpClientErrorException(ex.getStatusCode(), messageSource.getMessage("1007"));
			}
		}
		
		String accessToken = res.getBody().getValue();

		commonService.updateAccessToken(accessToken, login.getUsername());

		loginResponse.setoAuth2AccessToken(res.getBody());
		loginResponse.setStatus(res.getStatusCode().toString());
		loginResponse.setMessage("success");
		return new ResponseEntity<>(loginResponse,res.getStatusCode());
	}

}
