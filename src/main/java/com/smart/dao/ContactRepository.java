package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact,Integer> {
//here we are going to use Pagination 
	@Query("from Contact as c where c.user.id =:userId")
	//pageable will contain two information 1.currentPage-page 2.Contact Per page 
	public Page<Contact> findContactByUser(@Param("userId")int userId,Pageable pePageable);
	
	//search 
	public List<Contact> findByNameContainingAndUser(String name,User user);
}
