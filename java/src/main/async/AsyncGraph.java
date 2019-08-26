package async;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.function.BiFunction;
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

  @SuppressWarnings("unchecked")
  public static <T1, T2, R> AsyncOperation<R> combineAsync(
      AsyncOperation<T1> a1, 
      AsyncOperation<T2> a2, 
      BiFunction<T1, T2, R> combiner) {
    return combineAsync(
        List.of((AsyncOperation<Object>) a1, (AsyncOperation<Object>) a2), 
        args -> combiner.apply((T1) args[0], (T2) args[1]));
  }

  private static <R> AsyncOperation<R> combineAsync(
      List<AsyncOperation<Object>> asyncOps, 
      AsyncCombiner<R> combiner) {
    int numOps = asyncOps.size();
    Callable<R> callable = () ->
        {
          Semaphore semaphore = new Semaphore(0, true);
          Object[] results = new Object[numOps];
          
          for (int i = 0; i < numOps; i++) {
            runAsync(asyncOps.get(i), getCombinerResultHandler(semaphore, results, i));
          }
          
          semaphore.acquire(numOps);
          return combiner.apply(results);
        };

    return createAsync(callable);
  }

  // non-blocking
  public static <R> void runAsync(AsyncOperation<R> asyncOp, Consumer<R> callback) {
    new Thread(() -> asyncOp.first.start(callback)).start();
  }

  // blocking
  public static <R> R getSync(AsyncOperation<R> asyncOp) {
    return asyncOp.first.getValue();
  }

  private static <R> Consumer<R> getCombinerResultHandler(
    Semaphore semaphore,
    Object[] results,
    int index) {
    return result -> {
      results[index] = result;
      semaphore.release();
    };
  }

  public interface AsyncCombiner<R> {
    R apply(Object... objects);
  }
}