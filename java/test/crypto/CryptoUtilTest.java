package crypto;

import static org.junit.Assert.*;

import org.junit.*;

public class CryptoUtilTest {

  private static final String ODD = "Odd";
  private static final String EVEN = "Even";

  private static final byte[] ODD_BYTES = new byte[] {(byte) 0x4f, (byte) 0x64, (byte) 0x64};
  private static final byte[] EVEN_BYTES =
      new byte[] {(byte) 0x45, (byte) 0x76, (byte) 0x65, (byte) 0x6e};
  private static final byte[] NEGATIVES_BYTES =
      new byte[] {(byte) 0xf5, (byte) 0xd6, (byte) 0x65, (byte) 0xae};
  private static final byte[] LEAD_ZERO_BYTES = new byte[] {(byte) 0x05, (byte) 0x1e, (byte) 0xd9};

  private static final String ODD_STR = "4f6464";
  private static final String EVEN_STR = "4576656e";
  private static final String NEGATIVES_STR = "f5d665ae";
  private static final String LEAD_ZERO_STR = "51ed9";

  @Test
  public void test_byteArrayToHexString() {
    assertEquals(ODD_STR, CryptoUtil.byteArrayToHexString(ODD_BYTES));
    assertEquals(EVEN_STR, CryptoUtil.byteArrayToHexString(EVEN_BYTES));
    assertEquals(NEGATIVES_STR, CryptoUtil.byteArrayToHexString(NEGATIVES_BYTES));
    assertEquals(LEAD_ZERO_STR, CryptoUtil.byteArrayToHexString(LEAD_ZERO_BYTES));
  }

  @Test
  public void test_hexStringToByteArray() {
    assertArrayEquals(ODD_BYTES, CryptoUtil.hexStringToByteArray(ODD_STR));
    assertArrayEquals(EVEN_BYTES, CryptoUtil.hexStringToByteArray(EVEN_STR));
    assertArrayEquals(NEGATIVES_BYTES, CryptoUtil.hexStringToByteArray(NEGATIVES_STR));
    assertArrayEquals(LEAD_ZERO_BYTES, CryptoUtil.hexStringToByteArray(LEAD_ZERO_STR));
  }

  @Test
  public void test_byteArrayToAsciiString() {
    assertEquals(ODD, CryptoUtil.byteArrayToAsciiString(ODD_BYTES));
    assertEquals(EVEN, CryptoUtil.byteArrayToAsciiString(EVEN_BYTES));
  }

  @Test
  public void test_asciiStringToByteArray() {
    assertArrayEquals(ODD_BYTES, CryptoUtil.asciiStringToByteArray(ODD));
    assertArrayEquals(EVEN_BYTES, CryptoUtil.asciiStringToByteArray(EVEN));
  }

  @Test
  public void test_xor() {
    byte[] actual = CryptoUtil.hexStringToByteArray("a12016e");
    assertArrayEquals(actual, CryptoUtil.xor(ODD_BYTES, EVEN_BYTES));

    actual = CryptoUtil.hexStringToByteArray("f0c8bcae");
    assertArrayEquals(actual, CryptoUtil.xor(NEGATIVES_BYTES, LEAD_ZERO_BYTES));
  }

  @Test
  public void test_xorTruncate() {
    byte[] actual = CryptoUtil.hexStringToByteArray("a1201");
    assertArrayEquals(actual, CryptoUtil.xorTruncate(ODD_BYTES, EVEN_BYTES));

    actual = CryptoUtil.hexStringToByteArray("f0c8bc");
    assertArrayEquals(actual, CryptoUtil.xorTruncate(NEGATIVES_BYTES, LEAD_ZERO_BYTES));
  }

  @Test
  public void test_printBytes() {
    CryptoUtil.printBytes(ODD_BYTES);
    CryptoUtil.printBytes(EVEN_BYTES);
    CryptoUtil.printBytes(NEGATIVES_BYTES);
  }
}
