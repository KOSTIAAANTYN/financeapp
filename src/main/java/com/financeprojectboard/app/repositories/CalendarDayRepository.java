package com.financeprojectboard.app.repositories;

import com.financeprojectboard.app.model.CalendarDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarDayRepository extends JpaRepository<CalendarDay, Long> {
}
