package com.financeprojectboard.app.DTO;

import com.financeprojectboard.app.model.CalendarDay;
import com.financeprojectboard.app.model.User;
import com.financeprojectboard.app.model.UserCalendar;
import com.financeprojectboard.app.model.UserHistory;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data

public class UserCalendarDTO {
    private Long id;//userId

    private double globalTotal;
    private double weekTotal;

    private String username;
    private String email;


    private List<CalendarDayDTO> calendar;
    private List<UserHistoryDTO> userHistory;

}
