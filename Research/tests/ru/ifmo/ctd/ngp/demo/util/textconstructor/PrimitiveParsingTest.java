package ru.ifmo.ctd.ngp.demo.util.textconstructor;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Maxim Buzdalov
 */
public class PrimitiveParsingTest {
    enum TestEnum {
        FALSE, TRUE, UNDEFINED
    }

    private void parseFromInferredClass(String s, Object expected) {
        Object actual = TextConstructor.constructFromString(expected.getClass(), s);
        Assert.assertEquals("Inferred class is " + expected.getClass(), expected, actual);
    }

    @Test
    public void parseString() {
        parseFromInferredClass("value", "value");
    }

    @Test
    public void parseCharacter() {
        parseFromInferredClass("a", 'a');
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseCharacterFail1() {
        parseFromInferredClass("aa", 'a');
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseCharacterFail2() {
        parseFromInferredClass("", 'a');
    }

    @Test
    public void parseByte() {
        parseFromInferredClass("31", (byte) (31));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseByteFail() {
        parseFromInferredClass("128", (byte) (31));
    }

    @Test
    public void parseShort() {
        parseFromInferredClass("429", (short) (429));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseShortFail() {
        parseFromInferredClass("32768", (short) (429));
    }

    @Test
    public void parseInt() {
        parseFromInferredClass("1943124", 1943124);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseIntFail() {
        parseFromInferredClass("test", 1943124);
    }

    @Test
    public void parseLong() {
        parseFromInferredClass("1943124", 1943124L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseLongFail() {
        parseFromInferredClass("test", 1943124L);
    }

    @Test
    public void parseFloat() {
        parseFromInferredClass("1943124.0", 1943124.0f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseFloatFail() {
        parseFromInferredClass("3123,0", 3123.0f);
    }

    @Test
    public void parseDouble() {
        parseFromInferredClass("1943124.0", 1943124.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseDoubleFail() {
        parseFromInferredClass("3123,0", 3123.0);
    }

    @Test
    public void parseEnum() {
        parseFromInferredClass("FALSE", TestEnum.FALSE);
        parseFromInferredClass("TRUE", TestEnum.TRUE);
        parseFromInferredClass("UNDEFINED", TestEnum.UNDEFINED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseEnumFail() {
        parseFromInferredClass("false", TestEnum.FALSE);
    }
}
