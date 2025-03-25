package test.base;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.value.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

public class RefTest {

    @Test
    public void testVal() {
        assertSame(Val.ofNull(), Val.ofNull());
        Object o = new Object();
        assertSame(Val.of(o).get(), o);
        assertSame(BooleanVal.ofTrue(), BooleanVal.ofTrue());
        assertSame(BooleanVal.ofFalse(), BooleanVal.ofFalse());
        assertEquals(BooleanVal.of(true).get(), true);
        assertEquals(BooleanVal.of(true).toVal().get(), Boolean.TRUE);
        assertSame(ByteVal.ofZero(), ByteVal.ofZero());
        assertEquals(ByteVal.of(1).get(), 1);
        assertEquals(ByteVal.of(1).toVal().get(), Byte.valueOf((byte) 1));
        assertSame(CharVal.ofZero(), CharVal.ofZero());
        assertEquals(CharVal.of(1).get(), 1);
        assertEquals(CharVal.of(1).toVal().get(), Character.valueOf((char) 1));
        assertSame(ShortVal.ofZero(), ShortVal.ofZero());
        assertEquals(ShortVal.of(1).get(), 1);
        assertEquals(ShortVal.of(1).toVal().get(), Short.valueOf((short) 1));
        assertSame(IntVal.ofZero(), IntVal.ofZero());
        assertEquals(IntVal.of(1).get(), 1);
        assertEquals(IntVal.of(1).toVal().get(), Integer.valueOf(1));
        assertSame(LongVal.ofZero(), LongVal.ofZero());
        assertEquals(LongVal.of(1).get(), 1);
        assertEquals(LongVal.of(1).toVal().get(), Long.valueOf(1));
        assertSame(FloatVal.ofZero(), FloatVal.ofZero());
        assertEquals(FloatVal.of(1.0).get(), 1);
        assertEquals(FloatVal.of(1).toVal().get(), Float.valueOf(1));
        assertSame(DoubleVal.ofZero(), DoubleVal.ofZero());
        assertEquals(DoubleVal.of(1).get(), 1);
        assertEquals(DoubleVal.of(1).toVal().get(), Double.valueOf(1));
    }

    @Test
    public void testVar() {
        Object o = new Object();
        assertSame(Var.of(o).get(), o);
        Var<Integer> vi = Var.of(1);
        assertEquals(vi.get(), 1);
        Var<String> vs = vi.set("2");
        assertSame(vs, vi);
        assertEquals(vs.get(), "2");

        assertEquals(BooleanVar.of(true).get(), true);
        assertEquals(BooleanVar.of(true).set(false).get(), false);
        assertEquals(BooleanVar.of(true).toggle().get(), false);
        assertEquals(BooleanVar.of(true).toggleAndGet(), false);
        BooleanVar bool = BooleanVar.of(true);
        assertEquals(bool.getAndToggle(), true);
        assertEquals(bool.get(), false);
        assertEquals(BooleanVar.of(true).toVar().get(), Boolean.TRUE);

        assertEquals(ByteVar.of(1).get(), 1);
        assertEquals(ByteVar.of(1).set(2).get(), 2);
        assertEquals(ByteVar.of(1).add(1).get(), 2);
        assertEquals(ByteVar.of(1).getAndIncrement(), 1);
        assertEquals(ByteVar.of(1).incrementAndGet(), 2);
        ByteVar bv = ByteVar.of(1);
        assertEquals(bv.getAndIncrement(), 1);
        assertEquals(bv.get(), 2);
        assertEquals(ByteVar.of(1).toVar().get(), Byte.valueOf((byte) 1));

        assertEquals(CharVar.of(1).get(), 1);
        assertEquals(CharVar.of(1).set(2).get(), 2);
        assertEquals(CharVar.of(1).add(1).get(), 2);
        assertEquals(CharVar.of(1).getAndIncrement(), 1);
        assertEquals(CharVar.of(1).incrementAndGet(), 2);
        CharVar cv = CharVar.of(1);
        assertEquals(cv.getAndIncrement(), 1);
        assertEquals(cv.get(), 2);
        assertEquals(CharVar.of(1).toVar().get(), Character.valueOf((char) 1));

        assertEquals(ShortVar.of(1).get(), 1);
        assertEquals(ShortVar.of(1).set(2).get(), 2);
        assertEquals(ShortVar.of(1).add(1).get(), 2);
        assertEquals(ShortVar.of(1).getAndIncrement(), 1);
        assertEquals(ShortVar.of(1).incrementAndGet(), 2);
        ShortVar sv = ShortVar.of(1);
        assertEquals(sv.getAndIncrement(), 1);
        assertEquals(sv.get(), 2);
        assertEquals(ShortVar.of(1).toVar().get(), Short.valueOf((short) 1));

        assertEquals(IntVar.of(1).get(), 1);
        assertEquals(IntVar.of(1).set(2).get(), 2);
        assertEquals(IntVar.of(1).add(1).get(), 2);
        assertEquals(IntVar.of(1).getAndIncrement(), 1);
        assertEquals(IntVar.of(1).incrementAndGet(), 2);
        IntVar iv = IntVar.of(1);
        assertEquals(iv.getAndIncrement(), 1);
        assertEquals(iv.get(), 2);
        assertEquals(IntVar.of(1).toVar().get(), Integer.valueOf(1));

        assertEquals(LongVar.of(1).get(), 1);
        assertEquals(LongVar.of(1).set(2).get(), 2);
        assertEquals(LongVar.of(1).add(1).get(), 2);
        assertEquals(LongVar.of(1).getAndIncrement(), 1);
        assertEquals(LongVar.of(1).incrementAndGet(), 2);
        LongVar lv = LongVar.of(1);
        assertEquals(lv.getAndIncrement(), 1);
        assertEquals(lv.get(), 2);
        assertEquals(LongVar.of(1).toVar().get(), Long.valueOf(1));

        assertEquals(FloatVar.of(1).get(), 1);
        assertEquals(FloatVar.of(1).set(2).get(), 2);
        assertEquals(FloatVar.of(1).add(1).get(), 2);
        assertEquals(FloatVar.of(1).toVar().get(), Float.valueOf(1));
        assertEquals(FloatVar.of(1.0).get(), 1);
        assertEquals(FloatVar.of(1.0).set(2.0).get(), 2);
        assertEquals(FloatVar.of(1.0).add(1.0).get(), 2);

        assertEquals(DoubleVar.of(1).get(), 1);
        assertEquals(DoubleVar.of(1).set(2).get(), 2);
        assertEquals(DoubleVar.of(1).add(1).get(), 2);
        assertEquals(DoubleVar.of(1).toVar().get(), Double.valueOf(1));
    }
}
