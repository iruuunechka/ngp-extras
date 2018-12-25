package ru.ifmo.ctd.ngp.demo.util.textconstructor;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Maxim Buzdalov
 */
public class ParamDefParsingTest {
    static final class Test1 {
        public final int first;
        public final int second;
        public Test1(
                @ParamDef(name = "first") int first,
                @ParamDef(name = "second", value = "445") short second
        ) {
            this.first = first;
            this.second = second;
        }
    }

    static final class Test2 {
        public final int first;
        public final Test1 second;

        public Test2(
                @ParamDef(name = "first") int first,
                @ParamDef(name = "second") Test1 second) {
            this.first = first;
            this.second = second;
        }
    }

    @SuppressWarnings({"unused"})
    static final class Test3 {
        public final int first;
        public final long second;

        public Test3(@ParamDef(name = "first") int first) {
            this.first = first;
            this.second = 0;
        }

        public Test3(@ParamDef(name = "second") long second) {
            this.first = 0;
            this.second = second;
        }
    }
    
    @Test
    public void allSpecified() {
        Test1 value = TextConstructor.constructFromString(Test1.class,
                "ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDefParsingTest$Test1(first = 60, second=45)");
        Assert.assertEquals(60, value.first);
        Assert.assertEquals(45, value.second);
    }

    @Test
    public void useDefaultValue() {
        Test1 value = TextConstructor.constructFromString(Test1.class,
                "ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDefParsingTest$Test1(first = 60)");
        Assert.assertEquals(60, value.first);
        Assert.assertEquals(445, value.second);
    }
    
    @Test
    public void recursive() {
        Test2 v = TextConstructor.constructFromString(Test2.class,
                "ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDefParsingTest$Test2(second = " +
                        "ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDefParsingTest$Test1(first = 2, second = 3)" +
                        ", first = 1)");
        Assert.assertEquals(1, v.first);
        Assert.assertEquals(2, v.second.first);
        Assert.assertEquals(3, v.second.second);
    }

    @Test
    public void multipleConstructors() {
        Test3 v1 = TextConstructor.constructFromString(Test3.class,
                "ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDefParsingTest$Test3(first = 10)");
        Assert.assertEquals(10, v1.first);
        Assert.assertEquals(0, v1.second);

        Test3 v2 = TextConstructor.constructFromString(Test3.class,
                "ru.ifmo.ctd.ngp.demo.util.textconstructor.ParamDefParsingTest$Test3(second= 10)");
        Assert.assertEquals(10, v2.second);
        Assert.assertEquals(0, v2.first);
    }
}
