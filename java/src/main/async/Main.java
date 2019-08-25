package async;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Main {
  public static int longRunningOp(int n) {
    try {
      Thread.sleep(1000);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return n;
  }

  public static void main(String[] args) throws InterruptedException {
    AsyncOperation<String> asyncOp1 = 
        AsyncGraph
            .createAsync(() -> longRunningOp(8))
            .peek(num -> System.out.println("long op finished"))
            .peek(num -> System.out.println("long op returned " + num))
            .then(num -> num + 0.5)
            .then(dec -> Double.toString(dec).substring(1));

    AsyncOperation<String> asyncOp2 = 
        AsyncGraph
            .createImmediateAsync(8)
            .then(num -> longRunningOp(num + 7))
            .peek(num -> System.out.println("Thread: id " + Thread.currentThread().getId())            )
            .then(num -> Double.toString(num).substring(1));

    AsyncGraph.runAsync(asyncOp1, 
        str -> System.out.println(
            String.format("Thread %d computed %s", Thread.currentThread().getId(), str)));

    for (int i = 0; i < 5; i++) {
      System.out.println("Main thread: id " + Thread.currentThread().getId());
    }

    System.out.println(
        String.format("Thread %d computed %s", 
            Thread.currentThread().getId(), AsyncGraph.getSync(asyncOp2)));

    AsyncGraph.runAsync(
        asyncOp2, 
        str -> System.out.println(
            String.format("Thread %d computed %s", Thread.currentThread().getId(), str)));

    List<String> strList = new ArrayList<>(Arrays.asList("a", "b", "c"));

    Function<String, AsyncOperation<Void>> fnReturnAsyncOp = 
        str -> 
            AsyncGraph
                .createAsync(() -> {
                    strList.remove(str);
                    return null;
                });

    AsyncOperation<String> asyncOp = 
        AsyncGraph
            .createAsync(() -> "a")
            .peekCollapse(str -> fnReturnAsyncOp.apply(str));        
  }
}