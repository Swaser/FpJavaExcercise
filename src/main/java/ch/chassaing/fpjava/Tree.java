package ch.chassaing.fpjava;

import java.util.Comparator;

@SuppressWarnings("unused")
public abstract class Tree<A> {

    public abstract A value();

    abstract Tree<A> left();

    abstract Tree<A> right();

    public abstract Tree<A> insert(A a);

    private static class Empty<A> extends Tree<A> {

        private final Comparator<A> comparator;

        private Empty(Comparator<A> comparator) {
            this.comparator = comparator;
        }

        @Override
        public A value() {
            throw new IllegalStateException("value() called on empty");
        }

        @Override
        Tree<A> left() {
            throw new IllegalStateException("value() called on empty");
        }

        @Override
        Tree<A> right() {
            throw new IllegalStateException("value() called on empty");
        }

        @Override
        public Tree<A> insert(A a) {
            return new Node<>(comparator, empty(comparator), a, empty(comparator));
        }
    }

    private static class Node<A> extends Tree<A> {

        private final Comparator<A> comparator;

        private final Tree<A> left;
        private final A value;
        private final Tree<A> right;

        private Node(Comparator<A> comparator, Tree<A> left, A value, Tree<A> right) {
            this.comparator = comparator;
            this.left = left;
            this.value = value;
            this.right = right;
        }

        @Override
        public A value() {
            return value;
        }

        @Override
        Tree<A> left() {
            return left;
        }

        @Override
        Tree<A> right() {
            return right;
        }

        @Override
        public Tree<A> insert(A a) {

            int compare = comparator.compare(a, value);
            return compare == 0 ?
                    new Node<>(comparator, left, a, right) :
                    compare < 0 ?
                            new Node<>(comparator, left.insert(a), value, right) :
                            new Node<>(comparator, left, value, right.insert(a));
        }


    }

    @SuppressWarnings("unchecked")
    public static <A> Tree<A> empty(Comparator<A> comparator) {
        return new Empty<>(comparator);
    }
}
