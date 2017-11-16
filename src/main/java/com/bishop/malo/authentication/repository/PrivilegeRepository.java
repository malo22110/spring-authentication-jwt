package com.bishop.malo.authentication.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bishop.malo.authentication.model.entity.Privilege;



@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Serializable> {
	public Privilege findByName(String name);
}
