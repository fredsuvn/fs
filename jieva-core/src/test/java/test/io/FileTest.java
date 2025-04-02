//package test;
//
//import org.testng.Assert;
//import org.testng.annotations.Test;
//import xyz.sunqian.common.base.chars.JieChars;
//import xyz.sunqian.common.base.JieMath;
//import xyz.sunqian.common.file.JieFile;
//import xyz.sunqian.common.ref.LongVar;
//import xyz.sunqian.common.file.FileAcc;
//import xyz.sunqian.common.io.JieIO;
//import xyz.sunqian.common.io.JieIOException;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
//public class FileTest {
//
//    private static final String GENERATED_TEMP_DIR = "generated/temp/test";
//
//    private static final String DATA = TestUtil.buildRandomString(256, 256);
//
//    public static File createFile(String path) {
//        createGeneratedTempDir();
//        File file = new File(GENERATED_TEMP_DIR + "/" + path);
//        return file;
//    }
//
//    public static File createFile(String path, String data) throws IOException {
//        createGeneratedTempDir();
//        File file = new File(GENERATED_TEMP_DIR + "/" + path);
//        FileOutputStream fileOutputStream = new FileOutputStream(file, false);
//        fileOutputStream.write(data.getBytes(JieChars.defaultCharset()));
//        fileOutputStream.close();
//        return file;
//    }
//
//    private static void createGeneratedTempDir() {
//        File firFile = new File(GENERATED_TEMP_DIR);
//        firFile.mkdirs();
//    }
//
//    @Test
//    public void testFile() throws IOException {
//        String data = DATA;
//        byte[] bytes = data.getBytes(JieChars.defaultCharset());
//        File file = createFile("FileTest-testFile.txt", data);
//        FileAcc gekFile = FileAcc.open(file.toPath());
//        Assert.expectThrows(JieIOException.class, () -> gekFile.bindInputStream());
//        gekFile.open("r");
//        gekFile.position(3);
//        InputStream bin = gekFile.bindInputStream();
//        IOTest.testInputStream(data, 3, bytes.length - 3, bin, false);
//        gekFile.position(2);
//        IOTest.testInputStream(data, 2, 130, JieIO.limit(gekFile.bindInputStream(), 130), false);
//        gekFile.close();
//        Assert.expectThrows(JieIOException.class, () -> gekFile.bindInputStream());
//        Assert.expectThrows(JieIOException.class, () -> bin.read());
//        gekFile.open("rw");
//        gekFile.position(3);
//        IOTest.testInputStream(data, 3, bytes.length - 3, bin, false);
//        gekFile.position(2);
//        IOTest.testInputStream(data, 2, 130, JieIO.limit(gekFile.bindInputStream(), 130), false);
//        gekFile.position(4);
//        IOTest.testOutStream(-1, gekFile.bindOutputStream(), (offset, length) ->
//            JieFile.readBytes(file.toPath(), offset + 4, length));
//        long fileLength = gekFile.length();
//        gekFile.position(fileLength);
//        LongVar newLength = LongVar.of(fileLength);
//        IOTest.testOutStream(-1, gekFile.bindOutputStream(), (offset, length) -> {
//            newLength.incrementAndGet(length);
//            return JieFile.readBytes(file.toPath(), offset + fileLength, length);
//        });
//        Assert.assertEquals(gekFile.length(), newLength.get());
//        gekFile.position(8);
//        IOTest.testOutStream(233, JieIO.limit(gekFile.bindOutputStream(), 233), (offset, length) ->
//            JieFile.readBytes(file.toPath(), offset + 8, length));
//        file.delete();
//    }
//
//    @Test
//    public void testFileCacheIO() throws IOException {
//        testFileCacheIO0(3, 4);
//        testFileCacheIO0(4, 3);
//        testFileCacheIO0(3, 40000);
//        testFileCacheIO0(40000, 3);
//    }
//
//    private void testFileCacheIO0(int chunkSize, int bufferSize) throws IOException {
//        String data = DATA;
//        byte[] bytes = data.getBytes(JieChars.defaultCharset());
//        File file = createFile("FileTest-testFileCacheIO.txt", data);
//        GekFileCache fileCache = GekFileCache.newBuilder()
//            .chunkSize(chunkSize)
//            .bufferSize(bufferSize)
//            .build();
//        IOTest.testInputStream(data, 0, bytes.length, fileCache.getInputStream(file.toPath(), 0), false);
//        IOTest.testInputStream(data, 5, 230, JieIO.limit(fileCache.getInputStream(file.toPath(), 5), 230), false);
//        IOTest.testInputStream(data, 0, 230, JieIO.limit(fileCache.getInputStream(file.toPath(), 0), 230), false);
//        IOTest.testInputStream(data, 0, bytes.length, fileCache.getInputStream(file.toPath(), 0), false);
//        IOTest.testOutStream(-1, fileCache.getOutputStream(file.toPath(), 4), (offset, length) ->
//            JieFile.readBytes(file.toPath(), offset + 4, length));
//        long fileLength = file.length();
//        LongVar newLength = LongVar.of(fileLength);
//        IOTest.testOutStream(-1, fileCache.getOutputStream(file.toPath(), fileLength), (offset, length) -> {
//            newLength.incrementAndGet(length);
//            return JieFile.readBytes(file.toPath(), offset + fileLength, length);
//        });
//        Assert.assertEquals(file.length(), newLength.get());
//        IOTest.testOutStream(233, JieIO.limit(fileCache.getOutputStream(file.toPath(), 0), 233), (offset, length) ->
//            JieFile.readBytes(file.toPath(), offset, length));
//        IOTest.testOutStream(233, JieIO.limit(fileCache.getOutputStream(file.toPath(), 3), 233), (offset, length) ->
//            JieFile.readBytes(file.toPath(), offset + 3, length));
//        file.delete();
//    }
//
//    @Test
//    public void testFileCache() throws IOException {
//        String data = "01234567890123456789";
//        byte[] bytes1 = data.getBytes(JieChars.defaultCharset());
//        byte[] bytes2 = (data + data + data).getBytes(JieChars.defaultCharset());
//        File file1 = createFile("FileTest-testFileCache1.txt", data);
//        File file2 = createFile("FileTest-testFileCache2.txt", data + data + data);
//        LongVar cacheRead = LongVar.of(0);
//        LongVar fileRead = LongVar.of(0);
//        LongVar cacheWrite = LongVar.of(0);
//        LongVar fileWrite = LongVar.of(0);
//        GekFileCache fileCache = GekFileCache.newBuilder()
//            .chunkSize(3)
//            .bufferSize(4)
//            .cacheReadListener((path, offset, length) -> cacheRead.incrementAndGet(length))
//            .fileReadListener((path, offset, length) -> fileRead.incrementAndGet(length))
//            .cacheWriteListener((path, offset, length) -> cacheWrite.incrementAndGet(length))
//            .fileWriteListener((path, offset, length) -> fileWrite.incrementAndGet(length))
//            .build();
//        byte[] dest = new byte[bytes1.length * 4];
//        fileCache.getInputStream(file1.toPath(), 0).read(dest);
//        Assert.assertEquals(fileCache.cachedChunkCount(), JieMath.leastPortion(bytes1.length, 3));
//        fileCache.getInputStream(file2.toPath(), 0).read(dest);
//        Assert.assertEquals(fileCache.cachedChunkCount(), JieMath.leastPortion(bytes1.length + bytes2.length, 3) + 1);
//        Assert.assertEquals(cacheRead.get(), 0);
//        Assert.assertEquals(fileRead.get(), bytes1.length + bytes2.length);
//        Assert.assertEquals(cacheWrite.get(), bytes1.length + bytes2.length);
//
//        fileCache.getInputStream(file1.toPath(), 0).read(dest);
//        Assert.assertEquals(cacheRead.get(), bytes1.length);
//        Assert.assertEquals(fileRead.get(), bytes1.length + bytes2.length);
//
//        fileCache.getInputStream(file2.toPath(), 0).read(dest);
//        Assert.assertEquals(cacheRead.get(), bytes1.length + bytes2.length);
//        Assert.assertEquals(fileRead.get(), bytes1.length + bytes2.length);
//
//        fileCache.getOutputStream(file1.toPath(), 10).write(new byte[5]);
//        Assert.assertEquals(cacheRead.get(), bytes1.length + bytes2.length);
//        Assert.assertEquals(fileRead.get(), bytes1.length + bytes2.length);
//        Assert.assertEquals(cacheWrite.get(), bytes1.length + bytes2.length);
//        Assert.assertEquals(fileWrite.get(), 5);
//
//        fileCache.getInputStream(file1.toPath(), 0).read(dest);
//        Assert.assertEquals(cacheRead.get(), bytes1.length + bytes2.length);
//        Assert.assertEquals(fileRead.get(), bytes1.length + bytes2.length + bytes1.length);
//        Assert.assertEquals(cacheWrite.get(), bytes1.length + bytes2.length + bytes1.length);
//        Assert.assertEquals(fileWrite.get(), 5);
//
//        fileCache.setFileLength(file1.toPath(), 40);
//        fileCache.getOutputStream(file1.toPath(), 10).write(new byte[25]);
//        fileCache.getInputStream(file1.toPath(), 0).read(dest);
//        Assert.assertEquals(cacheRead.get(), bytes1.length + bytes2.length);
//        Assert.assertEquals(fileRead.get(), bytes1.length + bytes2.length + bytes1.length + 40);
//        Assert.assertEquals(cacheWrite.get(), bytes1.length + bytes2.length + bytes1.length + 40);
//        Assert.assertEquals(fileWrite.get(), 5 + 25);
//
//        file1.delete();
//        file2.delete();
//    }
//}
