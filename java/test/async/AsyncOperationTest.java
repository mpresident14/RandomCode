package async;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.Test;
import other.Wrapper;

public class AsyncOperationTest {

  @Test
  public void test_createImmediateAsync() {
    int expected = 8;

    AsyncOperation<Integer> asyncOp = AsyncGraph.createImmediateAsync(expected);
    int actual = AsyncGraph.getSync(asyncOp);

    assertThat(actual, is(expected));
    assertAsyncResults(asyncOp, result -> assertThat(result, is(expected)));
  }

  @Test
  public void test_createVoidAsync() {
    AsyncOperation<Void> asyncOp = AsyncGraph.createVoidAsync();
    Object actual = AsyncGraph.getSync(asyncOp);

    assertNull(actual);
    assertAsyncResults(asyncOp, result -> assertNull(result));
  }

  @Test
  public void test_createAsync() {
    String expected = "string";

    AsyncOperation<String> asyncOp = AsyncGraph.createAsync(() -> expected);
    String actual = AsyncGraph.getSync(asyncOp);

    assertThat(actual, is(expected));
    assertAsyncResults(asyncOp, result -> assertThat(result, is(expected)));
  }

  @Test
  public void test_runAsync_runsOnDifferentThread() {
    long mainThreadId = getThreadId();
    Wrapper<Long> asyncThreadId = new Wrapper<>();

    AsyncOperation<Void> asyncOp =
        AsyncGraph.createVoidAsync().check(v -> asyncThreadId.set(getThreadId()));

    AsyncGraph.runAsyncBlocking(asyncOp);

    assertThat(asyncThreadId.get(), not(mainThreadId));
  }

  @Test
  public void test_runAsync_executesCallback() {
    Wrapper<String> actual = new Wrapper<>();

    AsyncOperation<String> asyncOp =
        AsyncGraph.createImmediateAsync(5).then(num -> Integer.toString(num));

    AsyncGraph.runAsyncBlocking(asyncOp, result -> actual.set(result));

    assertThat(actual.get(), is("5"));
  }

  @Test
  public void test_getSync_runsOnSameThread() {
    long mainThreadId = getThreadId();
    Wrapper<Long> syncThreadId = new Wrapper<>();

    AsyncOperation<Void> asyncOp =
        AsyncGraph.createVoidAsync().check(v -> syncThreadId.set(getThreadId()));

    AsyncGraph.getSync(asyncOp);

    assertThat(syncThreadId.get(), is(mainThreadId));
  }

  @Test
  public void test_then() {
    AsyncOperation<Integer> asyncOp = AsyncGraph.createImmediateAsync(5).then(num -> num + 1);

    int actual = AsyncGraph.getSync(asyncOp);

    assertThat(actual, is(6));
    assertAsyncResults(asyncOp, result -> assertThat(actual, is(6)));
  }

  @Test
  public void test_thenCollapse() {
    String string = "Hello";
    Function<String, AsyncOperation<Integer>> fnReturnAsyncOp =
        str -> AsyncGraph.createImmediateAsync(str).then(String::length);

    AsyncOperation<Integer> asyncOp =
        AsyncGraph.createAsync(() -> string).thenCollapse(str -> fnReturnAsyncOp.apply(str));
    int actual = AsyncGraph.getSync(asyncOp);

    assertThat(actual, is(string.length()));
    assertAsyncResults(asyncOp, result -> assertThat(actual, is(string.length())));
  }

  @Test
  public void test_consume() {
    List<String> strList = new ArrayList<>(List.of("a", "a", "b", "c"));
    AsyncOperation<Void> asyncOp =
        AsyncGraph.createImmediateAsync("a").consume(str -> strList.remove(str));

    Object actual = AsyncGraph.getSync(asyncOp);
    assertNull(actual);
    assertThat(strList, is(List.of("a", "b", "c")));
    assertAsyncResults(
        asyncOp,
        result -> assertNull(actual),
        result -> assertThat(strList, is(List.of("b", "c"))));
  }

  @Test
  public void test_consumeCollapse() {
    List<String> strList = new ArrayList<>(List.of("a", "a", "b", "c"));
    Function<String, AsyncOperation<Void>> fnReturnAsyncOp =
        str ->
            AsyncGraph.createAsync(
                () -> {
                  strList.remove(str);
                  return null;
                });

    AsyncOperation<Void> asyncOp =
        AsyncGraph.createAsync(() -> "a").consumeCollapse(str -> fnReturnAsyncOp.apply(str));

    Object actual = AsyncGraph.getSync(asyncOp);
    assertNull(actual);
    assertThat(strList, is(List.of("a", "b", "c")));
    assertAsyncResults(
        asyncOp,
        result -> assertNull(actual),
        result -> assertThat(strList, is(List.of("b", "c"))));
  }

  @Test
  public void test_returnVoid() {
    AsyncOperation<Void> asyncOp = AsyncGraph.createImmediateAsync(5).returnVoid();

    Object actual = AsyncGraph.getSync(asyncOp);

    assertNull(actual);
    assertAsyncResults(asyncOp, result -> assertNull(result));
  }

  @Test
  public void test_check() {
    List<String> strList = new ArrayList<>(List.of("a", "a", "b", "c"));
    AsyncOperation<String> asyncOp =
        AsyncGraph.createImmediateAsync("a")
            .check(str -> strList.remove(str))
            .then(str -> str + "bc");

    String actual = AsyncGraph.getSync(asyncOp);

    assertThat(actual, is("abc"));
    assertThat(strList, is(List.of("a", "b", "c")));
    assertAsyncResults(
        asyncOp,
        result -> assertThat(actual, is("abc")),
        result -> assertThat(strList, is(List.of("b", "c"))));
  }

  @Test
  public void test_checkCollapse() {
    List<String> strList = new ArrayList<>(List.of("a", "a", "b", "c"));

    Function<String, AsyncOperation<Void>> fnReturnAsyncOp =
        str ->
            AsyncGraph.createAsync(
                () -> {
                  strList.remove(str);
                  return null;
                });

    AsyncOperation<String> asyncOp =
        AsyncGraph.createAsync(() -> "a").checkCollapse(str -> fnReturnAsyncOp.apply(str));

    String actual = AsyncGraph.getSync(asyncOp);

    assertThat(actual, is("a"));
    assertThat(strList, is(List.of("a", "b", "c")));
    assertAsyncResults(
        asyncOp,
        result -> assertThat(actual, is("a")),
        result -> assertThat(strList, is(List.of("b", "c"))));
  }

  @Test
  public void test_createCombinedAsync_computesCorrectValue_runsOnSeparateThreads() {
    long mainThreadId = getThreadId();
    long[] asyncThreadIds = new long[2];

    AsyncOperation<String> async1 =
        AsyncGraph.createImmediateAsync("a").check(unused -> asyncThreadIds[0] = getThreadId());
    AsyncOperation<Integer> async2 =
        AsyncGraph.createAsync(() -> 6).check(unused -> asyncThreadIds[1] = getThreadId());

    AsyncOperation<List<String>> asyncOp =
        AsyncGraph.createCombinedAsync(
            async1, async2, (str, num) -> List.of(str, Integer.toString(num)));

    List<String> actual = AsyncGraph.getSync(asyncOp);
    List<String> expected = List.of("a", "6");

    assertThat(actual, is(expected));
    assertThat(asyncThreadIds[0], not(mainThreadId));
    assertThat(asyncThreadIds[1], not(mainThreadId));
    assertThat(asyncThreadIds[0], not(asyncThreadIds[1]));
    assertAsyncResults(
        asyncOp,
        result -> assertThat(actual, is(expected)),
        result -> assertThat(asyncThreadIds[0], not(mainThreadId)),
        result -> assertThat(asyncThreadIds[1], not(mainThreadId)),
        result -> assertThat(asyncThreadIds[0], not(asyncThreadIds[1])));
  }

  @Test
  public void test_createCombinedAsync_secondFinishesFirst() {
    AsyncOperation<String> async1 =
        AsyncGraph.createAsync(
            () -> {
              Thread.sleep(2000);
              return "a";
            });
    AsyncOperation<Integer> async2 = AsyncGraph.createImmediateAsync(6);

    AsyncOperation<List<String>> asyncOp =
        AsyncGraph.createCombinedAsync(
            async1, async2, (str, num) -> List.of(str, Integer.toString(num)));

    List<String> actual = AsyncGraph.getSync(asyncOp);
    List<String> expected = List.of("a", "6");

    assertThat(actual, is(expected));
    assertAsyncResults(asyncOp, result -> assertThat(actual, is(expected)));
  }

  @Test
  public void test_createCombinedAsync3() {
    AsyncOperation<String> async1 =
        AsyncGraph.createAsync(
            () -> {
              Thread.sleep(1000);
              return "1";
            });
    AsyncOperation<Integer> async2 = AsyncGraph.createImmediateAsync(2);
    AsyncOperation<Integer> async3 = AsyncGraph.createImmediateAsync(3);

    AsyncOperation<List<Integer>> asyncOp =
        AsyncGraph.createCombinedAsync(
            async1, async2, async3, (str, n1, n2) -> List.of(Integer.parseInt(str), n1, n2));

    List<Integer> actual = AsyncGraph.getSync(asyncOp);
    List<Integer> expected = List.of(1, 2, 3);

    assertThat(actual, is(expected));
    assertAsyncResults(asyncOp, result -> assertThat(actual, is(expected)));
  }

  @SafeVarargs
  private static <T> void assertAsyncResults(AsyncOperation<T> asyncOp, Consumer<T>... assertions) {
    Wrapper<AssertionError> error = new Wrapper<>();

    AsyncGraph.runAsyncBlocking(
        asyncOp,
        actual -> {
          for (Consumer<T> assertion : assertions) {
            try {
              assertion.accept(actual);
            } catch (AssertionError e) {
              error.set(e);
              return;
            }
          }
        });

    if (error.get() != null) {
      throw new AssertionError(error.get());
    }
  }

  private static long getThreadId() {
    return Thread.currentThread().getId();
  }
}
