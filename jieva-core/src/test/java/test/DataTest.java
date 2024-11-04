//package test;
//
//import org.testng.Assert;
//import org.testng.annotations.Test;
//import xyz.sunqian.common.base.JieChars;
//import xyz.sunqian.common.data.GekData;
//import xyz.sunqian.common.io.JieIO;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.nio.ByteBuffer;
//import java.util.Arrays;
//import java.util.function.Supplier;
//
//public class DataTest {
//
//    private static final String DATA = TestUtil.buildRandomString(256, 256);
//
//    @Test
//    public void testData() {
//        byte[] bytes = DATA.getBytes(JieChars.defaultCharset());
//        testData(bytes, () -> GekData.wrap(bytes));
//        testData(bytes, () -> GekData.wrap(ByteBuffer.wrap(bytes)));
//        testData(bytes, () -> GekData.wrap(new ByteArrayInputStream(bytes)));
//        //testData(bytes, () -> GekData.from(() -> Arrays.copyOf(bytes, bytes.length)));
//
//        Assert.assertEquals(GekData.wrap(bytes, 0, 0).write(new byte[0]), -1);
//        Assert.assertEquals(GekData.wrap(bytes, 0, 0).write(new byte[bytes.length], 2, 9), -1);
//        Assert.assertEquals(GekData.wrap(JieIO.emptyBuffer()).write(new byte[bytes.length], 2, 9), -1);
//        Assert.assertEquals(GekData.wrap(new ByteArrayInputStream(new byte[0])).write(new byte[bytes.length], 2, 9), -1);
//        Assert.assertNull(GekData.wrap(JieIO.emptyBuffer()).toBuffer());
//        Assert.assertEquals(GekData.wrap(bytes).write(new byte[bytes.length], 2, 0), 0);
//    }
//
//    private void testData(byte[] data, Supplier<GekData> supplier) {
//
//        //array:
//        byte[] b = new byte[data.length];
//        supplier.get().write(b);
//        Assert.assertEquals(b, data);
//        supplier.get().write(b, 2, 8);
//        Assert.assertEquals(Arrays.copyOfRange(b, 2, 2 + 8), Arrays.copyOfRange(data, 0, 8));
//
//        //buffer:
//        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
//        supplier.get().write(buffer);
//        buffer.flip();
//        Assert.assertEquals(JieIO.read(buffer), data);
//        buffer.clear();
//        supplier.get().write(buffer, 11);
//        buffer.flip();
//        Assert.assertEquals(JieIO.read(buffer, 11), Arrays.copyOfRange(data, 0, 11));
//
//        //stream:
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        supplier.get().write(out);
//        Assert.assertEquals(out.toByteArray(), data);
//        out.reset();
//        supplier.get().write(out, 11);
//        Assert.assertEquals(out.toByteArray(), Arrays.copyOfRange(data, 0, 11));
//
//        //to/as:
//        Assert.assertEquals(supplier.get().toArray(), data);
//        Assert.assertEquals(JieIO.read(supplier.get().toBuffer()), data);
//        Assert.assertEquals(JieIO.read(supplier.get().asInputStream()), data);
//    }
//}
