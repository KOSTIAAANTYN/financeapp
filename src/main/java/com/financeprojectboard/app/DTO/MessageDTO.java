package com.financeprojectboard.app.DTO;

import com.financeprojectboard.app.model.CalendarDay;
import com.financeprojectboard.app.model.Message;
import lombok.*;

@Data

public class MessageDTO {
    private Long id;
    private Long dayId;
    private boolean isIncome;
    private String description;
    private double price;

    public boolean getIsIncome(){
        return isIncome;
    }

    public Message toEntity(CalendarDay calendarDay) {
        Message message = new Message();
        message.setId(this.id);
        message.setIncome(this.isIncome);
        message.setDescription(this.description);
        message.setPrice(this.price);

        message.setCalendarDay(calendarDay);

        return message;
    }
}
