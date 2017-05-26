package ch.chassaing.fpjava;

import org.junit.Test;

public class JavaRNGTest {
  @Test
  public void nextInt() throws Exception {

    RNG rng = JavaRNG.rng();

    System.out.println(rng.nextInt());
    System.out.println(rng.nextInt());
    System.out.println(rng.nextInt());

  }

}