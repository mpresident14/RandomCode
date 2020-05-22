package async;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import async.AsyncCombiners.AsyncCombiners3;
import async.AsyncCombiners.AsyncCombiners4;
import async.AsyncCombiners.AsyncCombiners5;

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
  public static <T1, T2, R> AsyncOperation<R> createCombinedAsync(
      AsyncOperation<T1> a1, 
      AsyncOperation<T2> a2, 
      BiFunction<T1, T2, R> combiner) {
    return createCombinedAsync(
        List.of((AsyncOperation<Object>) a1, (AsyncOperation<Object>) a2), 
        args -> combiner.apply((T1) args[0], (T2) args[1]));
  }

  @SuppressWarnings("unchecked")
  public static <T1, T2, T3, R> AsyncOperation<R> createCombinedAsync(
      AsyncOperation<T1> a1, 
      AsyncOperation<T2> a2,
      AsyncOperation<T3> a3,  
      AsyncCombiners3<T1, T2, T3, R> combiner) {
    return createCombinedAsync(
        List.of((AsyncOperation<Object>) a1, (AsyncOperation<Object>) a2, (AsyncOperation<Object>) a3), 
        args -> combiner.apply((T1) args[0], (T2) args[1], (T3) args[2]));
  }

  @SuppressWarnings("unchecked")
  public static <T1, T2, T3, T4, R> AsyncOperation<R> createCombinedAsync(
      AsyncOperation<T1> a1, 
      AsyncOperation<T2> a2,
      AsyncOperation<T3> a3,  
      AsyncOperation<T4> a4,
      AsyncCombiners4<T1, T2, T3, T4, R> combiner) {
    return createCombinedAsync(
        List.of((AsyncOperation<Object>) a1, (AsyncOperation<Object>) a2, (AsyncOperation<Object>) a3, (AsyncOperation<Object>) a4), 
        args -> combiner.apply((T1) args[0], (T2) args[1], (T3) args[2], (T4) args[3]));
  }

  @SuppressWarnings("unchecked")
  public static <T1, T2, T3, T4, T5, R> AsyncOperation<R> createCombinedAsync(
      AsyncOperation<T1> a1, 
      AsyncOperation<T2> a2,
      AsyncOperation<T3> a3,  
      AsyncOperation<T4> a4,
      AsyncOperation<T5> a5,
      AsyncCombiners5<T1, T2, T3, T4, T5, R> combiner) {
    return createCombinedAsync(
        List.of(
            (AsyncOperation<Object>) a1, 
            (AsyncOperation<Object>) a2, 
            (AsyncOperation<Object>) a3, 
            (AsyncOperation<Object>) a4,
            (AsyncOperation<Object>) a5),
        args -> combiner.apply((T1) args[0], (T2) args[1], (T3) args[2], (T4) args[3], (T5) args[4]));
  }

  // TODO: Is there a way to provide compile-time type safety for varargs of this?
  // TODO: Implement createCombinedAsyncCollapse

  private static <R> AsyncOperation<R> createCombinedAsync(
      List<AsyncOperation<Object>> asyncOps, 
      VarargsCombiner<R> combiner) {
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
  public static <R> void runAsync(AsyncOperation<R> asyncOp) {
    runAsync(asyncOp, v -> {});
  }

  // non-blocking
  public static <R> void runAsync(AsyncOperation<R> asyncOp, Consumer<R> callback) {
    new Thread(() -> asyncOp.first.start(callback)).start();
  }

  // blocking
  public static <R> R getSync(AsyncOperation<R> asyncOp) {
    return asyncOp.first.getValue();
  }

  // Only for testing runAsync
  static <R> void runAsyncBlocking(AsyncOperation<R> asyncOp) {
    runAsyncBlocking(asyncOp, v -> {});
  }

  // Only for testing runAsync
  static <R> void runAsyncBlocking(AsyncOperation<R> asyncOp, Consumer<R> callback) {
    Thread t = new Thread(() -> asyncOp.first.start(callback));
    t.start();
    try {
      t.join();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
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

  private interface VarargsCombiner<R> {
    R apply(Object... objects);
  }
}