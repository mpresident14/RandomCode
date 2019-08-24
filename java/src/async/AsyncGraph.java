package async;

public class AsyncGraph {
  public interface AsyncCallback<T> {
    void onFinish(T value);
  }

  public static <R> AsyncNode<R> createAsync() {
    return createAsync(null);
  }

  public static <R> AsyncNode<R> createAsync(R value) {
    AsyncNode<R> node = new AsyncNode<>(value);
    node.first = node;
    return node;
  }

  // non-blocking
  public static <R> void runAsync(AsyncNode<R> asyncNode, AsyncCallback<R> callback) {
    (new Thread(() -> asyncNode.first.start(callback))).start();
  }

  // blocking
  public static <R> R getSync(AsyncNode<R> asyncNode) {
    return asyncNode.first.getValue();
  }

  public static int longRunningOp(int n) {
    try {
      Thread.sleep(1000);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    System.out.println("Thread: id " + Thread.currentThread().getId());
    return n;
  }

  public static void main(String[] args) throws InterruptedException {
    AsyncNode<String> node1 = 
        AsyncGraph
            .createAsync()
            .then(v -> longRunningOp(8))
            .then(num -> num + 0.5)
            .then(dec -> Double.toString(dec).substring(1));

    AsyncNode<String> node2 = 
        AsyncGraph
            .createAsync(8)
            .then(num -> longRunningOp(num + 7))
            .then(num -> Double.toString(num).substring(1));

    AsyncGraph.runAsync(node1, str -> System.out.println(str));

    for (int i = 0; i < 5; i++) {
      System.out.println("Main thread: id " + Thread.currentThread().getId());
    }

    System.out.println(AsyncGraph.getSync(node2));
    AsyncGraph.runAsync(node2, str -> System.out.println(str));
  }
}