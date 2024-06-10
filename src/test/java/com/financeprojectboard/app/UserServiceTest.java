package com.financeprojectboard.app;

import com.financeprojectboard.app.DTO.UserHistoryDTO;
import com.financeprojectboard.app.model.User;
import com.financeprojectboard.app.repositories.*;
import com.financeprojectboard.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    public void testAddToHistory() {
        UserHistoryDTO userHistoryDTO = new UserHistoryDTO();
        userHistoryDTO.setId(1L);
        userHistoryDTO.setDate("2023-06-10");
        userHistoryDTO.setType("test");
        userHistoryDTO.setTotal(100);

        User user = new User("username", "email@example.com", "password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.addToHistory(userHistoryDTO);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testIsExist() {
        String email = "email@example.com";
        when(userRepository.findByEmail(email)).thenReturn(new User());

        boolean result = userService.isExist(email);

        assertTrue(result);
    }

    @Test
    public void testEmailAuth() {
        String email = "email@example.com";

        String code = userService.emailAuth(email);

        assertNotNull(code);
        assertEquals(6, code.length());
    }

    // Додаткові тести для інших методів UserService
}

