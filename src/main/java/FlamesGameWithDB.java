package src.main.java;

import java.util.*;
import java.sql.*;

public class FlamesGameWithDB {
    private static String getDbUrl() {
        String dbUrl = System.getenv("DB_URL");
        if (dbUrl == null || dbUrl.isEmpty()) {
            return "jdbc:postgresql://localhost:5432/mydatabase";
        }
        // If the URL doesn't start with jdbc:postgresql://, add it
        if (!dbUrl.startsWith("jdbc:postgresql://")) {
            dbUrl = "jdbc:postgresql://" + dbUrl;
        }
        return dbUrl;
    }

    private static final String DB_URL = getDbUrl();
    private static final String USER = System.getenv().getOrDefault("DB_USER", "postgres");
    private static final String PASS = System.getenv().getOrDefault("DB_PASSWORD", "postgres");

    public static void main(String[] args) {
        try {
            // Test database connection
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                System.out.println("Database connection successful!");
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            System.exit(1);
        }

        // Create database table if it doesn't exist
        createTable();
        
        Scanner scanner = new Scanner(System.in);
        
        // Get input names
        System.out.print("Enter first name: ");
        String name1 = scanner.nextLine().toLowerCase().replaceAll("\\s+", "");
        System.out.print("Enter second name: ");
        String name2 = scanner.nextLine().toLowerCase().replaceAll("\\s+", "");
        
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
        System.out.println("\nNumber of remaining letters: " + count);
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
        
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
            System.out.println("\n" + eliminated + " is eliminated!");
            flamesList.remove(index);
            
            // Show remaining options
            System.out.print("Remaining options: ");
            for (String option : flamesList) {
                System.out.print(option + " ");
            }
            System.out.println();
            
            // Wait for user input before continuing
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
        }
        
        // Show final result
        String finalResult = flamesList.get(0);
        System.out.println("\nFinal Result: " + finalResult);
        
        // Save result to database
        saveResult(name1, name2, count, eliminatedOptions, finalResult);
        
        // Show all previous results
        showAllResults();
        
        scanner.close();
    }
    
    private static void createTable() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "CREATE TABLE IF NOT EXISTS flames_results (" +
                        "id SERIAL PRIMARY KEY," +
                        "name1 VARCHAR(100)," +
                        "name2 VARCHAR(100)," +
                        "letter_count INTEGER," +
                        "eliminated_options TEXT," +
                        "final_result VARCHAR(50)," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Database table created successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }
    
    private static void saveResult(String name1, String name2, int count, List<String> eliminatedOptions, String finalResult) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "INSERT INTO flames_results (name1, name2, letter_count, eliminated_options, final_result) " +
                        "VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name1);
                pstmt.setString(2, name2);
                pstmt.setInt(3, count);
                pstmt.setString(4, String.join(", ", eliminatedOptions));
                pstmt.setString(5, finalResult);
                
                pstmt.executeUpdate();
                System.out.println("\nResult saved to database successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Error saving result: " + e.getMessage());
        }
    }
    
    private static void showAllResults() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String sql = "SELECT * FROM flames_results ORDER BY created_at DESC";
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                System.out.println("\nPrevious Results:");
                System.out.println("----------------------------------------");
                while (rs.next()) {
                    System.out.println("Names: " + rs.getString("name1") + " & " + rs.getString("name2"));
                    System.out.println("Letter Count: " + rs.getInt("letter_count"));
                    System.out.println("Eliminated Options: " + rs.getString("eliminated_options"));
                    System.out.println("Final Result: " + rs.getString("final_result"));
                    System.out.println("Date: " + rs.getTimestamp("created_at"));
                    System.out.println("----------------------------------------");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving results: " + e.getMessage());
        }
    }
} 