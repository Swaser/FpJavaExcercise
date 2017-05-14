package ch.chassaing.fpjava;

import javaslang.Tuple;
import javaslang.Tuple2;

import java.util.function.BiFunction;
import java.util.function.Predicate;

import static ch.chassaing.fpjava.TailCall.ret;
import static ch.chassaing.fpjava.TailCall.sus;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class List<T> {

  @SuppressWarnings("rawtypes")
  public static final List NIL = new Nil();

  private List() {
    // do not instantiate
  }

  @SuppressWarnings("unchecked")
  public static <T> List<T> empty() { return NIL; }

  @SafeVarargs
  public static <T> List<T> of(T... ts) {
    List<T> list = empty();
    for (T t : ts) {
      list = list.prepend(t);
    }
    return list.reverse();
  }

  private static TailCall<Boolean> equals_(List<?> left, List<?> right) {
    if (left.isEmpty() && right.isEmpty())
      return ret(Boolean.TRUE);
    else if (left.isEmpty() || right.isEmpty())
      return ret(Boolean.FALSE);
    else
      return left.head().equals(right.head()) ?
             sus(() -> equals_(left.tail(), right.tail())) :
             ret(Boolean.FALSE);
  }

  private static <V> TailCall<List<V>> drop_(List<V> list, long n) {
    return n <= 0 || list.isEmpty() ?
           ret(list) :
           sus(() -> drop_(list.tail(), n - 1));
  }

  private static <V> TailCall<List<V>> dropWhile_(List<V> list, Predicate<V> predicate) {
    return list.isEmpty() ?
           ret(list) : predicate.test(list.head()) ?
                       sus(() -> dropWhile_(list.tail(), predicate)) :
                       ret(list);
  }

  private static <T, V> TailCall<V> foldLeft_(List<T> list, V acc, BiFunction<V, T, V> f) {
    return list.isEmpty() ?
           ret(acc) :
           sus(() -> foldLeft_(list.tail(), f.apply(acc, list.head()), f));
  }

  public static <T> List<T> flatten(List<List<T>> lists) {
    return lists.foldRight(List.empty(), List::prependAll);
  }

  public static <T> List<T> concat(List<T> left, List<T> right) {
    return left.appendAll(right);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (!(obj instanceof List)) return false;
    List other = (List) obj;
    if (size() != other.size()) return false;
    return equals_(this, other).eval();
  }

  /**
   * Complexity is O(n)
   */
  public List<T> append(T t) {
    return reverse().foldLeft(List.of(t), List::prepend);
  }

  public abstract T head();

  public abstract List<T> tail();

  public abstract boolean isEmpty();

  public abstract long size();

  public List<T> appendAll(List<T> other) {
    return other.isEmpty() ? this :
           this.isEmpty() ? other : foldRight(other, List::prepend);
  }

  public List<T> drop(long n) {
    return drop_(this, n).eval();
  }

  public List<T> dropWhile(Predicate<T> predicate) {
    return dropWhile_(this, predicate).eval();
  }

  public <V> List<V> flatMap(Function1<T, List<V>> f) {
    return foldRight(List.<V>empty(), (acc, e) -> acc.prependAll(f.apply(e)));
  }

  public List<T> filter(Predicate<T> predicate) {
    return foldRight(List.empty(), (acc, e) -> predicate.test(e) ? new Cons<>(e, acc) : acc);
  }

  /**
   * Complexity is O(N)
   */
  public <V> V foldLeft(V zero, BiFunction<V, T, V> f) {
    return foldLeft_(this, zero, f).eval();
  }

  /**
   * Complexity is O(N)
   */
  public <V> V foldRight(V zero, BiFunction<V, T, V> f) {
    return reverse().foldLeft(zero, f);
  }

  public String mkString(String sep) {
    String fill = isEmpty() ?
                  "" :
                  tail().foldLeft(new StringBuilder(head().toString()),
                                  (sb, e) -> sb.append(sep).append(e.toString())).toString();
    return String.format("[%s]", fill);
  }

  /**
   * Complexity is O(1)
   */
  public List<T> prepend(T t) {
    return new Cons<>(t, this);
  }

  /**
   * Complexity is O(n) where n is the number of elements in the other list
   */
  public List<T> prependAll(List<T> other) {
    return other.foldRight(this, List::prepend);
  }

  /**
   * Complexity is O(N)
   */
  public List<T> reverse() {
    return foldLeft(List.empty(), (list, elem) -> new Cons<>(elem, list));
  }

  public abstract List<T> setHead(T t);

  public abstract List<List<T>> splitAt(int index);

  @Override
  public String toString() {
    return mkString(",");
  }

  private static class Nil<A> extends List<A> {

    private Nil() {}

    @Override
    public A head() {
      throw new IllegalStateException("An empty list has no head");
    }

    @Override
    public List<A> tail() {
      throw new IllegalStateException("An empty list has no tail");
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public long size() {
      return 0;
    }

    @Override
    public List<A> setHead(A a) {
      throw new IllegalStateException("Try to set head of empty list");
    }

    @Override
    public List<List<A>> splitAt(int index) {
      return empty();
    }
  }

  private static class Cons<T> extends List<T> {

    private final T head;
    private final List<T> tail;
    private final long size;

    private Cons(T head, List<T> tail) {
      this.head = head;
      this.tail = tail;
      this.size = 1L + tail.size();
    }

    @Override
    public T head() {
      return head;
    }

    @Override
    public List<T> tail() {
      return tail;
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public long size() {
      return size;
    }

    @Override
    public List<T> setHead(T t) {
      return drop(1).prepend(t);
    }

    @Override
    public List<List<T>> splitAt(int index) {
      if (index >= size) return List.of(this);
      Tuple2<List<T>, List<T>> preSplit = splitAt_(Tuple.of(List.empty(), this), index).eval();
      return List.of(preSplit._1.reverse(), preSplit._2);
    }

    private static <T> TailCall<Tuple2<List<T>, List<T>>> splitAt_(Tuple2<List<T>, List<T>> acc, int index) {
      return index <= 0 || acc._2.isEmpty() ?
             ret(acc) :
             sus(() -> splitAt_(Tuple.of(acc._1.prepend(acc._2.head()), acc._2.tail()), index - 1));
    }
  }
}