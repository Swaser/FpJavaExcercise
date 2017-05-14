package ch.chassaing.fpjava;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StreamTest {
  @Test
  public void take() throws Exception {

    List<Integer> list = Stream.from(0)
                               .take(10)
                               .toList();


    assertEquals(10, list.size());
    assertEquals(0, (long)list.head());
    assertEquals(1, (long)list.tail().head());
  }

}