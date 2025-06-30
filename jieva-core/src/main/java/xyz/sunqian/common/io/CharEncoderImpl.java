// package xyz.sunqian.common.io;
//
// import xyz.sunqian.annotations.Nullable;
// import xyz.sunqian.common.base.JieCoding;
// import xyz.sunqian.common.base.chars.JieChars;
// import xyz.sunqian.common.collect.JieCollect;
//
// import java.io.IOException;
// import java.io.Reader;
// import java.io.Writer;
// import java.nio.CharBuffer;
// import java.util.ArrayList;
// import java.util.Collection;
// import java.util.List;
// import java.util.function.Function;
//
// import static xyz.sunqian.common.base.JieCheck.checkOffsetLength;
//
// final class CharEncoderImpl implements CharEncoder {
//
//     private final @Nullable Object source;
//     private @Nullable Object dest;
//     private long readLimit = -1;
//     private int readBlockSize = IOKit.bufferSize();
//     private boolean endOnZeroRead = false;
//     private @Nullable List<Handler> encoders;
//
//     // initials after starting process
//     private @Nullable CharReader sourceReader;
//     private @Nullable CharEncoder.Handler oneEncoder;
//
//     CharEncoderImpl(Reader source) {
//         this.source = source;
//     }
//
//     CharEncoderImpl(char[] source) {
//         this.source = source;
//     }
//
//     CharEncoderImpl(CharBuffer source) {
//         this.source = source;
//     }
//
//     CharEncoderImpl(CharSequence source) {
//         this.source = source;
//     }
//
//     private Object getSource() {
//         if (source == null) {
//             throw new IORuntimeException("The source is null!");
//         }
//         return source;
//     }
//
//     private Object getDest() {
//         if (dest == null) {
//             throw new IORuntimeException("The destination is null!");
//         }
//         return dest;
//     }
//
//     private CharReader getSourceReader() {
//         if (sourceReader == null) {
//             sourceReader = toCharReader(getSource());
//         }
//         return sourceReader;
//     }
//
//     private Handler getEncoder() {
//         if (oneEncoder == null) {
//             oneEncoder = toOneEncoder(encoders);
//         }
//         return oneEncoder;
//     }
//
//     @Override
//     public CharEncoder readLimit(long readLimit) {
//         this.readLimit = readLimit;
//         return this;
//     }
//
//     @Override
//     public CharEncoder readBlockSize(int readBlockSize) {
//         if (readBlockSize <= 0) {
//             throw new IllegalArgumentException("readBlockSize must > 0!");
//         }
//         this.readBlockSize = readBlockSize;
//         return this;
//     }
//
//     @Override
//     public CharEncoder endOnZeroRead(boolean endOnZeroRead) {
//         this.endOnZeroRead = endOnZeroRead;
//         return this;
//     }
//
//     @Override
//     public CharEncoder encoder(Handler encoder) {
//         if (encoders == null) {
//             encoders = new ArrayList<>();
//         }
//         encoders.add(encoder);
//         return this;
//     }
//
//     @Override
//     public long process() {
//         this.dest = NullDataWriter.SINGLETON;
//         return start();
//     }
//
//     @Override
//     public long writeTo(Appendable dest) {
//         this.dest = dest;
//         return start();
//     }
//
//     @Override
//     public long writeTo(char[] dest) {
//         this.dest = dest;
//         return start();
//     }
//
//     @Override
//     public long writeTo(char[] dest, int offset, int length) {
//         try {
//             this.dest = CharBuffer.wrap(dest, offset, length);
//         } catch (Exception e) {
//             throw new IORuntimeException(e);
//         }
//         return start();
//     }
//
//     @Override
//     public long writeTo(CharBuffer dest) {
//         this.dest = dest;
//         return start();
//     }
//
//     @Override
//     public String toString() {
//         return new String(toCharArray());
//     }
//
//     @Override
//     public Reader toReader() {
//         if (JieCollect.isEmpty(encoders)) {
//             return toReader(getSource());
//         }
//         return new ProcessorReader();
//     }
//
//     private Reader toReader(Object src) {
//         if (src instanceof Reader) {
//             return (Reader) src;
//         }
//         if (src instanceof char[]) {
//             return IOKit.newReader((char[]) src);
//         }
//         if (src instanceof CharBuffer) {
//             return IOKit.newReader((CharBuffer) src);
//         }
//         if (src instanceof CharSequence) {
//             return IOKit.newReader((CharSequence) src);
//         }
//         throw new IORuntimeException("The type of source is unsupported: " + src.getClass());
//     }
//
//     private long start() {
//         if (readLimit == 0) {
//             return 0;
//         }
//         try {
//             if (JieCollect.isEmpty(encoders)) {
//                 Object src = getSource();
//                 Object dst = getDest();
//                 if (src instanceof char[]) {
//                     if (dst instanceof char[]) {
//                         return charsToChars((char[]) src, (char[]) dst);
//                     }
//                     if (dst instanceof CharBuffer) {
//                         return charsToBuffer((char[]) src, (CharBuffer) dst);
//                     }
//                     return charsToAppender((char[]) src, (Appendable) dst);
//                 } else if (src instanceof CharBuffer) {
//                     if (dst instanceof char[]) {
//                         return bufferToChars((CharBuffer) src, (char[]) dst);
//                     }
//                     return bufferToAppender((CharBuffer) src, (Appendable) dst);
//                 } else if (src instanceof CharSequence) {
//                     if (dst instanceof char[]) {
//                         return charSeqToChars((CharSequence) src, (char[]) dst);
//                     }
//                     return charSeqToAppender((CharSequence) src, (Appendable) dst);
//                 }
//             }
//             return startInBlocks();
//         } catch (IORuntimeException e) {
//             throw e;
//         } catch (Exception e) {
//             throw new IORuntimeException(e);
//         }
//     }
//
//     private long charsToChars(char[] src, char[] dst) {
//         int len = getDirectLen(src.length);
//         System.arraycopy(src, 0, dst, 0, len);
//         return len;
//     }
//
//     private long charsToBuffer(char[] src, CharBuffer dst) {
//         int len = getDirectLen(src.length);
//         dst.put(src, 0, len);
//         return len;
//     }
//
//     private long charsToAppender(char[] src, Appendable dst) throws IOException {
//         int len = getDirectLen(src.length);
//         if (dst instanceof Writer) {
//             ((Writer) dst).write(src, 0, len);
//         } else {
//             dst.append(new String(src, 0, len));
//         }
//         return len;
//     }
//
//     private long bufferToChars(CharBuffer src, char[] dst) {
//         int len = getDirectLen(src.remaining());
//         src.get(dst, 0, len);
//         return len;
//     }
//
//     private long bufferToAppender(CharBuffer src, Appendable dst) throws IOException {
//         int len = getDirectLen(src.remaining());
//         int pos = src.position();
//         int newPos = pos + len;
//         dst.append(src, 0, len);
//         src.position(newPos);
//         return len;
//     }
//
//     private long charSeqToChars(CharSequence src, char[] dst) throws IOException {
//         int len = getDirectLen(src.length());
//         if (src instanceof String) {
//             ((String) src).getChars(0, len, dst, 0);
//         } else {
//             for (int i = 0; i < len; i++) {
//                 dst[i] = src.charAt(i);
//             }
//         }
//         return len;
//     }
//
//     private long charSeqToAppender(CharSequence src, Appendable dst) throws IOException {
//         int len = getDirectLen(src.length());
//         dst.append(src, 0, len);
//         return len;
//     }
//
//     private int getDirectLen(int srcSize) {
//         return readLimit < 0 ? srcSize : Math.min(srcSize, (int) readLimit);
//     }
//
//     private long startInBlocks() throws Exception {
//         DataWriter out = toBufferOut(getDest());
//         return readTo(getSourceReader(), getEncoder(), out);
//     }
//
//     private long readTo(CharReader reader, Handler oneEncoder, DataWriter out) throws Exception {
//         // CharReader reader = readLimit < 0 ? in : in.withReadLimit(readLimit);
//         long count = 0;
//         while (true) {
//             CharSegment segment;
//             int nextSize = nextReadBlockSize(count);
//             if (nextSize == 0) {
//                 segment = CharSegment.empty(true);
//             } else {
//                 segment = reader.read(nextSize);
//             }
//             count += segment.data().remaining();
//             @Nullable CharBuffer encoded = oneEncoder.encode(segment.data(), segment.end());
//             if (!JieChars.isEmpty(encoded)) {
//                 out.write(encoded);
//             }
//             if (segment.end()) {
//                 return count;
//             }
//         }
//     }
//
//     private int nextReadBlockSize(long count) {
//         if (readLimit < 0) {
//             return readBlockSize;
//         }
//         return (int) Math.min(readLimit - count, readBlockSize);
//     }
//
//     private CharReader toCharReader(Object src) {
//         if (src instanceof Reader) {
//             return CharReader.from((Reader) src);
//         }
//         if (src instanceof char[]) {
//             return CharReader.from((char[]) src);
//         }
//         if (src instanceof CharBuffer) {
//             return CharReader.from((CharBuffer) src);
//         }
//         if (src instanceof CharSequence) {
//             return CharReader.from((CharSequence) src);
//         }
//         throw new IORuntimeException("The type of source is unsupported: " + src.getClass());
//     }
//
//     private DataWriter toBufferOut(Object dst) {
//         if (dst instanceof DataWriter) {
//             return (DataWriter) dst;
//         }
//         if (dst instanceof char[]) {
//             return new AppendableDataWriter(CharBuffer.wrap((char[]) dst));
//         }
//         if (dst instanceof CharBuffer) {
//             return new AppendableDataWriter(IOKit.newWriter((CharBuffer) dst));
//         }
//         if (dst instanceof Appendable) {
//             return new AppendableDataWriter((Appendable) dst);
//         }
//         throw new IORuntimeException("The type of destination is unsupported: " + dst.getClass());
//     }
//
//     private Handler toOneEncoder(@Nullable List<Handler> encoders) {
//         if (JieCollect.isEmpty(encoders)) {
//             return Handler.emptyEncoder();
//         }
//         if (encoders.size() == 1) {
//             return encoders.get(0);
//         }
//         return (data, end) -> {
//             @Nullable CharBuffer chars = data;
//             for (Handler encoder : encoders) {
//                 chars = encoder.encode(chars, end);
//                 if (chars == null) {
//                     break;
//                 }
//             }
//             return chars;
//         };
//     }
//
//     private interface DataWriter {
//         void write(CharBuffer buffer) throws Exception;
//     }
//
//     private static final class AppendableDataWriter implements DataWriter {
//
//         private final Appendable dest;
//
//         private AppendableDataWriter(Appendable dest) {
//             this.dest = dest;
//         }
//
//         @Override
//         public void write(CharBuffer buffer) throws IOException {
//             if (dest instanceof Writer) {
//                 write(buffer, (Writer) dest);
//                 return;
//             }
//             if (buffer.hasArray()) {
//                 int remaining = buffer.remaining();
//                 dest.append(new String(
//                     buffer.array(),
//                     BufferKit.arrayStartIndex(buffer),
//                     buffer.remaining()
//                 ));
//                 buffer.position(buffer.position() + remaining);
//             } else {
//                 char[] buf = new char[buffer.remaining()];
//                 buffer.get(buf);
//                 dest.append(new String(buf));
//             }
//         }
//
//         private void write(CharBuffer buffer, Writer writer) throws IOException {
//             if (buffer.hasArray()) {
//                 int remaining = buffer.remaining();
//                 writer.write(buffer.array(), BufferKit.arrayStartIndex(buffer), remaining);
//                 buffer.position(buffer.position() + remaining);
//             } else {
//                 char[] buf = new char[buffer.remaining()];
//                 buffer.get(buf);
//                 writer.write(buf);
//             }
//         }
//     }
//
//     private static final class NullDataWriter implements DataWriter {
//
//         static final NullDataWriter SINGLETON = new NullDataWriter();
//
//         @Override
//         public void write(CharBuffer buffer) {
//             // Do nothing
//         }
//     }
//
//     private final class ProcessorReader extends Reader {
//
//         private @Nullable CharSegment nextSeg = null;
//         private boolean closed = false;
//
//         private ProcessorReader() {
//         }
//
//         private CharSegment read0() throws IOException {
//             try {
//                 CharSegment s0 = getSourceReader().read(readBlockSize);
//                 @Nullable CharBuffer encoded = getEncoder().encode(s0.data(), s0.end());
//                 if (encoded == s0.data()) {
//                     return s0;
//                 }
//                 return CharSegment.of(encoded, s0.end());
//             } catch (Exception e) {
//                 throw new IOException(e);
//             }
//         }
//
//         @Override
//         public int read() throws IOException {
//             checkClosed();
//             while (true) {
//                 if (nextSeg == null) {
//                     nextSeg = read0();
//                 }
//                 if (nextSeg == CharSegment.empty(true)) {
//                     return -1;
//                 }
//                 if (nextSeg.data().hasRemaining()) {
//                     return nextSeg.data().get() & 0xffff;
//                 }
//                 if (nextSeg.end()) {
//                     nextSeg = CharSegment.empty(true);
//                     return -1;
//                 }
//                 nextSeg = null;
//             }
//         }
//
//         @Override
//         public int read(char[] dst) throws IOException {
//             return read(dst, 0, dst.length);
//         }
//
//         @Override
//         public int read(char[] dst, int off, int len) throws IOException {
//             checkClosed();
//             checkOffsetLength(dst.length, off, len);
//             if (len <= 0) {
//                 return 0;
//             }
//             int pos = off;
//             int remaining = len;
//             while (remaining > 0) {
//                 if (nextSeg == CharSegment.empty(true)) {
//                     return -1;
//                 }
//                 if (nextSeg == null) {
//                     nextSeg = read0();
//                 }
//                 if (nextSeg.data().hasRemaining()) {
//                     int readSize = Math.min(nextSeg.data().remaining(), remaining);
//                     nextSeg.data().get(dst, pos, readSize);
//                     pos += readSize;
//                     remaining -= readSize;
//                     continue;
//                 }
//                 if (nextSeg.end()) {
//                     nextSeg = CharSegment.empty(true);
//                     break;
//                 } else {
//                     nextSeg = null;
//                 }
//             }
//             if (nextSeg.end() && pos == off) {
//                 return -1;
//             }
//             return pos - off;
//         }
//
//         @Override
//         public long skip(long n) throws IOException {
//             checkClosed();
//             if (n <= 0) {
//                 return 0;
//             }
//             long pos = 0;
//             long remaining = n;
//             while (remaining > 0) {
//                 if (nextSeg == CharSegment.empty(true)) {
//                     return 0;
//                 }
//                 if (nextSeg == null) {
//                     nextSeg = read0();
//                 }
//                 if (nextSeg.data().hasRemaining()) {
//                     int readSize = (int) Math.min(nextSeg.data().remaining(), remaining);
//                     nextSeg.data().position(nextSeg.data().position() + readSize);
//                     pos += readSize;
//                     remaining -= readSize;
//                     continue;
//                 }
//                 if (nextSeg.end()) {
//                     nextSeg = CharSegment.empty(true);
//                     break;
//                 } else {
//                     nextSeg = null;
//                 }
//             }
//             if (nextSeg.end() && pos == 0) {
//                 return 0;
//             }
//             return pos;
//         }
//
//         @Override
//         public void close() throws IOException {
//             if (closed) {
//                 return;
//             }
//             if (source instanceof AutoCloseable) {
//                 try {
//                     ((AutoCloseable) source).close();
//                 } catch (IOException e) {
//                     throw e;
//                 } catch (Exception e) {
//                     throw new IOException(e);
//                 }
//             }
//             closed = true;
//         }
//
//         private void checkClosed() throws IOException {
//             if (closed) {
//                 throw new IOException("Reader closed.");
//             }
//         }
//     }
//
//     private static final class BufferMerger implements Function<Collection<CharBuffer>, CharBuffer> {
//
//         private static final BufferMerger SINGLETON = new BufferMerger();
//
//         @Override
//         public @Nullable CharBuffer apply(Collection<CharBuffer> charBuffers) {
//             if (charBuffers.isEmpty()) {
//                 return null;
//             }
//             int size = 0;
//             for (CharBuffer charBuffer : charBuffers) {
//                 size += charBuffer.remaining();
//             }
//             CharBuffer result = CharBuffer.allocate(size);
//             for (CharBuffer charBuffer : charBuffers) {
//                 result.put(charBuffer);
//             }
//             result.flip();
//             return result;
//         }
//     }
//
//     static final class FixedSizeEncoder implements Handler {
//
//         private final Handler encoder;
//         private final int size;
//
//         // Capacity is always the size.
//         private @Nullable CharBuffer buffer;
//
//         FixedSizeEncoder(Handler encoder, int size) throws IllegalArgumentException {
//             checkSize(size);
//             this.encoder = encoder;
//             this.size = size;
//         }
//
//         @Override
//         public @Nullable CharBuffer encode(CharBuffer data, boolean end) throws Exception {
//             @Nullable Object result = null;
//             boolean encoded = false;
//
//             // clean buffer
//             if (buffer != null && buffer.position() > 0) {
//                 BufferKit.readTo(data, buffer);
//                 if (end && !data.hasRemaining()) {
//                     buffer.flip();
//                     return encoder.encode(buffer, true);
//                 }
//                 if (buffer.hasRemaining()) {
//                     return null;
//                 }
//                 buffer.flip();
//                 result = JieCoding.ifAdd(result, encoder.encode(buffer, false));
//                 encoded = true;
//                 buffer.clear();
//             }
//
//             // split
//             int pos = data.position();
//             int limit = data.limit();
//             while (limit - pos >= size) {
//                 pos += size;
//                 data.limit(pos);
//                 CharBuffer slice = data.slice();
//                 data.position(pos);
//                 if (end && pos == limit) {
//                     result = JieCoding.ifAdd(result, encoder.encode(slice, true));
//                     return JieCoding.ifMerge(result, BufferMerger.SINGLETON);
//                 } else {
//                     result = JieCoding.ifAdd(result, encoder.encode(slice, false));
//                     encoded = true;
//                 }
//             }
//             data.limit(limit);
//
//             // buffering
//             if (data.hasRemaining()) {
//                 if (buffer == null) {
//                     buffer = CharBuffer.allocate(size);
//                 }
//                 BufferKit.readTo(data, buffer);
//                 if (end) {
//                     buffer.flip();
//                     result = JieCoding.ifAdd(result, encoder.encode(buffer, true));
//                     encoded = true;
//                 }
//             }
//
//             @Nullable CharBuffer ret = JieCoding.ifMerge(result, BufferMerger.SINGLETON);
//             if (end && !encoded) {
//                 return encoder.encode(JieChars.emptyBuffer(), true);
//             }
//             return ret;
//         }
//     }
//
//     static final class RoundingEncoder implements Handler {
//
//         private final Handler encoder;
//         private final int size;
//
//         // Capacity is always the size.
//         private @Nullable CharBuffer buffer;
//
//         RoundingEncoder(Handler encoder, int size) {
//             checkSize(size);
//             this.encoder = encoder;
//             this.size = size;
//         }
//
//         @Override
//         public @Nullable CharBuffer encode(CharBuffer data, boolean end) throws Exception {
//             @Nullable Object result = null;
//             boolean encoded = false;
//
//             // clean buffer
//             if (buffer != null && buffer.position() > 0) {
//                 BufferKit.readTo(data, buffer);
//                 if (end && !data.hasRemaining()) {
//                     buffer.flip();
//                     return encoder.encode(buffer, true);
//                 }
//                 if (buffer.hasRemaining()) {
//                     return null;
//                 }
//                 buffer.flip();
//                 result = JieCoding.ifAdd(result, encoder.encode(buffer, false));
//                 encoded = true;
//                 buffer.clear();
//             }
//
//             // rounding
//             int remaining = data.remaining();
//             int roundingSize = remaining / size * size;
//             if (roundingSize > 0) {
//                 int pos = data.position();
//                 pos += roundingSize;
//                 int limit = data.limit();
//                 data.limit(pos);
//                 CharBuffer slice = data.slice();
//                 data.position(pos);
//                 data.limit(limit);
//                 if (end && pos == limit) {
//                     result = JieCoding.ifAdd(result, encoder.encode(slice, true));
//                     return JieCoding.ifMerge(result, BufferMerger.SINGLETON);
//                 } else {
//                     result = JieCoding.ifAdd(result, encoder.encode(slice, false));
//                     encoded = true;
//                 }
//             }
//
//             // buffering
//             if (data.hasRemaining()) {
//                 if (buffer == null) {
//                     buffer = CharBuffer.allocate(size);
//                 }
//                 BufferKit.readTo(data, buffer);
//                 if (end) {
//                     buffer.flip();
//                     result = JieCoding.ifAdd(result, encoder.encode(buffer, true));
//                     encoded = true;
//                 }
//             }
//
//             @Nullable CharBuffer ret = JieCoding.ifMerge(result, BufferMerger.SINGLETON);
//             if (end && !encoded) {
//                 return encoder.encode(JieChars.emptyBuffer(), true);
//             }
//             return ret;
//         }
//     }
//
//     static final class BufferingEncoder implements Handler {
//
//         private final Handler encoder;
//         private char @Nullable [] buffer = null;
//
//         BufferingEncoder(Handler encoder) {
//             this.encoder = encoder;
//         }
//
//         @Override
//         public @Nullable CharBuffer encode(CharBuffer data, boolean end) throws Exception {
//             CharBuffer totalBuffer;
//             if (buffer != null) {
//                 CharBuffer newBuffer = CharBuffer.allocate(buffer.length + data.remaining());
//                 newBuffer.put(buffer);
//                 newBuffer.put(data);
//                 newBuffer.flip();
//                 totalBuffer = newBuffer;
//             } else {
//                 totalBuffer = data;
//             }
//             @Nullable CharBuffer ret = encoder.encode(totalBuffer, end);
//             if (end) {
//                 buffer = null;
//                 return ret;
//             }
//             if (totalBuffer.hasRemaining()) {
//                 char[] remainingBuffer = new char[totalBuffer.remaining()];
//                 totalBuffer.get(remainingBuffer);
//                 buffer = remainingBuffer;
//             } else {
//                 buffer = null;
//             }
//             return ret;
//         }
//     }
//
//     static final class EmptyEncoder implements Handler {
//
//         static final EmptyEncoder SINGLETON = new EmptyEncoder();
//
//         @Override
//         public CharBuffer encode(CharBuffer data, boolean end) {
//             return data;
//         }
//     }
//
//     private static void checkSize(int size) {
//         if (size <= 0) {
//             throw new IllegalArgumentException("The size must > 0.");
//         }
//     }
// }
