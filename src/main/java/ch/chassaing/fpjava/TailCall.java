package ch.chassaing.fpjava;

import java.util.function.Supplier;

@SuppressWarnings("WeakerAccess")
public abstract class TailCall<V> {

  public abstract TailCall<V> resume();
  public abstract V eval();
  public abstract boolean isSuspend();

  private TailCall() {
    // do not instantiate
  }

  public static <V> TailCall<V> ret(V value) {
    return new Return<>(value);
  }

  public static <V> TailCall<V> sus(Supplier<TailCall<V>> supplier) {
    return new Suspend<>(supplier);
  }

  private static class Return<V> extends TailCall<V> {

    private final V value;

    private Return(V value) {
      this.value = value;
    }

    @Override
    public TailCall<V> resume() {
      throw new IllegalStateException("Return has no resume");
    }

    @Override
    public V eval() {
      return value;
    }

    @Override
    public boolean isSuspend() {
      return false;
    }
  }

  private static class Suspend<V> extends TailCall<V> {

    private final Supplier<TailCall<V>> resume;

    private Suspend(Supplier<TailCall<V>> resume) {this.resume = resume;}

    @Override
    public TailCall<V> resume() {
      return resume.get();
    }

    @Override
    public V eval() {
      TailCall<V> tailRec = this;
      while (tailRec.isSuspend()) {
        tailRec = tailRec.resume();
      }
      return tailRec.eval();
    }

    @Override
    public boolean isSuspend() {
      return true;
    }
  }

}
