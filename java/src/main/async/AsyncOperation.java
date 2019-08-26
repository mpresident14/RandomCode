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

  @SuppressWarnings("unchecked")
  public <N> AsyncOperation<N> thenCollapse(Function<T, AsyncOperation<N>> transform) {
    next = new AsyncOperation<N>((T var) -> AsyncGraph.getSync(transform.apply(var)));
    next.first = this.first;
    return (AsyncOperation<N>) next;
  }

  @SuppressWarnings("unchecked")
  public AsyncOperation<Void> consume(Consumer<T> consumer) {
    next = new AsyncOperation<Void>((T var) -> 
        {
            consumer.accept(var);
            return null;
        });
    next.first = this.first;
    return (AsyncOperation<Void>) next;
  }

  @SuppressWarnings("unchecked")
  public AsyncOperation<Void> consumeCollapse(Function<T, AsyncOperation<Void>> transform) {
    next = new AsyncOperation<Void>((T var) -> 
        {
            AsyncGraph.getSync(transform.apply(var));
            return null;
        });
    next.first = this.first;
    return (AsyncOperation<Void>) next;
  }

  @SuppressWarnings("unchecked")
  public AsyncOperation<Void> returnVoid() {
    next = new AsyncOperation<>((T var) -> null);
    next.first = this.first;
    return (AsyncOperation<Void>) next;
  }

  public AsyncOperation<T> check(Consumer<T> nextConsumer) {
    peekers.add(nextConsumer);
    return this;
  }

  public AsyncOperation<T> checkCollapse(Function<T, AsyncOperation<Void>> nextConsumer) {
    peekers.add((T var) -> AsyncGraph.getSync(nextConsumer.apply(var)));
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
