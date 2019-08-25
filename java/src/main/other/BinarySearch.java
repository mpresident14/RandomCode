package other;

class BinarySearch {
  public static boolean searchMatrix(int[][] matrix, int target) {
      int rows = matrix.length;
      int cols = matrix[0].length;
      int low = 0;
      int high = rows * cols - 1;
      int mid = 0;
      
      while (low <= high) {
          mid = (low + high) / 2;
          int midNum = matrix[mid / cols][mid % cols];
          if (target == midNum) {
              return true;
          }
          if (target < midNum) {
              high = mid - 1;
          } else {
              low = mid + 1;
          }
      }
      return false;
  } 

  public static void main(String[] args) {
    int [][] matrix = 
        {
            {1, 3, 5, 7},
            {10, 11, 16, 20},
            {23, 30, 34, 50}
        };
    System.out.println(BinarySearch.searchMatrix(matrix, 3));
  }
}