package other;

public class Leetcode {
	
	public int findPaths(int m, int n, int N, int i, int j) {
        if (N == 0) {
            return 0;
        }
        int[][][] dp = new int[m][n][N];
        for (int row = 0; row < m; row++) {
            for (int col = 0; col < n; col++) {
                int sum = 0;
                if (row == 0) {
                    sum++;
                }
                if (row == m - 1) {
                    sum++;
                }
                if (col == 0) {
                    sum++;
                }
                if (col == n - 1) {
                    sum++;
                }
                dp[row][col][0] = sum;
            }
        }
        
        int modulus = (int) Math.pow(10, 9) + 7;
        
        for (int move = 1; move < N; move++) {
            for (int row = 0; row < m; row++) {
                for (int col = 0; col < n; col++) {
                
                    int sum = 0;
                    if (row != 0) {
                        sum += dp[row-1][col][move-1] % modulus;
                    }
                    if (row != m - 1) {
                        sum += dp[row+1][col][move-1] % modulus;
                        sum %= modulus;
                    }
                    if (col != 0) {
                        sum += dp[row][col-1][move-1] % modulus;
                        sum %= modulus;
                    }
                    if (col != n - 1) {
                        sum += dp[row][col+1][move-1] % modulus;
                        sum %= modulus;
                    }
                    dp[row][col][move] = sum;
                }    
            }
        }
        int total = 0;
        for (int move = 0; move < N; move++) { 
            total += dp[i][j][move];
            if (total > modulus) {
                total = total % modulus;
            }
        }    
        return total;
    }
	
	public static void main(String[] args) {
		Leetcode leetcode = new Leetcode();
		
		System.out.println(leetcode.findPaths(8,50,23,5,26));
	}
}
