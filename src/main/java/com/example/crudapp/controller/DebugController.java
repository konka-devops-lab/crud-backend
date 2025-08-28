package com.example.crudapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/debug")
public class DebugController {
    
    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @GetMapping("/redis")
    public ResponseEntity<?> debugRedis() {
        try {
            Set<String> keys = redisTemplate.keys("*");
            List<Map<String, Object>> entries = new ArrayList<>();
            
            if (keys != null) {
                for (String key : keys) {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("key", key);
                    entry.put("type", "string"); // Simplified for this example
                    entry.put("value", redisTemplate.opsForValue().get(key));
                    entries.add(entry);
                }
            }
            
            return ResponseEntity.ok(entries);
            
        } catch (Exception e) {
            logger.error("Redis debug failed", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Redis debug failed");
            error.put("details", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}