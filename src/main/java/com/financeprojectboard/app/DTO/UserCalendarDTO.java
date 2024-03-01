package com.financeprojectboard.app.DTO;

import com.financeprojectboard.app.model.CalendarDay;
import com.financeprojectboard.app.model.User;
import com.financeprojectboard.app.model.UserCalendar;
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
    private String password;

    private List<CalendarDayDTO> calendar;



    public UserCalendar toEntity(User user) {
        UserCalendar userCalendar = new UserCalendar();
        userCalendar.setGlobalTotal(this.globalTotal);
        userCalendar.setWeekTotal(this.weekTotal);

        userCalendar.setUser(user);

        if (this.calendar != null) {
            List<CalendarDay> calendarDays = this.calendar.stream()
                    .map(calendarDayDTO -> calendarDayDTO.toEntity(userCalendar))
                    .collect(Collectors.toList());
            userCalendar.setCalendar(calendarDays);
            userCalendar.allTotal();
        }

        return userCalendar;
    }

}
