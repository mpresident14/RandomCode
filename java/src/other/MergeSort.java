package other;

import java.util.Arrays;

class MergeSort {
    public static int[] sortArray(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return Arrays.copyOf(nums, nums.length);
        }
        int mid = nums.length / 2;
        int[] left = sortArray(Arrays.copyOfRange(nums, 0, mid));
        int[] right = sortArray(Arrays.copyOfRange(nums, mid, nums.length));
        return merge(left, right);
    }
    
    private static int[] merge(int[] left, int[] right) {
        int[] result = new int[left.length + right.length];
        int leftIndex = 0;
        int rightIndex = 0;
        for (int i = 0; i < left.length + right.length; i++) {
            if (leftIndex == left.length) {
                result[i] = right[rightIndex];
                rightIndex++;
                continue;
            } else if (rightIndex == right.length) {
                result[i] = left[leftIndex];
                leftIndex++;
                continue;
            }
            int leftNum = left[leftIndex];
            int rightNum = right[rightIndex];
            if (leftNum < rightNum) {
                result[i] = left[leftIndex];
                leftIndex++;
            } else {
                result[i] = right[rightIndex];
                rightIndex++;
            }
        }
        return result;
    }

    public static void main(String[] args) {
      int[] arr = new int[] {3,2,4,5,-1};
      System.out.println(Arrays.toString(MergeSort.sortArray(arr)));
    }
}