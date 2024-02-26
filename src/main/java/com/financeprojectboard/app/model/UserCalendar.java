package com.financeprojectboard.app.model;

import com.financeprojectboard.app.DTO.CalendarDayDTO;
import com.financeprojectboard.app.DTO.UserCalendarDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
public class UserCalendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double globalTotal = 0;
    private double weekTotal = 0;
    private final int maxSize = 35;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "userCalendar")
    private List<CalendarDay> calendar = new ArrayList<>();


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")//idk
    private User user;


    public UserCalendarDTO toDTO() {
        UserCalendarDTO dto = new UserCalendarDTO();
        dto.setId(this.id);
        dto.setGlobalTotal(this.globalTotal);
        dto.setWeekTotal(this.weekTotal);
        dto.setUserId(this.user.getId());
        dto.setEmail(this.user.getEmail());
        dto.setUsername(this.user.getUsername());
        dto.setPassword(this.user.getPassword());

        List<CalendarDayDTO> calendarDTOList = this.calendar.stream()
                .map(CalendarDay::toDTO)
                .collect(Collectors.toList());

        dto.setCalendar(calendarDTOList);

        return dto;
    }


    public void addDay(String date, String fullData) {
        CalendarDay calendarDay = new CalendarDay(date, fullData);
        if (calendar.size() >= maxSize) {
            calendar.remove(0);
        }
        calendarDay.setUserCalendar(this);
        calendar.add(calendarDay);

    }

    public void AllTotal() {
        for (CalendarDay calendarDay : calendar) {
            calendarDay.TotalDay();
            globalTotal += calendarDay.getTotal();
        }
        for (int i = 0; i < 7; i++) weekTotal += calendar.get(28 + i).getTotal();
    }
//28
}
