package other;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamPractice {
		
	public static void main(String[] args) {
		int[] arr = new int[]{4,2,7,1,3};
		
		Arrays.stream(arr).forEach(e -> System.out.print("element " + e + ", "));
		System.out.println("");
		int maxWithoutFirstThree = Arrays.stream(arr).skip(3).max().getAsInt();
		int sum = Arrays.stream(arr).sum();
		int sumOfFirstThree = Arrays.stream(arr).limit(3).sum();
		int[] multByTwo = Arrays.stream(arr).map(e -> e*2).toArray();
		int[] onlyGreaterThanTwo = Arrays.stream(arr).filter(e -> e > 2).toArray();
		int product = Arrays.stream(arr).reduce((e1, e2) -> e1 * e2).getAsInt();
		int productWithInitTwo = Arrays.stream(arr).reduce(2, (e1, e2) -> e1 * e2);
		long count = Arrays.stream(arr).count();
		Map<Integer, List<String>> lengthCounts = Stream.of("a", "b", "bcd", "ef", "def", "c").collect(Collectors.groupingBy(e -> e.length()));
		String concat = Stream.of(1,2,3,4,5).map(e -> Integer.toString(e)).collect(Collectors.joining(",", "[", "]"));
		
		
		System.out.println(maxWithoutFirstThree + "\n" + sum + "\n" + sumOfFirstThree);
		System.out.println(Arrays.toString(multByTwo));
		System.out.println(Arrays.toString(onlyGreaterThanTwo));
		System.out.println(product + "\n" + productWithInitTwo + "\n" + count);
		System.out.println(lengthCounts);
		System.out.println(concat);
	}
}
