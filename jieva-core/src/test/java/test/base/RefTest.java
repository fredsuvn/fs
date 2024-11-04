package test.base;

import org.testng.annotations.Test;
import xyz.sunqian.common.ref.*;

import static org.testng.Assert.*;

public class RefTest {

    @Test
    public void testVal() {
        assertSame(Val.ofNull(), Val.ofNull());
        Object o = new Object();
        assertSame(Val.of(o).get(), o);
        assertSame(BooleanVal.ofFalse(), BooleanVal.ofFalse());
        assertEquals(BooleanVal.of(true).get(), true);
        assertEquals(BooleanVal.of(true).toWrapper().get(), Boolean.TRUE);
        assertSame(ByteVal.ofZero(), ByteVal.ofZero());
        assertEquals(ByteVal.of(1).get(), 1);
        assertEquals(ByteVal.of(1).toWrapper().get(), Byte.valueOf((byte) 1));
        assertSame(CharVal.ofZero(), CharVal.ofZero());
        assertEquals(CharVal.of(1).get(), 1);
        assertEquals(CharVal.of(1).toWrapper().get(), Character.valueOf((char) 1));
        assertSame(ShortVal.ofZero(), ShortVal.ofZero());
        assertEquals(ShortVal.of(1).get(), 1);
        assertEquals(ShortVal.of(1).toWrapper().get(), Short.valueOf((short) 1));
        assertSame(IntVal.ofZero(), IntVal.ofZero());
        assertEquals(IntVal.of(1).get(), 1);
        assertEquals(IntVal.of(1).toWrapper().get(), Integer.valueOf(1));
        assertSame(LongVal.ofZero(), LongVal.ofZero());
        assertEquals(LongVal.of(1).get(), 1);
        assertEquals(LongVal.of(1).toWrapper().get(), Long.valueOf(1));
        assertSame(FloatVal.ofZero(), FloatVal.ofZero());
        assertEquals(FloatVal.of(1).get(), 1);
        assertEquals(FloatVal.of(1).toWrapper().get(), Float.valueOf(1));
        assertSame(DoubleVal.ofZero(), DoubleVal.ofZero());
        assertEquals(DoubleVal.of(1).get(), 1);
        assertEquals(DoubleVal.of(1).toWrapper().get(), Double.valueOf(1));
    }

    @Test
    public void testVar() {
        assertNotEquals(Var.ofNull(), Var.ofNull());
        Object o = new Object();
        assertSame(Var.of(o).get(), o);

        assertNotEquals(BooleanVar.ofFalse(), BooleanVar.ofFalse());
        assertEquals(BooleanVar.of(true).get(), true);
        assertEquals(BooleanVar.of(true).set(false).get(), false);
        assertEquals(BooleanVar.of(true).toggleAndGet(), false);
        BooleanVar bool = BooleanVar.of(true);
        assertEquals(bool.getAndToggle(), true);
        assertEquals(bool.get(), false);
        assertEquals(BooleanVar.of(true).toWrapper().get(), Boolean.TRUE);

        assertNotEquals(ByteVar.ofZero(), ByteVar.ofZero());
        assertEquals(ByteVar.of(1).get(), 1);
        assertEquals(ByteVar.of(1).set(2).get(), 2);
        assertEquals(ByteVar.of(1).add(1).get(), 2);
        assertEquals(ByteVar.of(1).getAndIncrement(), 1);
        assertEquals(ByteVar.of(1).incrementAndGet(), 2);
        ByteVar bv = ByteVar.of(1);
        assertEquals(bv.getAndIncrement(), 1);
        assertEquals(bv.get(), 2);
        assertEquals(ByteVar.of(1).toWrapper().get(), Byte.valueOf((byte) 1));

        assertNotEquals(CharVar.ofZero(), CharVar.ofZero());
        assertEquals(CharVar.of(1).get(), 1);
        assertEquals(CharVar.of(1).set(2).get(), 2);
        assertEquals(CharVar.of(1).add(1).get(), 2);
        assertEquals(CharVar.of(1).getAndIncrement(), 1);
        assertEquals(CharVar.of(1).incrementAndGet(), 2);
        CharVar cv = CharVar.of(1);
        assertEquals(cv.getAndIncrement(), 1);
        assertEquals(cv.get(), 2);
        assertEquals(CharVar.of(1).toWrapper().get(), Character.valueOf((char) 1));

        assertNotEquals(ShortVar.ofZero(), ShortVar.ofZero());
        assertEquals(ShortVar.of(1).get(), 1);
        assertEquals(ShortVar.of(1).set(2).get(), 2);
        assertEquals(ShortVar.of(1).add(1).get(), 2);
        assertEquals(ShortVar.of(1).getAndIncrement(), 1);
        assertEquals(ShortVar.of(1).incrementAndGet(), 2);
        ShortVar sv = ShortVar.of(1);
        assertEquals(sv.getAndIncrement(), 1);
        assertEquals(sv.get(), 2);
        assertEquals(ShortVar.of(1).toWrapper().get(), Short.valueOf((short) 1));

        assertNotEquals(IntVar.ofZero(), IntVar.ofZero());
        assertEquals(IntVar.of(1).get(), 1);
        assertEquals(IntVar.of(1).set(2).get(), 2);
        assertEquals(IntVar.of(1).add(1).get(), 2);
        assertEquals(IntVar.of(1).getAndIncrement(), 1);
        assertEquals(IntVar.of(1).incrementAndGet(), 2);
        IntVar iv = IntVar.of(1);
        assertEquals(iv.getAndIncrement(), 1);
        assertEquals(iv.get(), 2);
        assertEquals(IntVar.of(1).toWrapper().get(), Integer.valueOf(1));

        assertNotEquals(LongVar.ofZero(), LongVar.ofZero());
        assertEquals(LongVar.of(1).get(), 1);
        assertEquals(LongVar.of(1).set(2).get(), 2);
        assertEquals(LongVar.of(1).add(1).get(), 2);
        assertEquals(LongVar.of(1).getAndIncrement(), 1);
        assertEquals(LongVar.of(1).incrementAndGet(), 2);
        LongVar lv = LongVar.of(1);
        assertEquals(lv.getAndIncrement(), 1);
        assertEquals(lv.get(), 2);
        assertEquals(LongVar.of(1).toWrapper().get(), Long.valueOf(1));

        assertNotEquals(FloatVar.ofZero(), FloatVar.ofZero());
        assertEquals(FloatVar.of(1).get(), 1);
        assertEquals(FloatVar.of(1).set(2).get(), 2);
        assertEquals(FloatVar.of(1).add(1).get(), 2);
        assertEquals(FloatVar.of(1).toWrapper().get(), Float.valueOf(1));

        assertNotEquals(DoubleVar.ofZero(), DoubleVar.ofZero());
        assertEquals(DoubleVar.of(1).get(), 1);
        assertEquals(DoubleVar.of(1).set(2).get(), 2);
        assertEquals(DoubleVar.of(1).add(1).get(), 2);
        assertEquals(DoubleVar.of(1).toWrapper().get(), Double.valueOf(1));
    }
}
