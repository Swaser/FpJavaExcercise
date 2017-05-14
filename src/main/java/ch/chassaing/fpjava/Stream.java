package ch.chassaing.fpjava;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static ch.chassaing.fpjava.TailCall.ret;
import static ch.chassaing.fpjava.TailCall.sus;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class Stream<A> {

  private static final Stream EMPTY = new Empty();

  public abstract A head();
  public abstract Stream<A> tail();
  public abstract boolean isEmpty();
  public abstract List<A> toList();

  private Stream() { /* don't instantiate */ }

  public Stream<A> take(int n) {
    return take_(this, List.empty(), n).eval();
  }

  private static <A> TailCall<Stream<A>> take_(Stream<A> stream, List<Supplier<A>> acc, int n) {
    return n == 0 || stream.isEmpty() ?
           ret(acc.foldLeft(empty(), (s,ele) -> cons(ele,s))) :
           sus(() -> take_(stream.tail(), acc.prepend(((Cons<A>)stream).getHead()), n - 1));
  }

  public <U> U foldLeft(U init, BiFunction<U, A, U> f) {
    return foldLeft_(this, init, f).eval();
  }

  private static <A,U> TailCall<U> foldLeft_(Stream<A> stream, U acc, BiFunction<U, A, U> f) {
    return stream.isEmpty() ?
           ret(acc) :
           sus(() -> foldLeft_(stream.tail(), f.apply(acc,stream.head()), f));
  }

  private static class Empty<A> extends Stream<A> {

    @Override
    public A head() {
      throw new IllegalStateException("head() of empty Stream called");
    }

    @Override
    public Stream<A> tail() {
      throw new IllegalStateException("tail() of empty Stream called");
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public List<A> toList() {
      return List.empty();
    }
  }

  private static class Cons<A> extends Stream<A> {

    private final Supplier<A> head;
    private final Supplier<Stream<A>> tail;

    public Cons(Supplier<A> head, Supplier<Stream<A>> tail) {
      this.head = head;
      this.tail = tail;
    }

    private Supplier<A> getHead() {
      return head;
    }

    @Override
    public A head() {
      return head.get();
    }

    @Override
    public Stream<A> tail() {
      return tail.get();
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public List<A> toList() {
      return foldLeft(List.<A>empty(), List::prepend).reverse();
    }
  }

  static <A> Stream<A> cons(Supplier<A> head, Supplier<Stream<A>> tail) {
    return new Cons<>(head, tail);
  }

  static <A> Stream<A> cons(Supplier<A> head, Stream<A> tail) {
    return new Cons<>(head, () -> tail);
  }

  @SuppressWarnings("unchecked")
  public static <A> Stream<A> empty() {
    return EMPTY;
  }

  public static Stream<Integer> from(int i) {
    return cons(() -> i, () -> from(i + 1));
  }

}
