package com.financeprojectboard.app.repositories;

import com.financeprojectboard.app.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
