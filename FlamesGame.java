import java.util.*;

public class FlamesGame {
    public static void main(String[] args) {
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
        
        // Process FLAMES
        while (flamesList.size() > 1) {
            int index = (count % flamesList.size()) - 1;
            if (index < 0) {
                index = flamesList.size() - 1;
            }
            
            System.out.println("\n" + flamesList.get(index) + " is eliminated!");
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
        System.out.println("\nFinal Result: " + flamesList.get(0));
        
        scanner.close();
    }
} 