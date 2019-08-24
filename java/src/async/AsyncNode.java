package async;

import java.util.function.Function;

import async.AsyncGraph.AsyncCallback;

public class AsyncNode<T> {
  Function<T, ?> nextTransform = null;
  AsyncNode<?> next = null;
  T myValue = null;
  AsyncNode<?> first = null;

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

  @SuppressWarnings("unchecked")
  <N> void start(AsyncCallback<?> callback) {
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
    AsyncNode<N> castNext = (AsyncNode<N>) next;
    if (next == null) {
      return (R) myValue;
    } else {
      castNext.myValue = (N) nextTransform.apply(myValue);
      return (R) next.getValue();
    }
  }
}