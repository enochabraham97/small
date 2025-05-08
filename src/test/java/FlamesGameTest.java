package src.test.java;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class FlamesGameTest {
    
    @Test
    public void testRemoveCommonLetters() {
        String name1 = "john";
        String name2 = "jane";
        
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
        
        assertEquals("ohae", remainingLetters.toString());
    }
    
    @Test
    public void testFlamesElimination() {
        String[] flames = {"Friendship", "Love", "Attraction", "Marriage", "Enemy", "Sibling"};
        List<String> flamesList = new ArrayList<>(Arrays.asList(flames));
        int count = 4;
        
        int index = (count % flamesList.size()) - 1;
        if (index < 0) {
            index = flamesList.size() - 1;
        }
        
        String eliminated = flamesList.get(index);
        flamesList.remove(index);
        
        assertEquals("Marriage", eliminated);
        assertEquals(5, flamesList.size());
    }
} 