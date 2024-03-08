package com.financeprojectboard.app.model;

import com.financeprojectboard.app.DTO.MessageDTO;
import com.financeprojectboard.app.DTO.UserHistoryDTO;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "user_history")
public class UserHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private User user;

    private String startDate;
    private String endDate;

    private Boolean isMonth;

    private double globalTotal;
    private double weekTotal;


    public UserHistoryDTO toDTO() {
        UserHistoryDTO dto = new UserHistoryDTO();

        dto.setId(this.id);
        dto.setGlobalTotal(this.globalTotal);

        dto.setWeekTotal(this.weekTotal);
        dto.setStartDate(this.startDate);

        dto.setEndDate(this.endDate);
        dto.setIsMonth(this.isMonth);


        return dto;
    }
}
