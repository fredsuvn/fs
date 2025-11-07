package tests.base.value;

import org.junit.jupiter.api.Test;
import space.sunqian.common.base.value.BooleanVal;
import space.sunqian.common.base.value.BooleanVar;
import space.sunqian.common.base.value.ByteVal;
import space.sunqian.common.base.value.ByteVar;
import space.sunqian.common.base.value.CharVal;
import space.sunqian.common.base.value.CharVar;
import space.sunqian.common.base.value.DoubleVal;
import space.sunqian.common.base.value.DoubleVar;
import space.sunqian.common.base.value.FloatVal;
import space.sunqian.common.base.value.FloatVar;
import space.sunqian.common.base.value.IntVal;
import space.sunqian.common.base.value.IntVar;
import space.sunqian.common.base.value.LongSpan;
import space.sunqian.common.base.value.LongVal;
import space.sunqian.common.base.value.LongVar;
import space.sunqian.common.base.value.ShortVal;
import space.sunqian.common.base.value.ShortVar;
import space.sunqian.common.base.value.Span;
import space.sunqian.common.base.value.Val;
import space.sunqian.common.base.value.Var;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValueTest {

    @Test
    public void testVal() {
        {
            // object
            assertSame(Val.ofNull(), Val.ofNull());
            assertSame(Val.ofNull(), Val.of(null));
            Object o = new Object();
            assertSame(Val.of(o).get(), o);
        }
        {
            // boolean
            assertSame(BooleanVal.ofTrue(), BooleanVal.ofTrue());
            assertSame(BooleanVal.ofFalse(), BooleanVal.ofFalse());
            assertEquals(true, BooleanVal.of(true).get());
            assertEquals(Boolean.TRUE, BooleanVal.of(true).toVal().get());
        }
        {
            // byte
            assertSame(ByteVal.ofZero(), ByteVal.ofZero());
            assertEquals(1, ByteVal.of(1).get());
            assertEquals(ByteVal.of(1).toVal().get(), Byte.valueOf((byte) 1));
        }
        {
            // char
            assertSame(CharVal.ofZero(), CharVal.ofZero());
            assertEquals(1, CharVal.of(1).get());
            assertEquals(CharVal.of(1).toVal().get(), Character.valueOf((char) 1));
        }
        {
            // short
            assertSame(ShortVal.ofZero(), ShortVal.ofZero());
            assertEquals(1, ShortVal.of(1).get());
            assertEquals(ShortVal.of(1).toVal().get(), Short.valueOf((short) 1));
        }
        {
            // int
            assertSame(IntVal.ofZero(), IntVal.ofZero());
            assertEquals(1, IntVal.of(1).get());
            assertEquals(IntVal.of(1).toVal().get(), Integer.valueOf(1));
        }
        {
            // long
            assertSame(LongVal.ofZero(), LongVal.ofZero());
            assertEquals(1, LongVal.of(1).get());
            assertEquals(LongVal.of(1).toVal().get(), Long.valueOf(1));
        }
        {
            // float
            assertSame(FloatVal.ofZero(), FloatVal.ofZero());
            assertEquals(1, FloatVal.of(1.0).get());
            assertEquals(FloatVal.of(1).toVal().get(), Float.valueOf(1));
        }
        {
            // double
            assertSame(DoubleVal.ofZero(), DoubleVal.ofZero());
            assertEquals(1, DoubleVal.of(1).get());
            assertEquals(DoubleVal.of(1).toVal().get(), Double.valueOf(1));
        }
    }

    @Test
    public void testVar() {
        {
            // object
            Object o = new Object();
            assertSame(Var.of(o).get(), o);
            Var<Integer> vi = Var.of(1);
            assertEquals(1, vi.get());
            Var<Integer> v2 = vi.set(2);
            assertSame(v2, vi);
            assertEquals(2, v2.get());
            assertNull(v2.clear().get());
        }
        {
            // boolean
            assertEquals(true, BooleanVar.of(true).get());
            assertEquals(false, BooleanVar.of(true).set(false).get());
            assertEquals(false, BooleanVar.of(true).toggle().get());
            assertEquals(true, BooleanVar.of(false).toggle().get());
            assertEquals(false, BooleanVar.of(true).toggleAndGet());
            BooleanVar bool = BooleanVar.of(true);
            assertEquals(true, bool.getAndToggle());
            assertEquals(false, bool.get());
            assertEquals(Boolean.TRUE, BooleanVar.of(true).toVar().get());
            assertFalse(BooleanVar.of(true).clear().get());
        }
        {
            // byte
            assertEquals(1, ByteVar.of(1).get());
            assertEquals(2, ByteVar.of(1).set(2).get());
            assertEquals(2, ByteVar.of(1).add(1).get());
            assertEquals(1, ByteVar.of(1).getAndIncrement());
            assertEquals(2, ByteVar.of(1).incrementAndGet());
            ByteVar bv = ByteVar.of(1);
            assertEquals(1, bv.getAndIncrement());
            assertEquals(2, bv.get());
            assertEquals(ByteVar.of(1).toVar().get(), Byte.valueOf((byte) 1));
            assertEquals(0, ByteVar.of(111).clear().get());
        }
        {
            // char
            assertEquals(1, CharVar.of(1).get());
            assertEquals(2, CharVar.of(1).set(2).get());
            assertEquals(2, CharVar.of(1).add(1).get());
            assertEquals(1, CharVar.of(1).getAndIncrement());
            assertEquals(2, CharVar.of(1).incrementAndGet());
            CharVar cv = CharVar.of(1);
            assertEquals(1, cv.getAndIncrement());
            assertEquals(2, cv.get());
            assertEquals(CharVar.of(1).toVar().get(), Character.valueOf((char) 1));
            assertEquals(0, CharVar.of(111).clear().get());
        }
        {
            // short
            assertEquals(1, ShortVar.of(1).get());
            assertEquals(2, ShortVar.of(1).set(2).get());
            assertEquals(2, ShortVar.of(1).add(1).get());
            assertEquals(1, ShortVar.of(1).getAndIncrement());
            assertEquals(2, ShortVar.of(1).incrementAndGet());
            ShortVar sv = ShortVar.of(1);
            assertEquals(1, sv.getAndIncrement());
            assertEquals(2, sv.get());
            assertEquals(ShortVar.of(1).toVar().get(), Short.valueOf((short) 1));
            assertEquals(0, ShortVar.of(111).clear().get());
        }
        {
            // int
            assertEquals(1, IntVar.of(1).get());
            assertEquals(2, IntVar.of(1).set(2).get());
            assertEquals(2, IntVar.of(1).add(1).get());
            assertEquals(1, IntVar.of(1).getAndIncrement());
            assertEquals(2, IntVar.of(1).incrementAndGet());
            IntVar iv = IntVar.of(1);
            assertEquals(1, iv.getAndIncrement());
            assertEquals(2, iv.get());
            assertEquals(IntVar.of(1).toVar().get(), Integer.valueOf(1));
            assertEquals(0, IntVar.of(111).clear().get());
        }
        {
            // long
            assertEquals(1, LongVar.of(1).get());
            assertEquals(2, LongVar.of(1).set(2).get());
            assertEquals(2, LongVar.of(1).add(1).get());
            assertEquals(1, LongVar.of(1).getAndIncrement());
            assertEquals(2, LongVar.of(1).incrementAndGet());
            LongVar lv = LongVar.of(1);
            assertEquals(1, lv.getAndIncrement());
            assertEquals(2, lv.get());
            assertEquals(LongVar.of(1).toVar().get(), Long.valueOf(1));
            assertEquals(0, LongVar.of(111).clear().get());
        }
        {
            // float
            assertEquals(1, FloatVar.of(1).get());
            assertEquals(2, FloatVar.of(1).set(2).get());
            assertEquals(2, FloatVar.of(1).add(1).get());
            assertEquals(FloatVar.of(1).toVar().get(), Float.valueOf(1));
            assertEquals(1, FloatVar.of(1.0).get());
            assertEquals(2, FloatVar.of(1.0).set(2.0).get());
            assertEquals(2, FloatVar.of(1.0).add(1.0).get());
            assertEquals(0, FloatVar.of(111).clear().get());
        }
        {
            // double
            assertEquals(1, DoubleVar.of(1).get());
            assertEquals(2, DoubleVar.of(1).set(2).get());
            assertEquals(2, DoubleVar.of(1).add(1).get());
            assertEquals(DoubleVar.of(1).toVar().get(), Double.valueOf(1));
            assertEquals(0, DoubleVar.of(111).clear().get());
        }
    }

    @Test
    public void testSpan() {
        int start = 1;
        int end = 10;
        {
            // span
            assertEquals(Span.of(start, end), Span.of(start, end));
            assertEquals(start, Span.of(start, end).startIndex());
            assertEquals(end, Span.of(start, end).endIndex());
            assertNotEquals(Span.of(start, end), Span.of(start, end + 1));
            assertNotEquals(Span.of(start, end), Span.of(start + 1, end));
            assertNotEquals(Span.of(start, end), Span.of(start + 1, end + 1));
            assertFalse(Span.of(start, end).equals(""));
            assertEquals(Span.of(start, end).hashCode(), Span.of(start, end).hashCode());
            assertEquals("[1, 10)", Span.of(start, end).toString());
            assertEquals(false, Span.of(start, end).isEmpty());
            assertEquals(true, Span.of(start, start).isEmpty());
            assertEquals(end - start, Span.of(start, end).length());
            assertThrows(IllegalArgumentException.class, () -> Span.of(end, start));
            assertSame(Span.empty(), Span.empty());
        }
        {
            // long span
            assertEquals(LongSpan.of(start, end), LongSpan.of(start, end));
            assertEquals(start, LongSpan.of(start, end).startIndex());
            assertEquals(end, LongSpan.of(start, end).endIndex());
            assertNotEquals(LongSpan.of(start, end), LongSpan.of(start, end + 1));
            assertNotEquals(LongSpan.of(start, end), LongSpan.of(start + 1, end));
            assertNotEquals(LongSpan.of(start, end), LongSpan.of(start + 1, end + 1));
            assertFalse(LongSpan.of(start, end).equals(""));
            assertEquals(LongSpan.of(start, end).hashCode(), LongSpan.of(start, end).hashCode());
            assertEquals("[1, 10)", LongSpan.of(start, end).toString());
            assertEquals(false, LongSpan.of(start, end).isEmpty());
            assertEquals(true, LongSpan.of(start, start).isEmpty());
            assertEquals(end - start, LongSpan.of(start, end).length());
            assertThrows(IllegalArgumentException.class, () -> LongSpan.of(end, start));
            assertSame(LongSpan.empty(), LongSpan.empty());
        }
    }
}
