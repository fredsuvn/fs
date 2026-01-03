package tests.io.file;

import internal.test.AssertTest;
import internal.test.DataTest;
import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.io.BufferKit;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.io.IORuntimeException;
import space.sunqian.fs.io.file.FileKit;
import space.sunqian.fs.io.file.FileRef;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        URL url = ClassLoader.getSystemResource("io/fileRef.txt");
        Path path = Paths.get(url.toURI());
        // of
        assertEquals(FileRef.of(url).getPath(), path);
        assertEquals(FileRef.of(url.toURI()).getPath(), path);
        assertEquals(FileRef.of(path.toString()).getPath(), path);
        Path sub = path.resolve("sub");
        assertEquals(FileRef.of(path.toString(), "sub").getPath(), sub);
        URL errUrl = new URL("http://www.123.456");
        assertThrows(IORuntimeException.class, () -> FileRef.of(errUrl));

        // FileRef
        File file = path.toFile();
        Path errPath = path.resolveSibling("/sub/fileRef2.txt");
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
        assertThrows(IORuntimeException.class, errRef::getBasicFileAttributes);
        assertEquals(fileRef.getContentType(), Files.probeContentType(path));

        String hello = "hello";
        byte[] helloBytes = hello.getBytes(CharsKit.defaultCharset());
        // File Stream
        try (FileOutputStream out = fileRef.fileOutputStream()) {
            out.write(helloBytes);
        }
        assertThrows(IORuntimeException.class, errRef::fileOutputStream);
        try (FileInputStream in = fileRef.fileInputStream()) {
            assertEquals(hello, IOKit.string(in));
        }
        assertThrows(IORuntimeException.class, errRef::fileInputStream);
        // new Stream
        try (OutputStream out = fileRef.newOutputStream()) {
            out.write(helloBytes);
        }
        assertThrows(IORuntimeException.class, errRef::newOutputStream);
        try (InputStream in = fileRef.newInputStream()) {
            assertEquals(hello, IOKit.string(in));
        }
        assertThrows(IORuntimeException.class, errRef::newInputStream);
        // File Channel
        try (FileChannel channel = fileRef.newFileChannel(StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            BufferKit.readTo(ByteBuffer.wrap(helloBytes), channel);
            channel.position(0);
            ByteBuffer ret = ByteBuffer.allocate(helloBytes.length);
            assertEquals(IOKit.readTo(channel, ret), helloBytes.length);
            ret.flip();
            assertArrayEquals(BufferKit.copyContent(ret), helloBytes);
        }
        assertThrows(IORuntimeException.class, errRef::newFileChannel);
        // read
        assertEquals(hello, fileRef.readString());
        assertEquals(fileRef.readLines(), Collections.singletonList(hello));
        assertThrows(IORuntimeException.class, errRef::readString);
        assertThrows(IORuntimeException.class, errRef::readLines);
        // write
        String world = "world";
        fileRef.writeString(world);
        assertEquals(world, fileRef.readString());
        assertThrows(IORuntimeException.class, () -> errRef.writeString(world));
    }
}
