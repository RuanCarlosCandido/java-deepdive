package org;

public interface App {

    public static void main(String[] args) {

        Solution solution = new Solution();
        String a = "1010";
        String b = "1011";

        // System.out.println(solution.addBinary(a, b));

        // System.out.println(solution.mySqrt(9));
        // System.out.println(solution.mySqrt(4));
        // System.out.println(solution.mySqrt(8));

        System.out.println(solution.climbStairs(10));

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

        // 9 => [1,2,3,4,5,6,7,8,9]
        //
        public int mySqrt(int x) {

            int result = 0;

            int left = 1;
            int right = x / 2;

            while (left <= right) {

                int mid = left + (right - left) / 2;

                // System.out.println("mid => " + mid);

                long square = (long) mid * mid;

                // System.out.println("square => " + square);

                if (square == x) {
                    return mid;
                } else if (square < x) {
                    result = mid;
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }

            }

            return result;

        }

        public int climbStairs(int n) {
            if (n == 1) {
                return 1;
            }
            if (n == 2) {
                return 2;
            }

            int first = 1; // Formas de chegar no degrau 1
            int second = 2; // Formas de chegar no degrau 2

            for (int degrau = 3; degrau <= n; degrau++) {
                int next = first + second;
                first = second;
                second = next;
            }

            return second;
        }
    }
}