package async;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import async.AsyncGraph.AsyncCallback;

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

  // Maybe have a List of Consumer Functions for each node?
  public AsyncNode<T> peek(Consumer<T> nextConsumer) {
    // if (peekers == null) {
    //   peekers = new ArrayList<>();
    // }
    peekers.add(nextConsumer);
    return this;
  }

  @SuppressWarnings("unchecked")
  <N> void start(AsyncCallback<?> callback) {
    runPeekers();
    AsyncNode<N> castNext = (AsyncNode<N>) next;
    if (next == null) {
      ((AsyncCallback<T>) callback).onFinish(myValue);
    } else {
      castNext.myValue = (N) nextTransform.apply(myValue);
      next.start(callback);
    }
  }

  @SuppressWarnings("unchecked")
  <R ,N> R getValue() {
    runPeekers();
    AsyncNode<N> castNext = (AsyncNode<N>) next;
    if (next == null) {
      return (R) myValue;
    } else {
      castNext.myValue = (N) nextTransform.apply(myValue);
      return (R) next.getValue();
    }
  }

  private void runPeekers() {
    for (Consumer<T> peeker : peekers) {
      peeker.accept(myValue);
    }
  }
}