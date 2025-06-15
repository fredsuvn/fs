package test.base.process;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.JieSystem;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.base.exception.AwaitingException;
import xyz.sunqian.common.base.process.JieProcess;
import xyz.sunqian.common.base.process.ProcessReceipt;
import xyz.sunqian.common.base.process.VirtualProcess;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.common.task.TaskState;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class ProcessTest {

    @Test
    public void testProcess() {
        {
            if (JieSystem.isWindows()) {
                ProcessReceipt receipt = JieProcess.start("cmd.exe", "/c", "dir");
                receipt.getProcess().destroyForcibly();
            } else {
                ProcessReceipt receipt = JieProcess.start("ls", "-l");
                receipt.getProcess().destroyForcibly();
            }
        }
        {
            if (JieSystem.isWindows()) {
                ProcessReceipt receipt = JieProcess.start("cmd.exe /c dir");
                receipt.getProcess().destroyForcibly();
            } else {
                ProcessReceipt receipt = JieProcess.start("ls -l");
                receipt.getProcess().destroyForcibly();
            }
        }
    }

    @Test
    public void testReceipt() throws Exception {
        {
            VirtualProcess process = new VirtualProcess();
            ProcessReceipt receipt = JieProcess.receipt(process);
            assertEquals(receipt.getState(), TaskState.EXECUTING);
            try {
                receipt.await(1);
            } catch (AwaitingException e) {
                assertTrue(e.getCause() instanceof TimeoutException);
            }
            try {
                receipt.await(Duration.ofMillis(1));
            } catch (AwaitingException e) {
                assertTrue(e.getCause() instanceof TimeoutException);
            }
            assertFalse(receipt.isCancelled());
            assertFalse(receipt.isDone());
            CountDownLatch latch = new CountDownLatch(1);
            Jie.run(() -> {
                int result = receipt.await();
                assertEquals(result, 0);
                latch.countDown();
            });
            receipt.cancel();
            latch.await();
            assertTrue(receipt.isCancelled());
            assertTrue(receipt.isDone());
            assertEquals(receipt.getState(), TaskState.CANCELED_EXECUTING);
            process.alive(true);
            assertFalse(receipt.isCancelled());
            assertFalse(receipt.isDone());
        }
        {
            VirtualProcess process = new VirtualProcess();
            ProcessReceipt receipt = JieProcess.receipt(process);
            assertEquals(receipt.getState(), TaskState.EXECUTING);
            try {
                receipt.await(1);
            } catch (AwaitingException e) {
                assertTrue(e.getCause() instanceof TimeoutException);
            }
            try {
                receipt.await(Duration.ofMillis(1));
            } catch (AwaitingException e) {
                assertTrue(e.getCause() instanceof TimeoutException);
            }
            assertFalse(receipt.isCancelled());
            assertFalse(receipt.isDone());
            CountDownLatch latch = new CountDownLatch(1);
            Jie.run(() -> {
                int result = receipt.await();
                assertEquals(result, 0);
                latch.countDown();
            });
            receipt.cancel(false);
            latch.await();
            assertTrue(receipt.isCancelled());
            assertTrue(receipt.isDone());
            assertEquals(receipt.getState(), TaskState.CANCELED_EXECUTING);
        }
        {
            VirtualProcess process = new VirtualProcess();
            ProcessReceipt receipt = JieProcess.receipt(process);
            assertEquals(receipt.getState(), TaskState.EXECUTING);
            assertFalse(receipt.isCancelled());
            process.destroy();
            assertFalse(receipt.isCancelled());
            assertEquals(receipt.getState(), TaskState.SUCCEEDED);
            assertEquals(receipt.await(1), 0);
            process.normal(false);
            assertEquals(receipt.getState(), TaskState.FAILED);
            assertEquals(receipt.await(Duration.ofMillis(1)), 1);
            assertEquals(receipt.getInputStream(), JieIO.emptyInStream());
            assertEquals(receipt.getOutputStream(), JieIO.nullOutStream());
            assertEquals(receipt.getErrorStream(), JieIO.emptyInStream());
            assertNull(receipt.getException());
            assertNull(receipt.getDelay());
        }
        {
            String hello = "hello";
            VirtualProcess process = new VirtualProcess(
                JieIO.inStream(hello.getBytes(JieChars.localCharset())),
                JieIO.nullOutStream()
            );
            ProcessReceipt receipt = JieProcess.receipt(process);
            assertEquals(receipt.readString(), hello);
        }
        {
            String hello = "hello";
            VirtualProcess process = new VirtualProcess(
                JieIO.inStream(hello.getBytes(JieChars.localCharset())),
                JieIO.nullOutStream()
            );
            ProcessReceipt receipt = JieProcess.receipt(process);
            assertEquals(receipt.readBytes(), hello.getBytes(JieChars.localCharset()));
        }
    }

    @Test
    public void testVirtualProcess() throws Exception {
        {
            VirtualProcess process = new VirtualProcess();
            assertFalse(process.waitFor(1, TimeUnit.MILLISECONDS));
            expectThrows(IllegalThreadStateException.class, process::exitValue);
            process.destroy();
            assertEquals(process.waitFor(), 0);
            assertEquals(process.getInputStream(), JieIO.emptyInStream());
            assertEquals(process.getOutputStream(), JieIO.nullOutStream());
            assertEquals(process.getErrorStream(), JieIO.emptyInStream());
        }
        {
            VirtualProcess process = new VirtualProcess();
            assertFalse(process.waitFor(1, TimeUnit.MILLISECONDS));
            expectThrows(IllegalThreadStateException.class, process::exitValue);
            process.normal(false);
            process.destroy();
            assertEquals(process.waitFor(), 1);
        }
    }
}
