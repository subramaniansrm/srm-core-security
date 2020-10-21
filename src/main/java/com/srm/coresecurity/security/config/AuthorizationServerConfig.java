package com.srm.coresecurity.security.config;

import java.util.Arrays;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.srm.coreframework.config.CustomAccessTokenEnhancer;
import com.srm.coreframework.exception.CustomOauthException;

import lombok.Data;




/**
 */


@Data
@Configuration
@PropertySource(value = { "classpath:configuration.properties" })
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationServerConfig.class);
	static final int ACCESS_TOKEN_VALIDITY_SECONDS = 60 * 60 * 12; // default 12 hours 60 * 60 * 12.
	static final int REFRESH_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 30; // default 30 days.

	@Value("${security.jwt.client-id}")
	private String clientId;

	@Value("${security.jwt.client-secret}")
	private String clientSecret;

	@Value("${security.jwt.grant-type}")
	private String grantType;

	@Value("${security.jwt.scope-read}")
	private String scopeRead;

	@Value("${security.jwt.scope-write}")
	private String scopeWrite = "write";

	@Value("${security.jwt.trust}")
	private String trust;

	@Value("${security.signing-key}")
	private String signingKey;

	@Value("${security.encoding-strength}")
	private Integer encodingStrength;

	@Value("${security.jwt.refresh-token}")
	private String refreshToken;

	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Resource(name = "userService")
	private UserDetailsService userDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(encodingStrength);
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		accessTokenConverter.setSigningKey(signingKey);
		return accessTokenConverter;
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	@Primary
	public DefaultTokenServices tokenServices() {
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore());
		defaultTokenServices.setSupportRefreshToken(true);
		defaultTokenServices.setAuthenticationManager(authenticationManager);
		return defaultTokenServices;
	}

	@Bean
	public TokenEnhancer tokenEnhancer() {
		return new CustomAccessTokenEnhancer();
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
		//With refresh token
		/*configurer.inMemory().withClient(clientId).secret(clientSecret).authorizedGrantTypes(grantType, refreshToken)
				.scopes(scopeRead, scopeWrite, trust).accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS)
				.refreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS);*/
		//without refresh token
		configurer.inMemory().withClient(clientId).secret(clientSecret).authorizedGrantTypes(grantType)
					.scopes(scopeRead, scopeWrite, trust).accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
		enhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
		endpoints.tokenStore(tokenStore).accessTokenConverter(accessTokenConverter()).tokenEnhancer(enhancerChain)
				.authenticationManager(authenticationManager).exceptionTranslator(e -> {
					if (e instanceof OAuth2Exception) {
						// e.printStackTrace();
						LOG.info("OAuth2Exception ---> " + e.getMessage());
						OAuth2Exception oAuth2Exception = (OAuth2Exception) e;
						return ResponseEntity.status(oAuth2Exception.getHttpErrorCode())
								.body(new CustomOauthException(oAuth2Exception.getMessage()));
					} else {
						throw e;
					}
				});

		endpoints.userDetailsService(userDetailsService);
	}

}