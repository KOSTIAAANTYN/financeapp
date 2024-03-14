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
    private Long id;//userId

    private String date;

    private String type;

    private int total;

    public UserHistory toEntity(User user) {
        UserHistory userHistory = new UserHistory();

        userHistory.setDate(this.date);
        userHistory.setType(this.type);
        userHistory.setTotal(this.total);

        userHistory.setUser(user);

        return userHistory;
    }
}
