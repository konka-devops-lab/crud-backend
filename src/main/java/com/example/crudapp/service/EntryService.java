package com.example.crudapp.service;

import com.example.crudapp.model.Entry;
import com.example.crudapp.repository.EntryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class EntryService {
    
    private static final Logger logger = LoggerFactory.getLogger(EntryService.class);
    private static final String CACHE_KEY = "all_entries";
    private static final int CACHE_TTL = 60; // seconds
    
    @Autowired
    private EntryRepository entryRepository;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public List<Entry> getAllEntries() {
        try {
            // Try to get from cache first
            String cachedData = redisTemplate.opsForValue().get(CACHE_KEY);
            
            if (cachedData != null) {
                logger.info("Serving from Redis cache");
                return objectMapper.readValue(cachedData, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Entry.class));
            } else {
                logger.info("Cache miss: No cache found, fetching from database");
            }
            
            // Fetch from database
            List<Entry> entries = entryRepository.findAll();
            
            // Cache the result
            logger.info("Serving from Database and caching the result");
            String jsonData = objectMapper.writeValueAsString(entries);
            redisTemplate.opsForValue().set(CACHE_KEY, jsonData, CACHE_TTL, TimeUnit.SECONDS);
            
            return entries;
            
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON for cache", e);
            // Fallback to database only
            return entryRepository.findAll();
        } catch (Exception e) {
            logger.error("Redis Fetch Error", e);
            // Fallback to database only
            return entryRepository.findAll();
        }
    }
    
    public Entry createEntry(Entry entry) {
        Entry savedEntry = entryRepository.save(entry);
        logger.info("Inserted entry with ID: {}", savedEntry.getId());
        
        // Clear the cache because data changed
        clearCache();
        
        return savedEntry;
    }
    
    public boolean deleteEntry(Long id) {
        Optional<Entry> entry = entryRepository.findById(id);
        
        if (entry.isPresent()) {
            entryRepository.deleteById(id);
            logger.info("Deleted entry with ID: {}", id);
            
            // Clear the cache because data changed
            clearCache();
            
            return true;
        }
        
        return false;
    }
    
    private void clearCache() {
        try {
            redisTemplate.delete(CACHE_KEY);
            logger.info("Cache cleared for {}", CACHE_KEY);
        } catch (Exception e) {
            logger.error("Error clearing cache", e);
        }
    }
}