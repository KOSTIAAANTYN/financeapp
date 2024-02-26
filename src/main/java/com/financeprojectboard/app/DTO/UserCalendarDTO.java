package com.financeprojectboard.app.DTO;

import lombok.Data;

import java.util.List;

@Data

public class UserCalendarDTO {
    private Long id;
    private double globalTotal;
    private double weekTotal;
    private Long userId;
    private String username;
    private String email;
    private String password;
    private List<CalendarDayDTO> calendar;
}
