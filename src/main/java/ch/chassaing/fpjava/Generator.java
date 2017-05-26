package ch.chassaing.fpjava;

import javaslang.Tuple2;

public class Generator {

  public static Tuple2<Integer, RNG> integer(RNG rng) {
    return rng.nextInt();
  }

  public static Tuple2<Integer, RNG> integer(RNG rng, int limit) {

    return rng.nextInt().map1(i -> Math.abs(i % limit));
  }
}
