package ch.chassaing.fpjava;

import javaslang.Value;
import javaslang.collection.Iterator;
import javaslang.collection.Vector;
import javaslang.control.Option;
import javaslang.control.Try;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class Result<V> implements Value<V> {

  @SuppressWarnings("rawtypes")
  private final static Result EMPTY = new Empty();

  private Result() {
    // do not instantiate
  }

  @SuppressWarnings("unchecked")
  public static <V> Result<V> empty() { return EMPTY; }

  public static <V> Result<V> failure(Throwable throwable) {
    return new Failure<>(requireNonNull(throwable));
  }

  public static <V> Result<V> success(V value) {
    return new Success<>(requireNonNull(value));
  }

  public static <V> Result<V> ofOption(Option<V> anOption) {
    return requireNonNull(anOption)
            .map(Result::success)
            .getOrElse(empty());
  }

  public static <V> Result<V> ofTry(Try<V> aTry) {
    return requireNonNull(aTry)
            .map(Option::of)
            .map(Result::ofOption)
            .getOrElseGet(Result::failure);
  }

  public static <A,B> Function<Result<A>,Result<B>> lift(Function<A,B> f) {
    return a -> a.map(f);
  }

  public static <A,B> Function<Result<A>,Result<B>> liftChecked(Function<A,B> f) {
    return a -> {
      try {
        return a.map(f);
      } catch (Throwable t) {
        return failure(t);
      }
    };
  }

  public abstract <U> Result<U> flapMap(final Function<? super V, Result<U>> function);

  @Override
  public Iterator<V> iterator() {
    return toVector().iterator();
  }

  public abstract <U> Result<U> map(Function<? super V, ? extends U> function);

  public Result<V> orElse(Supplier<Result<V>> alternative) {
    return map(v -> this).getOrElse(alternative);
  }

  @Override
  public abstract Result<V> peek(Consumer<? super V> consumer);

  private static class Empty<V> extends Result<V> {

    @Override
    public V get() {
      throw new NoSuchElementException("getOrThrow() on empty Result called.");
    }

    @Override
    public V getOrElse(V alternative) {
      return alternative;
    }

    @Override
    public V getOrElse(Supplier<? extends V> alternative) {
      return alternative.get();
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public boolean isSingleValued() {
      return false;
    }

    @Override
    public Result<V> peek(Consumer<? super V> consumer) {
      return this;
    }

    @Override
    public String stringPrefix() {
      throw new UnsupportedOperationException("what's this?");
    }

    @Override
    public <U> Result<U> map(Function<? super V, ? extends U> function) {
      return empty();
    }

    @Override
    public <U> Result<U> flapMap(Function<? super V, Result<U>> function) {
      return empty();
    }

    @Override
    public Option<V> toOption() {
      return Option.none();
    }

    @Override
    public Try<V> toTry() {
      return Try.failure(new RuntimeException("empty result"));
    }

    @Override
    public Vector<V> toVector() {
      return Vector.empty();
    }
  }

  private final static class Failure<V> extends Empty<V> {

    private final Throwable throwable;

    private Failure(Throwable throwable) {
      this.throwable = throwable;
    }

    @Override
    public V get() {
      throw throwable instanceof RuntimeException ?
            (RuntimeException) throwable : new RuntimeException(throwable);
    }

    @Override
    public <U> Result<U> map(Function<? super V, ? extends U> function) {
      return new Failure<>(throwable);
    }

    @Override
    public <U> Result<U> flapMap(Function<? super V, Result<U>> function) {
      return new Failure<>(throwable);
    }

    @Override
    public Try<V> toTry() {
      return Try.failure(throwable);
    }
  }

  private final static class Success<V> extends Result<V> {

    private final V value;

    private Success(V value) {
      this.value = value;
    }

    @Override
    public V get() {
      return value;
    }

    @Override
    public V getOrElse(V alternative) {
      return value;
    }

    @Override
    public V getOrElse(Supplier<? extends V> alternative) {
      return value;
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public boolean isSingleValued() {
      return true;
    }

    @Override
    public <U> Result<U> map(Function<? super V, ? extends U> function) {
      return new Success<>(function.apply(value));
    }

    @Override
    public Result<V> peek(Consumer<? super V> consumer) {
      consumer.accept(value);
      return this;
    }

    @Override
    public String stringPrefix() {
      throw new UnsupportedOperationException("what's this?");
    }

    @Override
    public <U> Result<U> flapMap(Function<? super V, Result<U>> function) {
      return function.apply(value);
    }

    @Override
    public Vector<V> toVector() {
      return Vector.of(value);
    }
  }
}
