package tests.base.process;

import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.common.base.chars.CharsKit;
import space.sunqian.common.base.process.ProcessKit;
import space.sunqian.common.base.process.VirtualProcess;
import space.sunqian.common.base.system.OSKit;
import space.sunqian.common.io.IOKit;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessTest implements PrintTest {

    @Test
    public void testProcess() {
        {
            Process process;
            if (OSKit.isWindows()) {
                process = ProcessKit.start("cmd.exe", "/c", "dir");
            } else {
                process = ProcessKit.start("ls", "-l");
            }
            printProcess("split cmd", process);
            process.destroyForcibly();
        }
        {
            Process process;
            if (OSKit.isWindows()) {
                process = ProcessKit.start("cmd.exe /c dir");
            } else {
                process = ProcessKit.start("ls -l");
            }
            printProcess("one cmd", process);
            process.destroyForcibly();
        }
    }

    private void printProcess(String title, Process process) {
        InputStream in = process.getInputStream();
        printFor(title, IOKit.string(in, CharsKit.localCharset()));
    }

    @Test
    public void testVirtualProcess() throws Exception {
        {
            VirtualProcess process = new VirtualProcess();
            assertTrue(process.isAlive());
            assertSame(process.alive(false), process);
            assertSame(process.normal(false), process);
        }
        {
            VirtualProcess process = new VirtualProcess();
            assertFalse(process.waitFor(1, TimeUnit.MILLISECONDS));
            assertThrows(IllegalThreadStateException.class, process::exitValue);
            process.destroy();
            assertEquals(0, process.waitFor());
            assertEquals(process.getInputStream(), IOKit.emptyInputStream());
            assertEquals(process.getOutputStream(), IOKit.nullOutputStream());
            assertEquals(process.getErrorStream(), IOKit.emptyInputStream());
        }
        {
            VirtualProcess process = new VirtualProcess();
            assertFalse(process.waitFor(1, TimeUnit.MILLISECONDS));
            assertThrows(IllegalThreadStateException.class, process::exitValue);
            process.normal(false);
            assertSame(process.destroyForcibly(), process);
            assertEquals(1, process.waitFor());
        }
        {
            class ChProcess extends VirtualProcess {

                private final AtomicInteger count = new AtomicInteger();

                @Override
                public synchronized boolean isAlive() {
                    int c = count.incrementAndGet();
                    return c < 3;
                }
            }
            VirtualProcess process = new ChProcess();
            assertEquals(0, process.waitFor());
        }
    }
}
