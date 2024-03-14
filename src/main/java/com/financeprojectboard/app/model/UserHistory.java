package com.financeprojectboard.app.model;

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

    private String date;

    private String type;

    private int total;


    public UserHistoryDTO toDTO() {
        UserHistoryDTO dto = new UserHistoryDTO();

        dto.setId(this.user.getId());

        dto.setTotal(this.total);
        dto.setDate(this.date);
        dto.setType(this.type);


        return dto;
    }
}
