package com.example.crudapp.controller;

import com.example.crudapp.model.Entry;
import com.example.crudapp.service.EntryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EntryController {
    
    private static final Logger logger = LoggerFactory.getLogger(EntryController.class);
    
    @Autowired
    private EntryService entryService;
    
    @GetMapping("/entries")
    public ResponseEntity<List<Entry>> getAllEntries() {
        try {
            List<Entry> entries = entryService.getAllEntries();
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            logger.error("Error fetching entries", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/entries")
    public ResponseEntity<?> createEntry(@Valid @RequestBody Entry entry) {
        try {
            if (entry.getAmount() == null || entry.getDescription() == null || entry.getDescription().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Amount and description are required");
                return ResponseEntity.badRequest().body(error);
            }
            
            Entry savedEntry = entryService.createEntry(entry);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEntry);
            
        } catch (Exception e) {
            logger.error("Error creating entry", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to insert entry");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @DeleteMapping("/entries/{id}")
    public ResponseEntity<?> deleteEntry(@PathVariable Long id) {
        try {
            boolean deleted = entryService.deleteEntry(id);
            
            if (deleted) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Entry deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Entry not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
        } catch (Exception e) {
            logger.error("Error deleting entry", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete entry");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}