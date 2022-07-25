package com.smart.dao;

import com.smart.entities.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

    //pagination
    @Query("from Contact as c where c.user.id=:userId")
    //contactPage - page
    //contacts per page -5
    public Page<Contact> findContactsByUserId(@Param("userId") int userId, Pageable pageable);

    @Query("select c from Contact c where c.cId=:cId")
    public Contact getContactByCId(@Param("cId") int cId);
}
