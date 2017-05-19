package ch.chassaing.fpjava;

import java.util.function.Supplier;

public class Lazy<V> {

    private V value;
    private final Supplier<V> supplier;

    public Lazy(Supplier<V> supplier) {
        this.supplier = supplier;
    }

    public V get() {
        if (value == null) {
            value = supplier.get();
        }
        return value;
    }

    public synchronized V getSynchronized() {
        return get();
    }
}
