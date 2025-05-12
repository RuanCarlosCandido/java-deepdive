package org;

public interface App {

    public static void main(String[] args) {

        Solution solution = new Solution();
        String a = "1010";
        String b = "1011";

        System.out.println(solution.addBinary(a, b));

    }

    class Solution {
        public String addBinary(String a, String b) {
            StringBuilder result = new StringBuilder();
    
            int toNext = 0;
            int aIndex = a.length() - 1;
            int bIndex = b.length() - 1;
    
            boolean hasNextInA = true;
            boolean hasNextInB = true;
            boolean hasMoreElements = true;
    
            while (hasMoreElements) {
                int sum = 0;
    
                if (aIndex >= 0 && bIndex >= 0) {
                    sum = Character.getNumericValue(a.charAt(aIndex)) + Character.getNumericValue(b.charAt(bIndex));
                    aIndex--;
                    bIndex--;
                } else if (aIndex >= 0) {
                    sum = Character.getNumericValue(a.charAt(aIndex));
                    hasNextInB = false;
                    aIndex--;
                } else if (bIndex >= 0) {
                    sum = Character.getNumericValue(b.charAt(bIndex));
                    hasNextInA = false;
                    bIndex--;
                } else {
                    break;
                }
    
                sum += toNext;
                toNext = 0;
    
                if (sum > 2) {
                    sum = 1;
                    toNext = 1;
                } else if (sum > 1) {
                    sum = 0;
                    toNext = 1;
                }
    
                result.insert(0, sum);
    
                hasMoreElements = hasNextInA || hasNextInB;
            }
    
            if (toNext == 1) {
                result.insert(0, toNext);
            }
    
            return result.toString();
        }
    }
}