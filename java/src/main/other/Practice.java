package main.other;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;

public class Practice {
	
	public static void heapSort(int[] arr){
		Queue<Integer> q = new PriorityQueue<Integer>();
		for (int i = 0; i < arr.length; i++){
			q.add(arr[i]);
		}
		for (int i = 0; i < arr.length; i++){
			arr[i] = q.poll();
		}
	}
	
	public static void quickSort(int[] arr){
		if (arr.length <= 1){
			return;
		}
		
		int pivotIndex = 0;
		int right = arr.length-1;
		while (pivotIndex < right){
			if (arr[pivotIndex] <= arr[right]){
				right--;
			}
			else{
				int temp = arr[pivotIndex];
				arr[pivotIndex] = arr[right];
				arr[right] = arr[pivotIndex+1];
				arr[pivotIndex+1] = temp;
				pivotIndex++;
			}
		}
		int[] leftSide;
		int[] rightSide;
		if (pivotIndex == 0){
			leftSide = new int[0];
			rightSide = Arrays.copyOfRange(arr, 1, arr.length);
		}
		else{
			leftSide = Arrays.copyOfRange(arr, 0, pivotIndex);
			rightSide = Arrays.copyOfRange(arr, pivotIndex+1, arr.length);
		}
		
		Practice.quickSort(leftSide);
		Practice.quickSort(rightSide);
		System.arraycopy(leftSide, 0, arr, 0, leftSide.length);
		System.arraycopy(rightSide, 0, arr, pivotIndex+1, rightSide.length);
	}
	
	public static int[] mergeSort(int[] arr){
		if (arr.length <= 1){
			return arr;
		}
		int middle = arr.length/2;
		int[] left = Arrays.copyOfRange(arr, 0, middle);
		int[] right = Arrays.copyOfRange(arr, middle, arr.length);
		return Practice.mergeSortedArrays(Practice.mergeSort(left), Practice.mergeSort(right));
	}
	
	private static int[] mergeSortedArrays(int[] arr1, int[] arr2){
		int ptr1 = 0;
		int ptr2 = 0;
		int[] result = new int[arr1.length + arr2.length];
		for (int i = 0; i < arr1.length + arr2.length; i++){
			if (ptr1 == arr1.length){
				result[i] = arr2[ptr2];
				ptr2++;
			}
			else if(ptr2 == arr2.length){
				result[i] = arr1[ptr1];
				ptr1++;
			}
			else if(arr1[ptr1] < arr2[ptr2]){
				result[i] = arr1[ptr1];
				ptr1++;
			}
			else{
				result[i] = arr2[ptr2];
				ptr2++;
			}
		}
		return result;
		
	}
	
	public static int[] fibSeq(int n){
		int[] result = new int[n];
		if (n == 0){
			return result;
		}
		if (n >= 1){
			result[0] = 1;
		}
		if (n >= 2){
			result[1] = 1;
		}
		if (n > 2){
			for (int i = 2; i < n; i++){
				result[i] = result[i-2] + result[i-1];
			}
		}
		return result;
	}
	
	public static int power(int b, int n){
		return Practice.powerHelper(b, n, 1);
	}
	private static int powerHelper(int b, int n, int total){
		if (n == 0){
			return total;
		}
		else if(n % 2 == 0){
			int halfPow = powerHelper(b, n/2, 1);
			return total*halfPow*halfPow;
		}
		else{
			return powerHelper(b, n-1, total*b);
		}
	
	}
	
	
	public static void main(String[] args){
		// int[] arr = new int[] {7,5,3,4,2,4,6,13,1,4,56,7,8,8};
		// int[] sorted = Practice.fibSeq(10);
		System.out.println(Practice.power(3, 5));
	}
	
	
}
