package com.financeprojectboard.app.DTO;

import com.financeprojectboard.app.model.CalendarDay;
import com.financeprojectboard.app.model.User;
import com.financeprojectboard.app.model.UserCalendar;
import com.financeprojectboard.app.model.UserHistory;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserHistoryDTO {
    private Long id;
    private String startDate;
    private String endDate;

    private Boolean isMonth;

    private double globalTotal;
    private double weekTotal;
}
