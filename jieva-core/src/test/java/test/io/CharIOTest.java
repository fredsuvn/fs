// package test.io;
//
// import org.testng.annotations.Test;
// import xyz.sunqian.common.base.chars.CharsBuilder;
// import xyz.sunqian.common.io.BufferKit;
// import xyz.sunqian.common.io.IOKit;
// import xyz.sunqian.common.io.IORuntimeException;
// import xyz.sunqian.test.DataTest;
// import xyz.sunqian.test.ErrorAppender;
// import xyz.sunqian.test.ReadOps;
// import xyz.sunqian.test.TestReader;
//
// import java.io.CharArrayReader;
// import java.nio.CharBuffer;
// import java.util.Arrays;
//
// import static org.testng.Assert.assertEquals;
// import static org.testng.Assert.assertNull;
// import static org.testng.Assert.assertSame;
// import static org.testng.Assert.expectThrows;
//
// public class CharIOTest implements DataTest {
//
//     @Test
//     public void testReadChars() throws Exception {
//         testReadChars(64);
//         testReadChars(128);
//         testReadChars(256);
//         testReadChars(1024);
//         testReadChars(IOKit.bufferSize());
//         testReadChars(IOKit.bufferSize() - 1);
//         testReadChars(IOKit.bufferSize() + 1);
//         testReadChars(IOKit.bufferSize() - 5);
//         testReadChars(IOKit.bufferSize() + 5);
//         testReadChars(IOKit.bufferSize() * 2);
//         testReadChars(IOKit.bufferSize() * 2 - 1);
//         testReadChars(IOKit.bufferSize() * 2 + 1);
//         testReadChars(IOKit.bufferSize() * 2 - 5);
//         testReadChars(IOKit.bufferSize() * 2 + 5);
//         testReadChars(IOKit.bufferSize() * 3);
//         testReadChars(IOKit.bufferSize() * 3 - 1);
//         testReadChars(IOKit.bufferSize() * 3 + 1);
//         testReadChars(IOKit.bufferSize() * 3 - 5);
//         testReadChars(IOKit.bufferSize() * 3 + 5);
//
//         {
//             // read stream
//             assertNull(IOKit.read(new CharArrayReader(new char[0])));
//             assertNull(IOKit.read(new CharArrayReader(new char[0]), 66));
//             assertNull(IOKit.string(new CharArrayReader(new char[0])));
//             assertNull(IOKit.string(new CharArrayReader(new char[0]), 66));
//         }
//
//         {
//             // error
//             TestReader tin = new TestReader(new CharArrayReader(new char[0]));
//             tin.setNextOperation(ReadOps.THROW, 99);
//             expectThrows(IORuntimeException.class, () -> IOKit.read(tin));
//             expectThrows(IORuntimeException.class, () -> IOKit.read(tin, 1));
//             expectThrows(IllegalArgumentException.class, () -> IOKit.read(tin, -1));
//         }
//     }
//
//     private void testReadChars(int totalSize) throws Exception {
//         testReadChars(CharIO.get(IOKit.bufferSize()), totalSize);
//         testReadChars(CharIO.get(1), totalSize);
//         testReadChars(CharIO.get(2), totalSize);
//         testReadChars(CharIO.get(IOKit.bufferSize() - 1), totalSize);
//         testReadChars(CharIO.get(IOKit.bufferSize() + 1), totalSize);
//         testReadChars(CharIO.get(IOKit.bufferSize() * 2), totalSize);
//     }
//
//     private void testReadChars(CharIO reader, int totalSize) throws Exception {
//         testReadChars(reader, totalSize, totalSize);
//         testReadChars(reader, totalSize, 0);
//         testReadChars(reader, totalSize, 1);
//         testReadChars(reader, totalSize, totalSize / 2);
//         testReadChars(reader, totalSize, totalSize - 1);
//         testReadChars(reader, totalSize, totalSize + 1);
//         testReadChars(reader, totalSize, totalSize * 2);
//     }
//
//     private void testReadChars(CharIO reader, int totalSize, int readSize) throws Exception {
//         {
//             // reader
//             char[] data = randomChars(totalSize);
//             assertEquals(reader.read(new CharArrayReader(data)), data);
//             assertEquals(reader.string(new CharArrayReader(data)), new String(data));
//             assertEquals(
//                 reader.read(new CharArrayReader(data), readSize < 0 ? totalSize : readSize),
//                 (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
//             );
//             assertEquals(
//                 reader.string(new CharArrayReader(data), readSize < 0 ? totalSize : readSize),
//                 new String((readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize))
//             );
//             assertEquals(reader.read(new OneCharReader(data)), data);
//             assertEquals(reader.string(new OneCharReader(data)), new String(data));
//             assertEquals(
//                 reader.read(new OneCharReader(data), readSize < 0 ? totalSize : readSize),
//                 (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
//             );
//             assertEquals(
//                 reader.string(new OneCharReader(data), readSize < 0 ? totalSize : readSize),
//                 new String((readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize))
//             );
//         }
//     }
//
//     @Test
//     public void testReadCharsTo() throws Exception {
//         testReadCharsTo(64);
//         testReadCharsTo(128);
//         testReadCharsTo(256);
//         testReadCharsTo(1024);
//         testReadCharsTo(IOKit.bufferSize());
//         testReadCharsTo(IOKit.bufferSize() - 1);
//         testReadCharsTo(IOKit.bufferSize() + 1);
//         testReadCharsTo(IOKit.bufferSize() - 5);
//         testReadCharsTo(IOKit.bufferSize() + 5);
//         testReadCharsTo(IOKit.bufferSize() * 2);
//         testReadCharsTo(IOKit.bufferSize() * 2 - 1);
//         testReadCharsTo(IOKit.bufferSize() * 2 + 1);
//         testReadCharsTo(IOKit.bufferSize() * 2 - 5);
//         testReadCharsTo(IOKit.bufferSize() * 2 + 5);
//         testReadCharsTo(IOKit.bufferSize() * 3);
//         testReadCharsTo(IOKit.bufferSize() * 3 - 1);
//         testReadCharsTo(IOKit.bufferSize() * 3 + 1);
//         testReadCharsTo(IOKit.bufferSize() * 3 - 5);
//         testReadCharsTo(IOKit.bufferSize() * 3 + 5);
//
//         {
//             // size 0: reader to appender
//             char[] data = new char[0];
//             CharsBuilder bb = new CharsBuilder();
//             assertEquals(
//                 IOKit.readTo(new CharArrayReader(data), bb),
//                 -1
//             );
//             assertEquals(bb.size(), 0);
//             assertEquals(
//                 IOKit.readTo(new CharArrayReader(data), bb, 0),
//                 0
//             );
//             assertEquals(bb.size(), 0);
//             assertEquals(
//                 IOKit.readTo(new CharArrayReader(data), bb, 11),
//                 -1
//             );
//             assertEquals(bb.size(), 0);
//         }
//         {
//             // size 0: reader to array
//             char[] data = new char[0];
//             char[] aar = new char[64];
//             Arrays.fill(aar, (char) 7);
//             assertEquals(
//                 IOKit.readTo(new CharArrayReader(data), aar),
//                 -1
//             );
//             assertEquals(aar[0], (char) 7);
//             assertEquals(
//                 IOKit.readTo(new CharArrayReader(data), new char[0]),
//                 0
//             );
//             assertEquals(aar[0], (char) 7);
//         }
//         {
//             // size 0: reader to heap buffer
//             char[] data = new char[0];
//             CharBuffer buf = CharBuffer.allocate(1);
//             assertEquals(
//                 IOKit.readTo(new CharArrayReader(data), buf),
//                 -1
//             );
//             assertEquals(buf.position(), 0);
//             assertEquals(
//                 IOKit.readTo(new CharArrayReader(data), CharBuffer.allocate(0)),
//                 0
//             );
//             assertEquals(buf.position(), 0);
//             assertEquals(
//                 IOKit.readTo(new CharArrayReader(data), buf, 1),
//                 -1
//             );
//             assertEquals(buf.position(), 0);
//             assertEquals(
//                 IOKit.readTo(new CharArrayReader(data), buf, 0),
//                 0
//             );
//             assertEquals(buf.position(), 0);
//         }
//         {
//             // size 0: reader to direct buffer
//             char[] data = new char[0];
//             CharBuffer buf = BufferKit.directCharBuffer(1);
//             assertEquals(
//                 IOKit.readTo(new CharArrayReader(data), buf),
//                 -1
//             );
//             assertEquals(buf.position(), 0);
//             assertEquals(
//                 IOKit.readTo(new CharArrayReader(data), BufferKit.directCharBuffer(0)),
//                 0
//             );
//             assertEquals(buf.position(), 0);
//             assertEquals(
//                 IOKit.readTo(new CharArrayReader(data), buf, 1),
//                 -1
//             );
//             assertEquals(buf.position(), 0);
//             assertEquals(
//                 IOKit.readTo(new CharArrayReader(data), buf, 0),
//                 0
//             );
//             assertEquals(buf.position(), 0);
//         }
//
//         {
//             // error
//             TestReader tin = new TestReader(new CharArrayReader(new char[0]));
//             tin.setNextOperation(ReadOps.THROW, 99);
//             ErrorAppender errOut = new ErrorAppender();
//             // read stream
//             expectThrows(IORuntimeException.class, () -> IOKit.readTo(tin, errOut));
//             expectThrows(IORuntimeException.class, () -> IOKit.readTo(tin, errOut, 1));
//             expectThrows(IllegalArgumentException.class, () -> IOKit.readTo(tin, errOut, -1));
//             expectThrows(IORuntimeException.class, () -> IOKit.readTo(tin, new char[1], 0, 1));
//             expectThrows(IndexOutOfBoundsException.class, () -> IOKit.readTo(tin, new char[0], 1, 0));
//             expectThrows(IndexOutOfBoundsException.class, () -> IOKit.readTo(tin, new char[0], 0, 1));
//             expectThrows(IORuntimeException.class, () -> IOKit.readTo(tin, CharBuffer.allocate(1)));
//             expectThrows(IllegalArgumentException.class, () -> IOKit.readTo(tin, CharBuffer.allocate(1), -1));
//             expectThrows(IORuntimeException.class, () ->
//                 IOKit.readTo(new CharArrayReader(new char[1]), CharBuffer.allocate(1).asReadOnlyBuffer())
//             );
//         }
//     }
//
//     private void testReadCharsTo(int totalSize) throws Exception {
//         testReadCharsTo(CharIO.get(IOKit.bufferSize()), totalSize);
//         testReadCharsTo(CharIO.get(1), totalSize);
//         testReadCharsTo(CharIO.get(2), totalSize);
//         testReadCharsTo(CharIO.get(IOKit.bufferSize() - 1), totalSize);
//         testReadCharsTo(CharIO.get(IOKit.bufferSize() + 1), totalSize);
//         testReadCharsTo(CharIO.get(IOKit.bufferSize() * 2), totalSize);
//     }
//
//     private void testReadCharsTo(CharIO reader, int totalSize) throws Exception {
//         testReadCharsTo(reader, totalSize, totalSize);
//         testReadCharsTo(reader, totalSize, 0);
//         testReadCharsTo(reader, totalSize, 1);
//         testReadCharsTo(reader, totalSize, totalSize / 2);
//         testReadCharsTo(reader, totalSize, totalSize - 1);
//         testReadCharsTo(reader, totalSize, totalSize + 1);
//         testReadCharsTo(reader, totalSize, totalSize * 2);
//     }
//
//     private void testReadCharsTo(CharIO reader, int totalSize, int readSize) throws Exception {
//         {
//             // reader to appender
//             char[] data = randomChars(totalSize);
//             CharsBuilder builder = new CharsBuilder();
//             assertEquals(
//                 reader.readTo(new CharArrayReader(data), builder),
//                 totalSize
//             );
//             assertEquals(builder.toCharArray(), data);
//             builder.reset();
//             assertEquals(
//                 reader.readTo(new CharArrayReader(data), builder, readSize < 0 ? totalSize : readSize),
//                 actualReadSize(totalSize, readSize)
//             );
//             assertEquals(
//                 builder.toCharArray(),
//                 (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
//             );
//             builder.reset();
//             assertEquals(
//                 reader.readTo(new OneCharReader(data), builder),
//                 totalSize
//             );
//             assertEquals(builder.toCharArray(), data);
//             builder.reset();
//             assertEquals(
//                 reader.readTo(new OneCharReader(data), builder, readSize < 0 ? totalSize : readSize),
//                 actualReadSize(totalSize, readSize)
//             );
//             assertEquals(
//                 builder.toCharArray(),
//                 (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
//             );
//             builder.reset();
//         }
//         {
//             // reader to array
//             char[] data = randomChars(totalSize);
//             char[] dst = new char[data.length];
//             assertEquals(
//                 reader.readTo(new CharArrayReader(data), dst),
//                 totalSize
//             );
//             assertEquals(dst, data);
//             if (readSize >= 0 && readSize <= totalSize) {
//                 dst = new char[data.length];
//                 assertEquals(
//                     reader.readTo(new CharArrayReader(data), dst, 0, readSize),
//                     readSize
//                 );
//                 assertEquals(
//                     Arrays.copyOf(dst, readSize),
//                     Arrays.copyOf(data, readSize)
//                 );
//                 if (readSize <= totalSize - 1) {
//                     dst = new char[data.length];
//                     assertEquals(
//                         reader.readTo(new CharArrayReader(data), dst, 1, readSize),
//                         readSize
//                     );
//                     assertEquals(
//                         Arrays.copyOfRange(dst, 1, 1 + readSize),
//                         Arrays.copyOf(data, readSize)
//                     );
//                 }
//             }
//             if (readSize > totalSize) {
//                 dst = new char[readSize];
//                 assertEquals(
//                     reader.readTo(new CharArrayReader(data), dst, 0, readSize),
//                     totalSize
//                 );
//                 assertEquals(
//                     Arrays.copyOf(dst, totalSize),
//                     data
//                 );
//                 dst = new char[readSize + 1];
//                 assertEquals(
//                     reader.readTo(new CharArrayReader(data), dst, 1, readSize),
//                     totalSize
//                 );
//                 assertEquals(
//                     Arrays.copyOfRange(dst, 1, 1 + totalSize),
//                     data
//                 );
//             }
//         }
//         {
//             // reader to heap buffer
//             char[] data = randomChars(totalSize);
//             CharBuffer dst = CharBuffer.allocate(data.length);
//             assertEquals(
//                 reader.readTo(new CharArrayReader(data), dst),
//                 totalSize
//             );
//             assertEquals(dst.flip(), CharBuffer.wrap(data));
//             dst = CharBuffer.allocate(data.length);
//             assertEquals(
//                 reader.readTo(new CharArrayReader(data), dst, readSize < 0 ? totalSize : readSize),
//                 actualReadSize(totalSize, readSize)
//             );
//             assertEquals(dst.flip(), CharBuffer.wrap(
//                 (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
//             ));
//         }
//         {
//             // reader to direct buffer
//             char[] data = randomChars(totalSize);
//             CharBuffer dst = BufferKit.directCharBuffer(data.length * 2);
//             assertEquals(
//                 reader.readTo(new CharArrayReader(data), dst),
//                 totalSize
//             );
//             assertEquals(dst.flip(), CharBuffer.wrap(data));
//             dst = BufferKit.directCharBuffer(data.length * 2);
//             assertEquals(
//                 reader.readTo(new CharArrayReader(data), dst, readSize < 0 ? totalSize : readSize),
//                 actualReadSize(totalSize, readSize)
//             );
//             assertEquals(dst.flip(), CharBuffer.wrap(
//                 (readSize < 0 || readSize > totalSize) ? data : Arrays.copyOf(data, readSize)
//             ));
//         }
//     }
//
//     private int actualReadSize(int totalSize, int readSize) {
//         if (readSize == 0) {
//             return 0;
//         }
//         if (totalSize == 0) {
//             return -1;
//         }
//         return Math.min(readSize, totalSize);
//     }
//
//     @Test
//     public void testOther() {
//         {
//             // get operator
//             assertSame(CharIO.defaultOperator(), CharIO.get(IOKit.bufferSize()));
//             assertEquals(CharIO.newOperator(666).bufferSize(), 666);
//         }
//         {
//             // error
//             expectThrows(IllegalArgumentException.class, () -> CharIO.newOperator(0));
//             expectThrows(IllegalArgumentException.class, () -> CharIO.newOperator(-1));
//         }
//     }
// }
