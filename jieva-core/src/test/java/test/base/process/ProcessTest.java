package test.base.process;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.base.process.ProcessKit;
import xyz.sunqian.common.base.process.VirtualProcess;
import xyz.sunqian.common.base.system.OSKit;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.test.PrintTest;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

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
            expectThrows(IllegalThreadStateException.class, process::exitValue);
            process.destroy();
            assertEquals(process.waitFor(), 0);
            assertEquals(process.getInputStream(), IOKit.emptyInputStream());
            assertEquals(process.getOutputStream(), IOKit.nullOutputStream());
            assertEquals(process.getErrorStream(), IOKit.emptyInputStream());
        }
        {
            VirtualProcess process = new VirtualProcess();
            assertFalse(process.waitFor(1, TimeUnit.MILLISECONDS));
            expectThrows(IllegalThreadStateException.class, process::exitValue);
            process.normal(false);
            assertSame(process.destroyForcibly(), process);
            assertEquals(process.waitFor(), 1);
        }
    }
}
