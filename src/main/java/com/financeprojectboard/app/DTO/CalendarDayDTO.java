package com.financeprojectboard.app.DTO;

import com.financeprojectboard.app.model.CalendarDay;
import com.financeprojectboard.app.model.Message;
import com.financeprojectboard.app.model.UserCalendar;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CalendarDayDTO {
    private Long id;
    private String date;
    private String fullDate;
    private double total;
    private Long calendarId;
    private List<MessageDTO> messages;

    public CalendarDay toEntity(UserCalendar userCalendar) {
        CalendarDay calendarDay = new CalendarDay();
        calendarDay.setId(this.id);
        calendarDay.setDate(this.date);
        calendarDay.setFullDate(this.fullDate);
        calendarDay.setTotal(this.total);

        calendarDay.setUserCalendar(userCalendar);

        if (this.messages != null) {
            List<Message> messageEntities = this.messages.stream()
                    .map(messageDTO -> messageDTO.toEntity(calendarDay))
                    .collect(Collectors.toList());
            calendarDay.setMessages(messageEntities);
        }

        return calendarDay;
    }
}
