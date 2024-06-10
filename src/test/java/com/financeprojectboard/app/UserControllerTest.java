package com.financeprojectboard.app;

import com.financeprojectboard.app.DTO.UserCalendarDTO;
import com.financeprojectboard.app.DTO.UserHistoryDTO;
import com.financeprojectboard.app.controller.UserController;
import com.financeprojectboard.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testUpdateCalendar() throws Exception {
        UserCalendarDTO userCalendarDTO = new UserCalendarDTO();
        userCalendarDTO.setId(1L);
        userCalendarDTO.setEmail("email@example.com");

        when(userService.updateCalendar(any(UserCalendarDTO.class))).thenReturn(userCalendarDTO);

        mockMvc.perform(post("/updateCalendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"email\":\"email@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("email@example.com"));

        verify(userService, times(1)).updateCalendar(any(UserCalendarDTO.class));
    }

    @Test
    public void testAddToHistory() throws Exception {
        doNothing().when(userService).addToHistory(any(UserHistoryDTO.class));

        mockMvc.perform(post("/addToHistory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"date\":\"2023-06-10\",\"type\":\"test\",\"total\":100}"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));

        verify(userService, times(1)).addToHistory(any(UserHistoryDTO.class));
    }
}


