package ch.chassaing.fpjava;

import java.util.function.BiFunction;

@FunctionalInterface
public interface Function2<X,Y,Z> extends BiFunction<X,Y,Z> {

  Z apply(X x, Y y);

  static <X,Y,Z> Function2<X,Y,Z> f2(BiFunction<X, Y, Z> f) {
    return f::apply;
  }

  static <X,Y,Z> Function2<Result<X>, Result<Y>, Result<Z>> lift2(Function2<X, Y, Z> f) {
    return (x, y) -> x.flapMap(x_ -> y.map(y_ -> f.apply(x_, y_)));
  }

  default Function1<Y,Z> apply(X x) {
    return y -> this.apply(x, y);
  }

  default Function0<Z> lazy(X x, Y y) {
    return () -> this.apply(x, y);
  }

  default Function1<X, Function1<Y, Z>> curried() {
    return x -> y -> this.apply(x, y);
  }

  default Function2<Y,X,Z> reversed() {
    return (y,x) -> this.apply(x,y);
  }
}
