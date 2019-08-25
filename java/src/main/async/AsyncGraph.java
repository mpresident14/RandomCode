package async;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class AsyncGraph {
  public static AsyncOperation<Void> createVoidAsync() {
    return createImmediateAsync(null);
  }

  public static <R> AsyncOperation<R> createImmediateAsync(R value) {
    return createAsync(() -> value);
  }

  public static <R> AsyncOperation<R> createAsync(Callable<R> callable) {
    AsyncOperation<R> asyncOp = 
        new AsyncOperation<R>(
            unused -> 
                {
                  try {
                    return callable.call();
                  } catch (Exception e) {
                    throw new RuntimeException(e);
                  }
                });

    asyncOp.first = asyncOp;
    return asyncOp;
  }

  // non-blocking
  public static <R> void runAsync(AsyncOperation<R> asyncOp, Consumer<R> callback) {
    new Thread(() -> asyncOp.first.start(callback)).start();
  }

  // blocking
  public static <R> R getSync(AsyncOperation<R> asyncOp) {
    return asyncOp.first.getValue();
  }
}