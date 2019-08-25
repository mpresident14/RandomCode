package crypto;

import org.junit.*;
import static org.junit.Assert.*;

public class OneTimePadBreakerTest {

  private static final String HEX1 = "af452521dc";
  private static final String HEX2 = "39204fadcbff35";

  private static final OneTimePadBreaker breaker = 
      new OneTimePadBreaker(new String[] {HEX1, HEX2});

  @Test
  public void test_xorCharIsValidLetter() {
    assertTrue(OneTimePadBreaker.xorCharIsValidLetter((byte) 0x2E, (byte) 'A'));
    assertFalse(OneTimePadBreaker.xorCharIsValidLetter((byte) 0xAE, (byte) 'A'));
  }

  @Test
  public void test_getXoredMessages() {
    byte[] actual = breaker.getXoredMessages(0).get(0);

    byte[] hex1Bytes = CryptoUtil.hexStringToByteArray(HEX1);
    byte[] hex2Bytes = CryptoUtil.hexStringToByteArray(HEX2);
    assertArrayEquals(actual, CryptoUtil.xorTruncate(hex1Bytes, hex2Bytes));
  }
}