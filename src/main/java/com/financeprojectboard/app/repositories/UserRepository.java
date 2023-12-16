package com.financeprojectboard.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.financeprojectboard.app.model.User;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User,Long>{

    @Query("SELECT h FROM User h WHERE h.email = ?1")
    User findByEmail(String email);
}
