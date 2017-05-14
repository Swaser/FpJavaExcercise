package ch.chassaing.fpjava;

import java.util.function.Supplier;

@FunctionalInterface
public interface Function0<T> extends Supplier<T> {

  T apply();

  @Override
  default T get() {
    return apply();
  }

  static <T> Function0<T> f0(Function0<T> f) {
    return f;
  }
}
