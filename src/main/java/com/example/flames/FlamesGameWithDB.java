package com.example.flames;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/flames")
public class FlamesGameWithDB {
    private static final String[] FLAMES = {"Friendship", "Love", "Attraction", "Marriage", "Enemy", "Sibling"};
    
    @GetMapping("/results")
    public ResponseEntity<List<FlamesResult>> getAllResults() {
        List<FlamesResult> results = new ArrayList<>();
        String url = System.getenv("SPRING_DATASOURCE_URL");
        String user = System.getenv("SPRING_DATASOURCE_USERNAME");
        String password = System.getenv("SPRING_DATASOURCE_PASSWORD");
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            createTableIfNotExists(conn);
            String sql = "SELECT * FROM flames_results ORDER BY created_at DESC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    FlamesResult result = new FlamesResult();
                    result.setName1(rs.getString("name1"));
                    result.setName2(rs.getString("name2"));
                    result.setLetterCount(rs.getInt("letter_count"));
                    result.setEliminatedOptions(rs.getString("eliminated_options").split(", "));
                    result.setFinalResult(rs.getString("final_result"));
                    result.setCreatedAt(rs.getTimestamp("created_at"));
                    results.add(result);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(results);
    }

    @PostMapping("/calculate")
    public ResponseEntity<FlamesResult> calculateFlames(@RequestBody FlamesRequest request) {
        String name1 = request.getName1().toLowerCase();
        String name2 = request.getName2().toLowerCase();
        
        // Calculate common letters
        int[] charCount = new int[26];
        for (char c : name1.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                charCount[c - 'a']++;
            }
        }
        for (char c : name2.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                charCount[c - 'a']--;
            }
        }
        
        // Count remaining letters
        int letterCount = 0;
        for (int count : charCount) {
            letterCount += Math.abs(count);
        }
        
        // Calculate FLAMES
        List<String> remaining = new ArrayList<>(List.of(FLAMES));
        int currentIndex = 0;
        while (remaining.size() > 1) {
            currentIndex = (currentIndex + letterCount - 1) % remaining.size();
            remaining.remove(currentIndex);
        }
        
        FlamesResult result = new FlamesResult();
        result.setName1(name1);
        result.setName2(name2);
        result.setLetterCount(letterCount);
        result.setEliminatedOptions(FLAMES);
        result.setFinalResult(remaining.get(0));
        
        // Save to database
        String url = System.getenv("SPRING_DATASOURCE_URL");
        String user = System.getenv("SPRING_DATASOURCE_USERNAME");
        String password = System.getenv("SPRING_DATASOURCE_PASSWORD");
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            createTableIfNotExists(conn);
            String sql = "INSERT INTO flames_results (name1, name2, letter_count, eliminated_options, final_result) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name1);
                pstmt.setString(2, name2);
                pstmt.setInt(3, letterCount);
                pstmt.setString(4, String.join(", ", FLAMES));
                pstmt.setString(5, remaining.get(0));
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        
        return ResponseEntity.ok(result);
    }
    
    private void createTableIfNotExists(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS flames_results (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name1 VARCHAR(255), " +
                    "name2 VARCHAR(255), " +
                    "letter_count INTEGER, " +
                    "eliminated_options TEXT, " +
                    "final_result VARCHAR(255), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}

class FlamesRequest {
    private String name1;
    private String name2;
    
    public String getName1() { return name1; }
    public void setName1(String name1) { this.name1 = name1; }
    public String getName2() { return name2; }
    public void setName2(String name2) { this.name2 = name2; }
}

class FlamesResult {
    private String name1;
    private String name2;
    private int letterCount;
    private String[] eliminatedOptions;
    private String finalResult;
    private java.sql.Timestamp createdAt;
    
    public String getName1() { return name1; }
    public void setName1(String name1) { this.name1 = name1; }
    public String getName2() { return name2; }
    public void setName2(String name2) { this.name2 = name2; }
    public int getLetterCount() { return letterCount; }
    public void setLetterCount(int letterCount) { this.letterCount = letterCount; }
    public String[] getEliminatedOptions() { return eliminatedOptions; }
    public void setEliminatedOptions(String[] eliminatedOptions) { this.eliminatedOptions = eliminatedOptions; }
    public String getFinalResult() { return finalResult; }
    public void setFinalResult(String finalResult) { this.finalResult = finalResult; }
    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }
} 