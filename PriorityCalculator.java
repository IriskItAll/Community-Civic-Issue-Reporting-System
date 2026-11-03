package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Calculates a priority score (0-100) for a complaint using:
 *  1. Category weight   (e.g. water/electricity are more critical)
 *  2. Urgent keywords   in the description
 *  3. Community upvotes (more people affected = higher priority)
 *  4. Days pending      (older complaints rise in priority)
 */
public class PriorityCalculator {

    private static final Map<String, Integer> CATEGORY_WEIGHT = new HashMap<>();
    private static final String[] URGENT_KEYWORDS = {
            "urgent", "danger", "dangerous", "accident", "flood", "fire", "hospital",
            "school", "children", "live wire", "shock", "overflow", "injury", "emergency"
    };

    static {
        CATEGORY_WEIGHT.put("Water Supply", 25);
        CATEGORY_WEIGHT.put("Electricity", 25);
        CATEGORY_WEIGHT.put("Drainage / Sewage", 22);
        CATEGORY_WEIGHT.put("Road / Pothole", 20);
        CATEGORY_WEIGHT.put("Garbage", 15);
        CATEGORY_WEIGHT.put("Street Light", 15);
        CATEGORY_WEIGHT.put("Other", 10);
    }

    public static final String[] CATEGORIES = {
            "Water Supply", "Electricity", "Drainage / Sewage",
            "Road / Pothole", "Garbage", "Street Light", "Other"
    };

    public static int calculate(String category, String description, int upvotes, long daysPending) {
        int score = CATEGORY_WEIGHT.getOrDefault(category, 10);

        // keyword bonus (max 30)
        int keywordBonus = 0;
        String text = description == null ? "" : description.toLowerCase();
        for (String k : URGENT_KEYWORDS) {
            if (text.contains(k)) keywordBonus += 10;
        }
        score += Math.min(keywordBonus, 30);

        // upvote bonus (max 30)
        score += Math.min(upvotes * 3, 30);

        // age bonus (max 20)
        score += (int) Math.min(daysPending * 2, 20);

        return Math.min(score, 100);
    }

    public static String getLevel(int score) {
        if (score >= 70) return "Critical";
        if (score >= 50) return "High";
        if (score >= 30) return "Medium";
        return "Low";
    }
}