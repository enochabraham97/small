package com.example.flames;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api/flames")
public class FlamesGameWithDB {
    @Value("${spring.datasource.url}")
    private String dbUrl;
    
    @Value("${spring.datasource.username}")
    private String dbUser;
    
    @Value("${spring.datasource.password}")
    private String dbPassword;

    @PostMapping("/calculate")
    public Map<String, Object> calculateFlames(@RequestBody Map<String, String> request) {
        String name1 = request.get("name1").toLowerCase().replaceAll("\\s+", "");
        String name2 = request.get("name2").toLowerCase().replaceAll("\\s+", "");
        
        // Remove common letters
        StringBuilder remainingLetters = new StringBuilder();
        for (char c : name1.toCharArray()) {
            if (name2.indexOf(c) == -1) {
                remainingLetters.append(c);
            }
        }
        for (char c : name2.toCharArray()) {
            if (name1.indexOf(c) == -1) {
                remainingLetters.append(c);
            }
        }
        
        int count = remainingLetters.length();
        
        // FLAMES array and their meanings
        String[] flames = {"Friendship", "Love", "Attraction", "Marriage", "Enemy", "Sibling"};
        List<String> flamesList = new ArrayList<>(Arrays.asList(flames));
        List<String> eliminatedOptions = new ArrayList<>();
        
        // Process FLAMES
        while (flamesList.size() > 1) {
            int index = (count % flamesList.size()) - 1;
            if (index < 0) {
                index = flamesList.size() - 1;
            }
            
            String eliminated = flamesList.get(index);
            eliminatedOptions.add(eliminated);
            flamesList.remove(index);
        }
        
        String finalResult = flamesList.get(0);
        
        // Save result to database
        saveResult(name1, name2, count, eliminatedOptions, finalResult);
        
        Map<String, Object> response = new HashMap<>();
        response.put("name1", name1);
        response.put("name2", name2);
        response.put("letterCount", count);
        response.put("eliminatedOptions", eliminatedOptions);
        response.put("finalResult", finalResult);
        
        return response;
    }
    
    @GetMapping("/results")
    public List<Map<String, Object>> getAllResults() {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            String sql = "SELECT * FROM flames_results ORDER BY created_at DESC";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("name1", rs.getString("name1"));
                    result.put("name2", rs.getString("name2"));
                    result.put("letterCount", rs.getInt("letter_count"));
                    result.put("eliminatedOptions", rs.getString("eliminated_options"));
                    result.put("finalResult", rs.getString("final_result"));
                    result.put("createdAt", rs.getTimestamp("created_at"));
                    results.add(result);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
    
    private void saveResult(String name1, String name2, int count, List<String> eliminatedOptions, String finalResult) {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            String sql = "INSERT INTO flames_results (name1, name2, letter_count, eliminated_options, final_result) " +
                        "VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name1);
                pstmt.setString(2, name2);
                pstmt.setInt(3, count);
                pstmt.setString(4, String.join(", ", eliminatedOptions));
                pstmt.setString(5, finalResult);
                
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 