package async;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class AsyncOperation<T> {
  Function<?, T> myTransform = null;
  AsyncOperation<?> next = null;
  Object prevValue = null;
  AsyncOperation<?> first = null;
  List<Consumer<T>> peekers = new ArrayList<>();

  AsyncOperation(Function<?, T> transform) {
    this.myTransform = transform;
  }

  @SuppressWarnings("unchecked")
  public <N> AsyncOperation<N> then(Function<T, N> transform) {
    next = new AsyncOperation<>(transform);
    next.first = this.first;
    return (AsyncOperation<N>) next;
  }

  public AsyncOperation<T> peek(Consumer<T> nextConsumer) {
    peekers.add(nextConsumer);
    return this;
  }

  @SuppressWarnings("unchecked")
  <P, N> void start(Consumer<?> callback) {
    T myValue = ((Function<P, T>) myTransform).apply((P) prevValue);
    runPeekers(myValue);
    if (next == null) {
      ((Consumer<T>) callback).accept(myValue);
    } else {
      next.prevValue = myValue;
      next.start(callback);
    }
  }

  @SuppressWarnings("unchecked")
  <P, R, N> R getValue() {
    T myValue = ((Function<P, T>) myTransform).apply((P) prevValue);
    runPeekers(myValue);
    if (next == null) {
      return (R) myValue;
    } else {
      next.prevValue = myValue;
      return (R) next.getValue();
    }
  }

  private void runPeekers(T myValue) {
    for (Consumer<T> peeker : peekers) {
      peeker.accept(myValue);
    }
  }
}