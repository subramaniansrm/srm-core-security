package com.srm.coresecurity.security.repository;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.srm.coreframework.entity.UserEntity;

@Repository
public class CustomUserDAO {
	
	@PersistenceContext
	private EntityManager manager;
	
	public UserEntity findByNameOverride(String userLoginId) {
	
		String buf = "FROM UserEntity c WHERE c.deleteFlag  = '0' and c.activeFlag = '1' and c.userName = '" + userLoginId + "'";
		
		javax.persistence.Query query = manager.createQuery(buf.toString());
//		CommonUtil.setQueryParam(query);

		UserEntity resultList = null;
		try {
			resultList = (UserEntity) query.getSingleResult();	
		}catch(Exception e) {
			
			e.printStackTrace();
		}
		
		
		return resultList;
		
	}

}
