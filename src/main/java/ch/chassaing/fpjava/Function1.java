package ch.chassaing.fpjava;

import java.util.function.Function;

@FunctionalInterface
public interface Function1<X,Y> extends Function<X,Y> {

  Y apply(X x);

  default Function0<Y> lazy(X x) {
    return () -> this.apply(x);
  }

  static <X,Y> Function1<X, Y> f1(Function1<X,Y> f) {
    return f;
  }

}
