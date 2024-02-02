package com.financeprojectboard.app.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String username;
    String email;
    String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserCalendar userCalendar;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userCalendar = new UserCalendar();
        this.userCalendar.setUser(this);

        DateTimeFormatter dt = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        LocalDate localDate = LocalDate.now();
        LocalDate currentDateMinus = LocalDate.now().minusDays(35);

        int j = 0;
        while (!currentDateMinus.plusDays(j).equals(localDate)) {
            j++;
            userCalendar.addDay(currentDateMinus.plusDays(j).format(dt), currentDateMinus.plusDays(j).format(dtf));
        }
    }

    public User() {
    }
}
