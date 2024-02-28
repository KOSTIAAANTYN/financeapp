package com.financeprojectboard.app.repositories;

import com.financeprojectboard.app.model.UserCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCalendarRepository extends JpaRepository<UserCalendar,Long> {
}
