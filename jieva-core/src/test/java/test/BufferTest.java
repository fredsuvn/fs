//package test;
//
//import org.testng.Assert;
//import org.testng.annotations.Test;
//import xyz.sunqian.common.base.bytes.BytesBuilder;
//import xyz.sunqian.common.base.JieString;
//import xyz.sunqian.common.io.JieIO;
//
//import java.nio.ByteBuffer;
//import java.util.Arrays;
//import java.util.stream.Collectors;
//
//public class BufferTest {
//
//    @Test
//    public void testRead() {
//        ByteBuffer buffer = ByteBuffer.wrap(JieString.encode("123456789"));
//        buffer.mark();
//        Assert.assertEquals(
//            JieString.encode("123456789"),
//            JieIO.read(buffer)
//        );
//        Assert.assertEquals(
//            buffer.position(),
//            buffer.limit()
//        );
//        buffer.reset();
//        Assert.assertEquals(
//            JieString.encode("12345"),
//            JieIO.read(buffer, 5)
//        );
//        Assert.assertEquals(
//            buffer.position(),
//            5
//        );
//        buffer.reset();
//        Assert.assertEquals(
//            "123456789",
//            JieIO.readString(buffer)
//        );
//        Assert.assertEquals(
//            buffer.position(),
//            buffer.limit()
//        );
//        buffer.reset();
//        Assert.assertEquals(
//            JieString.encode("12345"),
//            JieIO.read(JieIO.slice(buffer, 5))
//        );
//        Assert.assertEquals(
//            buffer.position(),
//            0
//        );
//    }
//
//    @Test
//    public void testBytesBuilder() {
//        BytesBuilder builder = new BytesBuilder();
//        builder.append((byte) 'a');
//        builder.append(JieString.encode("123456789"));
//        builder.append(JieString.encode("123456789"), 2, 5);
//        ByteBuffer buffer1 = ByteBuffer.wrap(JieString.encode("123456789"));
//        Assert.assertTrue(buffer1.hasArray());
//        buffer1.mark();
//        buffer1.position(3);
//        builder.append(buffer1);
//        buffer1.reset();
//        ByteBuffer buffer2 = ByteBuffer.allocateDirect(9);
//        Assert.assertFalse(buffer2.hasArray());
//        buffer2.put(buffer1);
//        buffer2.flip();
//        buffer2.position(4);
//        builder.append(buffer2);
//        Assert.assertEquals(
//            JieIO.read(builder.toByteBuffer()),
//            JieString.encode("a1234567893456745678956789")
//        );
//    }
//
//    @Test
//    public void testSplit() {
//        ByteBuffer buffer = ByteBuffer.wrap(JieString.encode("1234567890"));
//        Assert.assertEquals(
//            JieIO.split(buffer, 4).stream().map(JieIO::readString).collect(Collectors.toList()),
//            Arrays.asList("1234", "5678")
//        );
//        Assert.assertEquals(buffer.position(), 8);
//        buffer = ByteBuffer.wrap(JieString.encode("1234567890"));
//        Assert.assertEquals(
//            JieIO.split(buffer, 5).stream().map(JieIO::readString).collect(Collectors.toList()),
//            Arrays.asList("12345", "67890")
//        );
//        Assert.assertEquals(buffer.position(), 10);
//        buffer = ByteBuffer.wrap(JieString.encode("123|456|"));
//        Assert.assertEquals(
//            JieIO.split(buffer, (byte) '|').stream().map(JieIO::readString).collect(Collectors.toList()),
//            Arrays.asList("123", "456")
//        );
//        Assert.assertEquals(buffer.position(), 8);
//        buffer = ByteBuffer.wrap(JieString.encode("|123|456|"));
//        Assert.assertEquals(
//            JieIO.split(buffer, (byte) '|').stream().map(JieIO::readString).collect(Collectors.toList()),
//            Arrays.asList(null, "123", "456")
//        );
//        Assert.assertEquals(buffer.position(), 9);
//        buffer = ByteBuffer.wrap(JieString.encode("|123|456"));
//        Assert.assertEquals(
//            JieIO.split(buffer, (byte) '|').stream().map(JieIO::readString).collect(Collectors.toList()),
//            Arrays.asList(null, "123")
//        );
//        Assert.assertEquals(buffer.position(), 5);
//    }
//
//    @Test
//    public void testSubBuffer() {
//        ByteBuffer buffer = ByteBuffer.wrap(JieString.encode("123456789"));
//        buffer.mark();
//        Assert.assertEquals(
//            JieString.encode("12345"),
//            JieIO.read(JieIO.slice(buffer, 5))
//        );
//        Assert.assertEquals(
//            buffer.position(),
//            0
//        );
//        buffer.reset();
//        Assert.assertEquals(
//            JieString.encode("56789"),
//            JieIO.read(JieIO.subBuffer(buffer, 4))
//        );
//        Assert.assertEquals(
//            buffer.position(),
//            0
//        );
//        buffer.reset();
//        Assert.assertEquals(
//            JieString.encode("567"),
//            JieIO.read(JieIO.subBuffer(buffer, 4, 3))
//        );
//        Assert.assertEquals(
//            buffer.position(),
//            0
//        );
//
//        ByteBuffer bf = ByteBuffer.wrap(JieString.encode("123456789"), 1, 7).slice();
//        ByteBuffer slice = JieIO.slice(bf, 1);
//        Assert.assertEquals(
//            slice.position(),
//            0
//        );
//        Assert.assertEquals(
//            slice.limit(),
//            1
//        );
//        Assert.assertTrue(slice.hasArray());
//        Assert.assertEquals(
//            slice.arrayOffset(),
//            1
//        );
//        ByteBuffer view = JieIO.subBuffer(bf, 2, 2);
//        Assert.assertEquals(
//            view.position(),
//            0
//        );
//        Assert.assertEquals(
//            view.limit(),
//            2
//        );
//        Assert.assertTrue(view.hasArray());
//        Assert.assertEquals(
//            view.arrayOffset(),
//            3
//        );
//    }
//}