package other;

import java.util.Arrays;

class QuickSort {
  public static int[] sortArray(int[] nums) {
      if (nums == null) {
          return null;
      }
      quickSort(nums, 0, nums.length - 1);
      return nums;
  }
  
  private static void quickSort(int[] arr, int start, int end) {
      if (start >= end) {
          return;
      }
      
      int pivot = start;
      int right = end;
      while (pivot < right) {
          if (arr[pivot] > arr[right]) {
              int temp = arr[pivot];
              arr[pivot] = arr[right];
              arr[right] = arr[pivot + 1];
              arr[pivot + 1] = temp;
          } else {
              right--;
          }
      }
      
      if (pivot == start) {
          quickSort(arr, start + 1, end);
      } else {
          quickSort(arr, start, pivot - 1);
          quickSort(arr, pivot, end);
      }        
  }
  
  public static void main(String[] args) {
    int[] arr = new int[] {3,2,4,5,-1};
    QuickSort.sortArray(arr);
    System.out.println(Arrays.toString(arr));
  }
}