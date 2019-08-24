package async;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class AsyncGraph {
  public static <R> AsyncNode<R> createVoidAsync() {
    return createAsync(null);
  }

  public static <R> AsyncNode<R> createImmediateAsync(R value) {
    return createAsync(() -> value);
  }

  public static <R> AsyncNode<R> createAsync(Callable<R> callable) {
    AsyncNode<R> node = 
        new AsyncNode<R>(
            (unused) -> 
            {
              try {
                return callable.call();
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            });

    node.first = node;
    return node;
  }

  // non-blocking
  public static <R> void runAsync(AsyncNode<R> asyncNode, Consumer<R> callback) {
    new Thread(() -> asyncNode.first.start(callback)).start();
  }

  // blocking
  public static <R> R getSync(AsyncNode<R> asyncNode) {
    return asyncNode.first.getValue();
  }
}