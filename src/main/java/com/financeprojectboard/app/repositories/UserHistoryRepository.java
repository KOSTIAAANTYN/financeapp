package com.financeprojectboard.app.repositories;

import com.financeprojectboard.app.model.User;
import com.financeprojectboard.app.model.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHistoryRepository extends JpaRepository<UserHistory,Long> {
    void deleteUserHistoriesByUserId(Long userId);
}
