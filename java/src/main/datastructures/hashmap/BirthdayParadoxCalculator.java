package datastructures.hashmap;

import java.math.BigDecimal;
import java.math.MathContext;

/** 
 * Realized testing my HashMap did not account for when random.nextInt() 
 * produced a repeat value. What are the chances of a repeat value with
 * 15 repetitions of 10000 insertions? (nextInt() has 2^32 possible values)
 * */ 
 
public class BirthdayParadoxCalculator {
  private BigDecimal n;
  private BigDecimal k;

  public BirthdayParadoxCalculator(String n, String k) {
    this.n = new BigDecimal(n);
    this.k = new BigDecimal(k);
  }

  private BigDecimal probOfRepeat() {
    BigDecimal result = BigDecimal.ONE;
    BigDecimal start = n.subtract(this.k).add(BigDecimal.ONE);
    for (BigDecimal i = start; i.compareTo(n) < 1; i = i.add(BigDecimal.ONE)) {
      BigDecimal quotient = i.divide(this.n, MathContext.DECIMAL128);
      result = result.multiply(quotient, MathContext.DECIMAL128);
    }
    return BigDecimal.ONE.subtract(result);
  }

  public static void main(String[] args) {
    BirthdayParadoxCalculator calculator = 
        new BirthdayParadoxCalculator("4294967296" /* 2^32 */, "10000");
    double probOfNoRepeat = 1.0 - calculator.probOfRepeat().doubleValue();
    int numTrials = 15;
    double probOfNoRepeatXTimes = Math.pow(probOfNoRepeat, numTrials);
    System.out.println(probOfNoRepeatXTimes);
  }
}