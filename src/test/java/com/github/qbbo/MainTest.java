package com.github.qbbo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MainTest {
    @Test
    public void testNewMainSayHelloWorld() {
        Assertions.assertEquals("Hello, World!", new Main().sayHelloWorld());
    }
}
