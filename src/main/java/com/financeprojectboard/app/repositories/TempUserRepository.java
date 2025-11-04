package com.financeprojectboard.app.repositories;

import com.financeprojectboard.app.model.TempUser;
import org.springframework.data.repository.CrudRepository;

public interface TempUserRepository extends CrudRepository<TempUser, String> {
}

