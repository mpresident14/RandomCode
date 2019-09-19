package async;

import java.util.Random;

public class Main {
  private static Random random = new Random();
  private static int longRunningOp(int n) {
    try {
      Thread.sleep(random.nextInt(3000));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return n;
  }
  private static long getThreadId() {
    return Thread.currentThread().getId();
  }

  private static AsyncOperation<String> getAsyncOp(int index) {
    return AsyncGraph
        .createAsync(() -> longRunningOp(index))
        .check(num -> System.out.println(String.format("Thread %d: long op returned %d", getThreadId(), num)))
        .then(num -> Integer.toString(num));
  }

  public static void main(String[] args) {
    AsyncOperation<String> asyncOp1 = getAsyncOp(1);
    AsyncOperation<String> asyncOp2 = getAsyncOp(2);
    AsyncOperation<String> asyncOp3 = 
        AsyncGraph
            .createImmediateAsync(3)
            .thenCollapse(num -> getAsyncOp(num));
    AsyncOperation<Integer> asyncOp4 = 
        AsyncGraph
            .createImmediateAsync(4)
            .consume(num -> {})
            .check(v -> System.out.println(String.format("Thread %d: Value is %s", getThreadId(), v)))
            .then(v -> 4);

    AsyncOperation<String> asyncOpFinal = 
        AsyncGraph
            .createCombinedAsync(
                asyncOp1,
                asyncOp2,
                asyncOp3,
                asyncOp4,
                (str1, str2, str3, int4) -> String.join(", ", str1, str2, str3, Integer.toString(int4)));

    AsyncGraph.runAsync(asyncOpFinal, str -> System.out.println("Final result is " + str));

    for (int i = 0; i < 5; i++) {
      System.out.println("Main thread: id " + Thread.currentThread().getId());
    }
  }
}