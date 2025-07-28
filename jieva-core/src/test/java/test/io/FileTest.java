package test.io;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.io.BufferKit;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

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
        expectThrows(IORuntimeException.class, errRef::getBasicFileAttributes);

        String hello = "hello";
        byte[] helloBytes = hello.getBytes(CharsKit.defaultCharset());
        // File Stream
        try (FileOutputStream out = fileRef.fileOutputStream()) {
            out.write(helloBytes);
        }
        expectThrows(IORuntimeException.class, errRef::fileOutputStream);
        try (FileInputStream in = fileRef.fileInputStream()) {
            assertEquals(IOKit.string(in), hello);
        }
        expectThrows(IORuntimeException.class, errRef::fileInputStream);
        // new Stream
        try (OutputStream out = fileRef.newOutputStream()) {
            out.write(helloBytes);
        }
        expectThrows(IORuntimeException.class, errRef::newOutputStream);
        try (InputStream in = fileRef.newInputStream()) {
            assertEquals(IOKit.string(in), hello);
        }
        expectThrows(IORuntimeException.class, errRef::newInputStream);
        // File Channel
        try (FileChannel channel = fileRef.newFileChannel(StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            BufferKit.readTo(ByteBuffer.wrap(helloBytes), channel);
            channel.position(0);
            ByteBuffer ret = ByteBuffer.allocate(helloBytes.length);
            assertEquals(IOKit.readTo(channel, ret), helloBytes.length);
            ret.flip();
            assertEquals(BufferKit.copyContent(ret), helloBytes);
        }
        expectThrows(IORuntimeException.class, errRef::newFileChannel);
        // read
        assertEquals(fileRef.readString(), hello);
        assertEquals(fileRef.readLines(), Collections.singleton(hello));
        expectThrows(IORuntimeException.class, errRef::readString);
        expectThrows(IORuntimeException.class, errRef::readLines);
        // write
        String world = "world";
        fileRef.writeString(world);
        assertEquals(fileRef.readString(), world);
        expectThrows(IORuntimeException.class, () -> errRef.writeString(world));
    }
}
