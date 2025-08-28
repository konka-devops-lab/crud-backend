package com.example.crudapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        String htmlResponse = """
            <html>
              <body style="font-family: Arial, sans-serif; text-align: center; margin-top: 20px;">
                <h1 style="color: green;">Server is healthy</h1>
              </body>
            </html>
            """;
        
        return ResponseEntity.ok()
                .header("Content-Type", "text/html")
                .body(htmlResponse);
    }
}