package com.financeprojectboard.app.service;

import com.financeprojectboard.app.model.TempUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TempUserService {

    private final RedisTemplate<String, TempUser> redisTemplate;

    @Autowired
    public TempUserService(RedisTemplate<String, TempUser> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveTempUser(String email,String username,String password) {
        TempUser tempUser = new TempUser();
        tempUser.setEmail(email);
        tempUser.setUsername(username);
        tempUser.setPasswordHash(password);
        redisTemplate.opsForValue().set(email,tempUser, Duration.ofMinutes(15));
    }

    public TempUser getTempUser(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    public void deleteTempUser(String email) {
        redisTemplate.delete(email);
    }
}

