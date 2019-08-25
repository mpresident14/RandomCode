package async;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class AsyncOperationTest {

  @Test
  public void test_createImmediateAsync() {
    Integer expected = 8;

    AsyncOperation<Integer> asyncOp = 
        AsyncGraph
            .createImmediateAsync(expected);

    assertResult(
        asyncOp, 
        actual -> assertEquals(actual, expected));
  }

  @Test
  public void test_createAsync() {
    String expected = "string";
    AsyncOperation<String> asyncOp = 
        AsyncGraph
            .createAsync(() -> expected);

    assertResult(
        asyncOp,
        actual -> assertEquals(actual, expected));
  }

  @Test
  public void test_then() {
    AsyncOperation<Integer> asyncOp = 
        AsyncGraph
            .createImmediateAsync(5)
            .then(num -> num + 1);

    assertResult(
        asyncOp, 
        actual -> assertEquals(actual, (Integer) 6));
  }

  @Test
  public void test_thenCollapse() {
    String string = "Hello";
    Function<String, AsyncOperation<Integer>> fnReturnAsyncOp =
        str -> 
            AsyncGraph
                .createImmediateAsync(str)
                .then(String::length);

    AsyncOperation<Integer> asyncOp = 
      AsyncGraph
          .createAsync(() -> string)
          .thenCollapse(str -> fnReturnAsyncOp.apply(str));

    assertResult(
        asyncOp, 
        actual -> assertEquals((int) actual, string.length()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void test_peek() {
    List<String> strList = new ArrayList<>(Arrays.asList("a", "b", "c"));
    AsyncOperation<String> asyncOp = 
        AsyncGraph
            .createImmediateAsync("a")
            .peek(str -> strList.remove(str))
            .then(str -> str + "bc");

    assertResults(
        asyncOp,
        Arrays.asList( 
            actual -> assertArrayEquals(new String[] {"b", "c"}, strList.toArray(new String[2])),
            actual -> assertEquals(actual, "abc")));
  }

  private static <T> void assertResult(AsyncOperation<T> asyncOp, Consumer<T> assertion) {
    assertResults(asyncOp, Arrays.asList(assertion));
  }

  private static <T> void assertResults(AsyncOperation<T> asyncOp, List<Consumer<T>> assertions) {
    AsyncGraph.runAsync(
      asyncOp, 
      actual -> 
          {
              for (Consumer<T> assertion : assertions) {
                assertion.accept(actual);
              }
          });
  }
}