package com.financeprojectboard.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.financeprojectboard.app.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User,Long>{
    User findByEmail(String email);
}
