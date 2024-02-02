package com.financeprojectboard.app.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private boolean isIncome;
    private String description;
    private double price;

    @JoinColumn
    @ManyToOne
    private CalendarDay calendarDay;

    public Message(boolean isIncome, String description, double price) {
        this.isIncome = isIncome;
        this.description = description;
        this.price = price;
    }

    public Message() {

    }
}
