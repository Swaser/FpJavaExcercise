package ch.chassaing.fpjava;

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.Seq;
import javaslang.collection.Vector;

import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface Random<A> extends Function1<RNG, Tuple2<A, RNG>> {

  static <A> Random<A> unit(A a) {
    return rng -> Tuple.of(a, rng);
  }

  static <A> Random<Seq<A>> sequence(Seq<Random<A>> ras) {
    return rng -> ras.foldLeft(unit(Vector.empty()),
                               (Random<Seq<A>> rndSeq, Random<A> ra) -> rndSeq.combine(ra, Seq::append))
                     .apply(rng);
  }

  default <B> Random<B> map(Function<A, B> f) {
    return rng -> apply(rng).map1(f);
  }

  default <B, C> Random<C> combine(Random<B> rb, BiFunction<A, B, C> f) {
    return rng -> {
      Tuple2<A, RNG> ta = this.apply(rng);
      Tuple2<B, RNG> tb = rb.apply(ta._2);
      return Tuple.of(f.apply(ta._1, tb._1), tb._2);
    };
  }

  default <B> Random<B> flatMap(Function<A,Random<B>> f) {
    return rng -> {
      Tuple2<A, RNG> ta = this.apply(rng);
      return f.apply(ta._1).apply(ta._2);
    };
  }
}
