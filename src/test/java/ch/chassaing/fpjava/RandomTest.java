package ch.chassaing.fpjava;

import org.junit.Test;

import static org.junit.Assert.*;

public class RandomTest {

  private static class Dot {
    private final int x;
    private final int y;

    public Dot(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
    }

    @Override
    public String toString() {
      return "Dot{" +
             "x=" + x +
             ", y=" + y +
             '}';
    }
  }

  @Test
  public void testBoolean() {

    Random<Integer> rngInt = RNG::nextInt;
    Random<Boolean> rngBool = rngInt.map(i -> i % 2 == 0);

    assertTrue(rngBool.apply(JavaRNG.rng(0L))._1);


    Random<Dot> dotRandom = rngInt.flatMap(x -> rngInt.map(y -> new Dot(x, y)));
    System.out.println(dotRandom.apply(JavaRNG.rng(0L)));


  }

}