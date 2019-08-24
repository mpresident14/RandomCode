package async;

import org.junit.*;
import static org.junit.Assert.*;

public class AsyncOperationTest {

  @Test
  public void test_createImmediateAsync() {
    AsyncOperation<Integer> asyncOp = 
        AsyncGraph
            .createImmediateAsync(8);

    assertResult(asyncOp, 8);
  }

  private static <T> void assertResult(AsyncOperation<T> asyncOp, T expected) {
    AsyncGraph.runAsync(asyncOp, actual -> assertEquals(expected, actual));
  }
}