package com.srm.coresecurity.security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.srm.coreframework.entity.UserEntity;


@Repository
public interface UserDAO extends CrudRepository<UserEntity, Integer> {
	
    UserEntity findByuserName(String userLoginId);
    
//    UserEntity findByUserNameOverride(String userLooginId);
    
  
    
    
    
}
