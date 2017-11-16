package com.bishop.malo.authentication.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bishop.malo.authentication.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Serializable> {
	public User findByUsername(String username);
	public User findByEmail(String email);
}
