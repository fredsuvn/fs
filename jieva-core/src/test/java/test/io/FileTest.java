package test.io;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.file.FileKit;
import xyz.sunqian.common.io.file.FileRef;
import xyz.sunqian.test.AssertTest;
import xyz.sunqian.test.DataTest;
import xyz.sunqian.test.PrintTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.expectThrows;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class FileTest implements DataTest, PrintTest, AssertTest {

    @Test
    public void testTempDirAndFile() throws Exception {
        Path tempDir = FileKit.getTempDir();
        printFor("tempDir", tempDir);
        {
            // create temp files
            Path tempFile = FileKit.createTempFile(null, null);
            printFor("tempFile", tempFile);
            assertTrue(tempFile.toFile().exists());
            tempFile.toFile().delete();
            assertFalse(tempFile.toFile().exists());
            Path tempTempDir = FileKit.createTempDir(null);
            printFor("tempTempDir", tempTempDir);
            assertTrue(tempTempDir.toFile().exists());
            tempTempDir.toFile().delete();
            assertFalse(tempTempDir.toFile().exists());
            // exceptions
            Method createTempFile = FileKit.class.getDeclaredMethod(
                "createTempFile", Path.class, String.class, String.class, FileAttribute[].class);
            invokeThrows(IORuntimeException.class, createTempFile, null, null, null, null, null);
            Method createTempDir = FileKit.class.getDeclaredMethod(
                "createTempDir", Path.class, String.class, FileAttribute[].class);
            invokeThrows(IORuntimeException.class, createTempDir, null, null, null, null);
        }
    }

    @Test
    public void testFileRef() throws Exception {
        Path path = Paths.get(ClassLoader.getSystemResource("io/fileRef.txt").toURI());
        File file = path.toFile();
        Path errPath = path.resolveSibling("fileRef2.txt");
        File errFile = errPath.toFile();
        FileRef fileRef = FileRef.of(path);
        assertEquals(fileRef.getPath(), path);
        assertEquals(fileRef.getFile(), file);
        assertEquals(fileRef.getFile(), file);
        FileRef errRef = FileRef.of(errFile);
        assertEquals(errRef.getFile(), errFile);
        assertEquals(errRef.getPath(), errPath);
        assertEquals(errRef.getPath(), errPath);
        BasicFileAttributes attributes = fileRef.getBasicFileAttributes();
        BasicFileAttributes attributes2 = Files.readAttributes(path, BasicFileAttributes.class);
        assertEquals(attributes.creationTime(), attributes2.creationTime());
        assertEquals(attributes.size(), attributes2.size());
        expectThrows(IORuntimeException.class, errRef::getBasicFileAttributes);

        String hello = "hello";
        byte[] helloBytes = hello.getBytes(CharsKit.defaultCharset());
        // File Stream
        try (FileOutputStream out = fileRef.fileOutputStream()) {
            out.write(helloBytes);
            out.flush();
        }
        try (FileInputStream in = fileRef.fileInputStream()) {
            assertEquals(hello, IOKit.string(in));
        }
    }

    // private static final String GENERATED_TEMP_DIR = "generated/temp/test";
    //
    // private static final String DATA = TestUtil.buildRandomString(256, 256);
    //
    // public static File createFile(String path) {
    //     createGeneratedTempDir();
    //     File file = new File(GENERATED_TEMP_DIR + "/" + path);
    //     return file;
    // }
    //
    // public static File createFile(String path, String data) throws IOException {
    //     createGeneratedTempDir();
    //     File file = new File(GENERATED_TEMP_DIR + "/" + path);
    //     FileOutputStream fileOutputStream = new FileOutputStream(file, false);
    //     fileOutputStream.write(data.getBytes(JieChars.defaultCharset()));
    //     fileOutputStream.close();
    //     return file;
    // }
    //
    // private static void createGeneratedTempDir() {
    //     File firFile = new File(GENERATED_TEMP_DIR);
    //     firFile.mkdirs();
    // }
    //
    // @Test
    // public void testFile() throws IOException {
    //     String data = DATA;
    //     byte[] bytes = data.getBytes(JieChars.defaultCharset());
    //     File file = createFile("FileTest-testFile.txt", data);
    //     FileAcc gekFile = FileAcc.open(file.toPath());
    //     Assert.expectThrows(JieIOException.class, () -> gekFile.bindInputStream());
    //     gekFile.open("r");
    //     gekFile.position(3);
    //     InputStream bin = gekFile.bindInputStream();
    //     IOTest.testInputStream(data, 3, bytes.length - 3, bin, false);
    //     gekFile.position(2);
    //     IOTest.testInputStream(data, 2, 130, JieIO.limit(gekFile.bindInputStream(), 130), false);
    //     gekFile.close();
    //     Assert.expectThrows(JieIOException.class, () -> gekFile.bindInputStream());
    //     Assert.expectThrows(JieIOException.class, () -> bin.read());
    //     gekFile.open("rw");
    //     gekFile.position(3);
    //     IOTest.testInputStream(data, 3, bytes.length - 3, bin, false);
    //     gekFile.position(2);
    //     IOTest.testInputStream(data, 2, 130, JieIO.limit(gekFile.bindInputStream(), 130), false);
    //     gekFile.position(4);
    //     IOTest.testOutStream(-1, gekFile.bindOutputStream(), (offset, length) ->
    //         JieFile.readBytes(file.toPath(), offset + 4, length));
    //     long fileLength = gekFile.length();
    //     gekFile.position(fileLength);
    //     LongVar newLength = LongVar.of(fileLength);
    //     IOTest.testOutStream(-1, gekFile.bindOutputStream(), (offset, length) -> {
    //         newLength.incrementAndGet(length);
    //         return JieFile.readBytes(file.toPath(), offset + fileLength, length);
    //     });
    //     Assert.assertEquals(gekFile.length(), newLength.get());
    //     gekFile.position(8);
    //     IOTest.testOutStream(233, JieIO.limit(gekFile.bindOutputStream(), 233), (offset, length) ->
    //         JieFile.readBytes(file.toPath(), offset + 8, length));
    //     file.delete();
    // }
    //
    // @Test
    // public void testFileCacheIO() throws IOException {
    //     testFileCacheIO0(3, 4);
    //     testFileCacheIO0(4, 3);
    //     testFileCacheIO0(3, 40000);
    //     testFileCacheIO0(40000, 3);
    // }
    //
    // private void testFileCacheIO0(int chunkSize, int bufferSize) throws IOException {
    //     String data = DATA;
    //     byte[] bytes = data.getBytes(JieChars.defaultCharset());
    //     File file = createFile("FileTest-testFileCacheIO.txt", data);
    //     GekFileCache fileCache = GekFileCache.newBuilder()
    //         .chunkSize(chunkSize)
    //         .bufferSize(bufferSize)
    //         .build();
    //     IOTest.testInputStream(data, 0, bytes.length, fileCache.getInputStream(file.toPath(), 0), false);
    //     IOTest.testInputStream(data, 5, 230, JieIO.limit(fileCache.getInputStream(file.toPath(), 5), 230), false);
    //     IOTest.testInputStream(data, 0, 230, JieIO.limit(fileCache.getInputStream(file.toPath(), 0), 230), false);
    //     IOTest.testInputStream(data, 0, bytes.length, fileCache.getInputStream(file.toPath(), 0), false);
    //     IOTest.testOutStream(-1, fileCache.getOutputStream(file.toPath(), 4), (offset, length) ->
    //         JieFile.readBytes(file.toPath(), offset + 4, length));
    //     long fileLength = file.length();
    //     LongVar newLength = LongVar.of(fileLength);
    //     IOTest.testOutStream(-1, fileCache.getOutputStream(file.toPath(), fileLength), (offset, length) -> {
    //         newLength.incrementAndGet(length);
    //         return JieFile.readBytes(file.toPath(), offset + fileLength, length);
    //     });
    //     Assert.assertEquals(file.length(), newLength.get());
    //     IOTest.testOutStream(233, JieIO.limit(fileCache.getOutputStream(file.toPath(), 0), 233), (offset, length) ->
    //         JieFile.readBytes(file.toPath(), offset, length));
    //     IOTest.testOutStream(233, JieIO.limit(fileCache.getOutputStream(file.toPath(), 3), 233), (offset, length) ->
    //         JieFile.readBytes(file.toPath(), offset + 3, length));
    //     file.delete();
    // }
    //
    // @Test
    // public void testFileCache() throws IOException {
    //     String data = "01234567890123456789";
    //     byte[] bytes1 = data.getBytes(JieChars.defaultCharset());
    //     byte[] bytes2 = (data + data + data).getBytes(JieChars.defaultCharset());
    //     File file1 = createFile("FileTest-testFileCache1.txt", data);
    //     File file2 = createFile("FileTest-testFileCache2.txt", data + data + data);
    //     LongVar cacheRead = LongVar.of(0);
    //     LongVar fileRead = LongVar.of(0);
    //     LongVar cacheWrite = LongVar.of(0);
    //     LongVar fileWrite = LongVar.of(0);
    //     GekFileCache fileCache = GekFileCache.newBuilder()
    //         .chunkSize(3)
    //         .bufferSize(4)
    //         .cacheReadListener((path, offset, length) -> cacheRead.incrementAndGet(length))
    //         .fileReadListener((path, offset, length) -> fileRead.incrementAndGet(length))
    //         .cacheWriteListener((path, offset, length) -> cacheWrite.incrementAndGet(length))
    //         .fileWriteListener((path, offset, length) -> fileWrite.incrementAndGet(length))
    //         .build();
    //     byte[] dest = new byte[bytes1.length * 4];
    //     fileCache.getInputStream(file1.toPath(), 0).read(dest);
    //     Assert.assertEquals(fileCache.cachedChunkCount(), JieMath.leastPortion(bytes1.length, 3));
    //     fileCache.getInputStream(file2.toPath(), 0).read(dest);
    //     Assert.assertEquals(fileCache.cachedChunkCount(), JieMath.leastPortion(bytes1.length + bytes2.length, 3) + 1);
    //     Assert.assertEquals(cacheRead.get(), 0);
    //     Assert.assertEquals(fileRead.get(), bytes1.length + bytes2.length);
    //     Assert.assertEquals(cacheWrite.get(), bytes1.length + bytes2.length);
    //
    //     fileCache.getInputStream(file1.toPath(), 0).read(dest);
    //     Assert.assertEquals(cacheRead.get(), bytes1.length);
    //     Assert.assertEquals(fileRead.get(), bytes1.length + bytes2.length);
    //
    //     fileCache.getInputStream(file2.toPath(), 0).read(dest);
    //     Assert.assertEquals(cacheRead.get(), bytes1.length + bytes2.length);
    //     Assert.assertEquals(fileRead.get(), bytes1.length + bytes2.length);
    //
    //     fileCache.getOutputStream(file1.toPath(), 10).write(new byte[5]);
    //     Assert.assertEquals(cacheRead.get(), bytes1.length + bytes2.length);
    //     Assert.assertEquals(fileRead.get(), bytes1.length + bytes2.length);
    //     Assert.assertEquals(cacheWrite.get(), bytes1.length + bytes2.length);
    //     Assert.assertEquals(fileWrite.get(), 5);
    //
    //     fileCache.getInputStream(file1.toPath(), 0).read(dest);
    //     Assert.assertEquals(cacheRead.get(), bytes1.length + bytes2.length);
    //     Assert.assertEquals(fileRead.get(), bytes1.length + bytes2.length + bytes1.length);
    //     Assert.assertEquals(cacheWrite.get(), bytes1.length + bytes2.length + bytes1.length);
    //     Assert.assertEquals(fileWrite.get(), 5);
    //
    //     fileCache.setFileLength(file1.toPath(), 40);
    //     fileCache.getOutputStream(file1.toPath(), 10).write(new byte[25]);
    //     fileCache.getInputStream(file1.toPath(), 0).read(dest);
    //     Assert.assertEquals(cacheRead.get(), bytes1.length + bytes2.length);
    //     Assert.assertEquals(fileRead.get(), bytes1.length + bytes2.length + bytes1.length + 40);
    //     Assert.assertEquals(cacheWrite.get(), bytes1.length + bytes2.length + bytes1.length + 40);
    //     Assert.assertEquals(fileWrite.get(), 5 + 25);
    //
    //     file1.delete();
    //     file2.delete();
    // }

    private static final class ErrorFileSystem extends FileSystem {

        @Override
        public FileSystemProvider provider() {
            return new ErrorFileSystemProvider();
        }

        @Override
        public void close() throws IOException {

        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public boolean isReadOnly() {
            return false;
        }

        @Override
        public String getSeparator() {
            return "";
        }

        @Override
        public Iterable<Path> getRootDirectories() {
            return null;
        }

        @Override
        public Iterable<FileStore> getFileStores() {
            return null;
        }

        @Override
        public Set<String> supportedFileAttributeViews() {
            return Collections.emptySet();
        }

        @NotNull
        @Override
        public Path getPath(@NotNull String first, @NotNull String... more) {
            return null;
        }

        @Override
        public PathMatcher getPathMatcher(String syntaxAndPattern) {
            return null;
        }

        @Override
        public UserPrincipalLookupService getUserPrincipalLookupService() {
            return null;
        }

        @Override
        public WatchService newWatchService() throws IOException {
            return null;
        }
    }

    private static final class ErrorFileSystemProvider extends FileSystemProvider {

        @Override
        public String getScheme() {
            return "";
        }

        @Override
        public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
            return null;
        }

        @Override
        public FileSystem getFileSystem(URI uri) {
            return null;
        }

        @NotNull
        @Override
        public Path getPath(@NotNull URI uri) {
            return null;
        }

        @Override
        public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
            return null;
        }

        @Override
        public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
            return null;
        }

        @Override
        public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {

        }

        @Override
        public void delete(Path path) throws IOException {

        }

        @Override
        public void copy(Path source, Path target, CopyOption... options) throws IOException {

        }

        @Override
        public void move(Path source, Path target, CopyOption... options) throws IOException {

        }

        @Override
        public boolean isSameFile(Path path, Path path2) throws IOException {
            return false;
        }

        @Override
        public boolean isHidden(Path path) throws IOException {
            return false;
        }

        @Override
        public FileStore getFileStore(Path path) throws IOException {
            return null;
        }

        @Override
        public void checkAccess(Path path, AccessMode... modes) throws IOException {

        }

        @Override
        public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
            return null;
        }

        @Override
        public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
            return null;
        }

        @Override
        public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
            return Collections.emptyMap();
        }

        @Override
        public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {

        }
    }
}
