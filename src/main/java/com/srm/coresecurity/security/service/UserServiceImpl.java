package com.srm.coresecurity.security.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.srm.coreframework.entity.UserEntity;
import com.srm.coreframework.response.CustomUserDetails;
import com.srm.coresecurity.security.repository.UserDAO;


@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService {

	@Autowired
	private UserDAO userDao;

	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		UserEntity user = userDao.findByuserName(userId);
		
		
		CustomUserDetails customUserDetails = new CustomUserDetails(user.getUserName());
		customUserDetails.setAuthentication(true);
		customUserDetails.setPassword(user.getPassword());
		if(user.getUserName()!=null) {
			customUserDetails.setUserId(user.getId());
		}
		customUserDetails.setFirstName(user.getFirstName());
		customUserDetails.setLastName(user.getLastName());
		customUserDetails.setEntityId(user.getEntityLicenseEntity().getId());
		customUserDetails.setRoleId(user.getUserRoleEntity().getId());
		customUserDetails.setLangCode(user.getLangCode());
		return customUserDetails;
	}

	
	public List<UserEntity> findAll() {
		List<UserEntity> list = new ArrayList<>();
		userDao.findAll().iterator().forEachRemaining(list::add);
		return list;
	}

}
