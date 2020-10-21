package com.srm.coresecurity.security.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 */
@Configuration
@EnableWebSecurity
@Order(1)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${security.signing-key}")
	private String signingKey;

	@Value("${security.encoding-strength}")
	private Integer encodingStrength;

	@Value("${security.security-realm}")
	private String securityRealm;

	@Resource(name = "userService")
	private UserDetailsService userDetailsService;

	@Autowired
	CustomUserDetailsMapper userDetailsMapper;

	@Value("${ldap.urls}")
	private String ldapUrls;
	
	@Value("${ldap.domain}")
	private String ldapDomain;

	@Bean
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Bean
    CorsFilter corsFilter() {
        CorsFilter filter = new CorsFilter();
        return filter;
    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().httpBasic()
				.realmName(securityRealm).and().csrf().disable();
		
		//http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues());
		
		/*http
        .addFilterBefore(corsFilter(), SessionManagementFilter.class) //adds your custom CorsFilter
        
        .csrf().disable()
        .anonymous().disable()
       
        .authorizeRequests()
       .antMatchers("/authentication").permitAll()
        
        .antMatchers("/oauth/token").permitAll();*/

	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder(encodingStrength));
	} 

	// LDAP 
	//if you are using LDAP Configuration comment above configure method. 
	//Only one configure AuthenticationManagerBuilder Method we can use
	/*@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		ActiveDirectoryLdapAuthenticationProvider  activeDirectoryLdapAuthenticationProvider=activeDirectoryLdapAuthenticationProvider();//.setUserDetailsContextMapper(userDetailsMapper)
		activeDirectoryLdapAuthenticationProvider.setUserDetailsContextMapper(userDetailsMapper);
		auth.authenticationProvider(activeDirectoryLdapAuthenticationProvider);

	}

	@Bean
	public ActiveDirectoryLdapAuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
		ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider(ldapDomain,
				ldapUrls);
		return provider;
	}*/
	
	

}