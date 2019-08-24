package async;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class AsyncNode<T> {
  Function<T, ?> nextTransform = null;
  AsyncNode<?> next = null;
  T myValue = null;
  AsyncNode<?> first = null;
  List<Consumer<T>> peekers = new ArrayList<>();

  AsyncNode(T value) {
    this.myValue = value;
  }

  <N> AsyncNode(Function<T, N> nextTransform, AsyncNode<?> first) {
    this.nextTransform = nextTransform;
    this.first = first;
  }

  @SuppressWarnings("unchecked")
  public <N> AsyncNode<N> then(Function<T, N> nextTransform) {
    this.nextTransform = nextTransform;
    next = new AsyncNode<>(null, first);
    return (AsyncNode<N>) next;
  }

  public AsyncNode<T> peek(Consumer<T> nextConsumer) {
    peekers.add(nextConsumer);
    return this;
  }

  @SuppressWarnings("unchecked")
  <N> void start(Consumer<?> callback) {
    runPeekers();
    if (next == null) {
      ((Consumer<T>) callback).accept(myValue);
    } else {
      ((AsyncNode<N>) next).myValue = (N) nextTransform.apply(myValue);
      next.start(callback);
    }
  }

  @SuppressWarnings("unchecked")
  <R, N> R getValue() {
    runPeekers();
    if (next == null) {
      return (R) myValue;
    } else {
      ((AsyncNode<N>) next).myValue = (N) nextTransform.apply(myValue);
      return (R) next.getValue();
    }
  }

  private void runPeekers() {
    for (Consumer<T> peeker : peekers) {
      peeker.accept(myValue);
    }
  }
}