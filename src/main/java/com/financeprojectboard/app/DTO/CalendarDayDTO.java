package com.financeprojectboard.app.DTO;

import lombok.Data;

import java.util.List;

@Data
public class CalendarDayDTO {
    private Long id;
    private int date;
    private String fullDate;
    private double total;
    private Long calendarId;
    private List<MessageDTO> messages;
}
