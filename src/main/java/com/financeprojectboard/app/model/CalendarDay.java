package com.financeprojectboard.app.model;

import com.financeprojectboard.app.DTO.CalendarDayDTO;
import com.financeprojectboard.app.DTO.MessageDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity(name = "calendar_day")
public class CalendarDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String date;
    private String fullDate;
    private double total;

    @JoinColumn
    @ManyToOne
    private UserCalendar userCalendar;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "calendarDay")
    private List<Message> messages = new ArrayList<>();

    public CalendarDay(String date, String fullDate) {
        this.date = date;
        this.fullDate = fullDate;
        this.messages = new ArrayList<>();
    }


    public CalendarDay() {

    }

    public CalendarDayDTO toDTO() {
        CalendarDayDTO dto = new CalendarDayDTO();
        dto.setId(this.id);
        dto.setDate(this.date);
        dto.setFullDate(this.fullDate);
        dto.setTotal(this.total);
        dto.setCalendarId(this.userCalendar.getId());

        List<MessageDTO> messageDTOList = this.messages.stream()
                .map(Message::toDTO)
                .collect(Collectors.toList());

        dto.setMessages(messageDTOList);

        return dto;
    }


    public void TotalDay() {
        if (!messages.isEmpty()) {
            for (Message message : messages) {
                if (message.isIncome()) {
                    total += message.getPrice();
                } else {
                    total -= message.getPrice();
                }
            }
        }
    }

}
