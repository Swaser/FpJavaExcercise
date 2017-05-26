package ch.chassaing.fpjava;

import javaslang.Tuple2;

public interface RNG {

  Tuple2<Integer, RNG> nextInt();
}
