package com.financeprojectboard.app.DTO;

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
}
