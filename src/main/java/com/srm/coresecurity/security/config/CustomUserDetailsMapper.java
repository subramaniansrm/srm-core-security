package com.srm.coresecurity.security.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.stereotype.Component;

import com.srm.coreframework.response.CustomUserDetails;
import com.srm.coresecurity.security.repository.UserDAO;


@Component
public class CustomUserDetailsMapper extends LdapUserDetailsMapper {

	@Autowired
	private UserDAO userDao;

	@Override
	public CustomUserDetails mapUserFromContext(DirContextOperations dirContextOperations, String userId,
			Collection<? extends GrantedAuthority> collection) {
		List<Object[]> userList =new ArrayList<>();
		/*
		List<Object[]> userList = userDao.findUserByUserName(userId);
		if (userList.isEmpty()) {
			throw new UsernameNotFoundException("Invalid User.");
		}*/
		CustomUserDetails customUserDetails = new CustomUserDetails();
		for (Object[] objects : userList) {
			customUserDetails.setUserId(((Integer) objects[0]));
			customUserDetails.setUserLoginId((String) objects[1]);
		}
		
		/*Long roleId = userDao.getSuperAdminId(customUserDetails.getUserId());
		
		if(null != roleId && roleId == 1) {
			customUserDetails.setSuperAdmin(true);
		}else {
			customUserDetails.setSuperAdmin(false);
		}*/
		
		return customUserDetails;
	}
}