package async;

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
    AsyncNode<String> node1 = 
        AsyncGraph
            .createAsync(() -> longRunningOp(8))
            .peek(num -> System.out.println("long op finished"))
            .peek(num -> System.out.println("long op returned " + num))
            .then(num -> num + 0.5)
            .then(dec -> Double.toString(dec).substring(1));

    AsyncNode<String> node2 = 
        AsyncGraph
            .createImmediateAsync(8)
            .then(num -> longRunningOp(num + 7))
            .peek(num -> System.out.println("Thread: id " + Thread.currentThread().getId())            )
            .then(num -> Double.toString(num).substring(1));

    AsyncGraph.runAsync(node1, 
        str -> System.out.println(
            String.format("Thread %d computed %s", Thread.currentThread().getId(), str)));

    for (int i = 0; i < 5; i++) {
      System.out.println("Main thread: id " + Thread.currentThread().getId());
    }

    System.out.println(
        String.format("Thread %d computed %s", 
            Thread.currentThread().getId(), AsyncGraph.getSync(node2)));

    AsyncGraph.runAsync(
        node2, 
        str -> System.out.println(
            String.format("Thread %d computed %s", Thread.currentThread().getId(), str)));
  }
}