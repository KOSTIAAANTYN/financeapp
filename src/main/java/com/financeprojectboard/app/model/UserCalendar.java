package com.financeprojectboard.app.model;

import com.financeprojectboard.app.DTO.CalendarDayDTO;
import com.financeprojectboard.app.DTO.UserCalendarDTO;
import com.financeprojectboard.app.DTO.UserHistoryDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity(name = "user_calendar")
@Data
public class UserCalendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double globalTotal = 0;
    private double weekTotal = 0;
    private final int maxSize = 35;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "userCalendar")
    private List<CalendarDay> calendar = new ArrayList<>();


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User user;

    public UserCalendarDTO toDTO() {
        UserCalendarDTO dto = new UserCalendarDTO();
        dto.setId(this.user.getId());

        dto.setGlobalTotal(this.globalTotal);
        dto.setWeekTotal(this.weekTotal);


        dto.setEmail(this.user.getEmail());
        dto.setUsername(this.user.getUsername());
        dto.setPassword(this.user.getPassword());

        List<CalendarDayDTO> calendarDTOList = this.calendar.stream()
                .map(CalendarDay::toDTO)
                .collect(Collectors.toList());
        List<UserHistoryDTO> userHistoryDTOS = this.user.getUserHistory().stream()
                .map(UserHistory::toDTO)
                .collect(Collectors.toList());
        dto.setUserHistory(userHistoryDTOS);
        dto.setCalendar(calendarDTOList);

        return dto;
    }


    public void addDay(String date, String fullData) {
        CalendarDay calendarDay = new CalendarDay(date, fullData);
        calendarDay.setUserCalendar(this);
        calendar.add(calendarDay);
    }
//TODO commit this
    public void allTotal() {
        globalTotal = 0;
        weekTotal = 0;
        for (CalendarDay calendarDay : calendar) {
            calendarDay.TotalDay();
            globalTotal += calendarDay.getTotal();
        }
        for (int i = 0; i < 7; i++) weekTotal += calendar.get(28 + i).getTotal();
    }
//28
}
