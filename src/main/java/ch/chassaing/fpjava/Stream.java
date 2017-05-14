package ch.chassaing.fpjava;

import java.util.function.BiFunction;
import java.util.function.Predicate;
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

    public abstract Stream<A> take(int n);

    public abstract Stream<A> takeWhile(Predicate<? super A> cond);

    public abstract Stream<A> drop(int n);

    private Stream() { /* don't instantiate */ }

    public <U> U foldLeft(U init, BiFunction<U, A, U> f) {
        return foldLeft_(this, init, f).eval();
    }

    private static <A, U> TailCall<U> foldLeft_(Stream<A> stream, U acc, BiFunction<U, A, U> f) {
        return stream.isEmpty() ?
                ret(acc) :
                sus(() -> foldLeft_(stream.tail(), f.apply(acc, stream.head()), f));
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

        @Override
        public Stream<A> take(int n) {
            return this;
        }

        @Override
        public Stream<A> takeWhile(Predicate<? super A> cond) {
            return this;
        }

        @Override
        public Stream<A> drop(int n) {
            return this;
        }
    }

    private static class Cons<A> extends Stream<A> {

        private final Supplier<A> head;
        private final Supplier<Stream<A>> tail;

        public Cons(Supplier<A> head, Supplier<Stream<A>> tail) {
            this.head = head;
            this.tail = tail;
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

        @Override
        public Stream<A> take(int n) {
            return n <= 0 ?
                    empty() :
                    cons(head, () -> tail().take(n - 1));
        }

        @Override
        public Stream<A> takeWhile(Predicate<? super A> cond) {
            return cond.test(head()) ?
                    cons(head, () -> tail().takeWhile(cond)) :
                    empty();
        }

        @Override
        public Stream<A> drop(int n) {
            return drop_(this, n).eval();
        }

        private static <A> TailCall<Stream<A>> drop_(Stream<A> stream, int n) {
            return n <= 0 ?
                    ret(empty()) :
                    sus(() -> drop_(stream.tail(), n - 1));
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

    public static <A> Stream<A> ofList(List<A> as) {
        return as.isEmpty() ?
                empty() :
                cons(as::head, () -> ofList(as.tail()));
    }

}
