package ch.chassaing.fpjava;

import javaslang.control.Option;

import java.util.Comparator;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class Tree<A> {

  public abstract A value();

  abstract Tree<A> left();

  abstract Tree<A> right();

  public abstract Tree<A> insert(A a);

  public abstract boolean member(A a);

  public abstract int size();

  public abstract int height();

  public abstract Option<A> max();

  public abstract Option<A> min();

  public abstract boolean isEmpty();

  public abstract Tree<A> remove(A a);

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

    @Override
    public boolean member(A a) {
      return false;
    }

    @Override
    public int size() {
      return 0;
    }

    @Override
    public int height() {
      return -1;
    }

    @Override
    public Option<A> max() {
      return Option.none();
    }

    @Override
    public Option<A> min() {
      return Option.none();
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public Tree<A> remove(A a) {
      return this;
    }
  }

  private static class Node<A> extends Tree<A> {

    private final Comparator<A> comparator;

    private final Tree<A> left;
    private final A value;
    private final Tree<A> right;
    private final int size;
    private final int height;

    private Node(Comparator<A> comparator, Tree<A> left, A value, Tree<A> right) {
      this.comparator = comparator;
      this.left = left;
      this.value = value;
      this.right = right;
      height = 1 + Math.max(left.height(), right.height());
      size = 1 + left.size() + right.size();
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
      return compare < 0
             ? new Node<>(comparator, left.insert(a), value, right)
             : compare > 0
               ? new Node<>(comparator, left, value, right.insert(a))
               : new Node<>(comparator, left, a, right);
    }

    @Override
    public boolean member(A a) {
      int compare = comparator.compare(a, value);
      return compare < 0
             ? left.member(a)
             : compare == 0 || right.member(a);
    }

    @Override
    public int size() {
      return size;
    }

    @Override
    public int height() {
      return height;
    }

    @Override
    public Option<A> max() {
      return right.max().orElse(() -> Option.of(value));
    }

    @Override
    public Option<A> min() {
      return left.min().orElse(() -> Option.of(value));
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public Tree<A> remove(A a) {
      int compare = comparator.compare(a, value);
      return compare < 0
             ? new Node<>(comparator, left.remove(a), value, right)
             : compare > 0
               ? new Node<>(comparator, left, value, right.remove(a))
               : merge(left, right);
    }

    private Tree<A> merge(Tree<A> left, Tree<A> right) {
      return left.isEmpty()
             ? right
             : right.isEmpty()
               ? left
               : new Node<>(comparator, left, right.value(), merge(right.left(), right.right()));
    }
  }

  @SuppressWarnings("unchecked")
  public static <A> Tree<A> empty(Comparator<A> comparator) {
    return new Empty<>(comparator);
  }

  @SafeVarargs
  public static <A> Tree<A> of(Comparator<A> comparator, A... as) {
    return List.of(as)
               .foldLeft(empty(comparator), Tree::insert);
  }
}
