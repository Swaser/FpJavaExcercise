package ch.chassaing.fpjava;

import org.junit.Test;

import static org.junit.Assert.*;

public class ListTest {

  @Test
  public void drop() throws Exception {
    assertEquals(List.of(3, 4), List.of(1, 2, 3, 4).drop(2));
    assertEquals(List.empty(), List.of(1, 2, 3, 4).drop(6));
  }

  @Test
  public void dropWhile() throws Exception {
    assertEquals(List.of(3, 4), List.of(1, 2, 3, 4).dropWhile(i -> i <= 2));
    assertEquals(List.empty(), List.of(1, 2, 3, 4).dropWhile(i -> i < 10));
  }

  @Test
  public void reverse() throws Exception {
    assertEquals(List.of(4,3,2,1), List.of(1, 2, 3, 4).reverse());
  }
}