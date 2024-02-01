package com.financeprojectboard.app.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class CalendarDay {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String date;
    private String fullData;
    private double total;

    @JoinColumn
    @ManyToOne
    private UserCalendar userCalendar;

    @OneToMany(cascade =CascadeType.ALL,fetch = FetchType.EAGER,mappedBy = "calendarDay")
    private List<Message> messages =new ArrayList<>();



    public void TotalDay(){
        if (!messages.isEmpty()){
            for (Message message : messages) {
                if (message.isIncome()) {
                    total += message.getPrice();
                } else {
                    total -= message.getPrice();
                }
            }}
    }
}
