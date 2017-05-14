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
        assertEquals(0, (long) list.head());
        assertEquals(1, (long) list.tail().head());
    }

    @Test
    public void takeWhile() throws Exception {

        List<Integer> list = Stream.from(1)
                                          .takeWhile(i -> i < 11)
                                          .toList();

        assertEquals(10, list.size());
        assertEquals(1, (long) list.head());
        assertEquals(2, (long) list.tail().head());
    }
}