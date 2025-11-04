package com.financeprojectboard.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempUser implements Serializable {
    private String email;
    private String username;
    private String passwordHash;

}

