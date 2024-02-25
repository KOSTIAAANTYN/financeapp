package com.financeprojectboard.app.DTO;

import lombok.Data;

import java.util.List;

@Data
public class MessageRequestDTO {
    private Long userId;
    private Long dayId;
    private List<MessageDTO> messages;
}
