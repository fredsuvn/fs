package test.base.process;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.base.exception.AwaitingException;
import xyz.sunqian.common.base.process.JieProcess;
import xyz.sunqian.common.base.process.ProcessReceipt;
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

    // @Test
    // public void testPing() {
    //     ProcessReceipt receipt = ProcessStarter.from("ping", "127.0.0.1").start();
    //     System.out.println(receipt.readString());
    // }
    //
    // @Test
    // public void testProcessStater() {
    //     ProcessReceipt receipt = ProcessStarter
    //         .from("ping", "127.0.0.1")
    //         .mergeErrorOutput()
    //         .start();
    // }

    @Test
    public void testReceipt() throws Exception {
        {
            Process process = JieProcess.virtualProcess();
            ProcessReceipt receipt = JieProcess.receipt(process);
            assertEquals(receipt.getState(), TaskState.EXECUTING);
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
                assertEquals(result, 1);
                latch.countDown();
            });
            receipt.cancel();
            assertTrue(receipt.isCancelled());
            assertTrue(receipt.isDone());
            latch.await();
            assertEquals(receipt.getState(), TaskState.CANCELED_EXECUTING);
        }
        {
            Process process = JieProcess.virtualProcess();
            ProcessReceipt receipt = JieProcess.receipt(process);
            assertEquals(receipt.getState(), TaskState.EXECUTING);
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
            assertTrue(receipt.isCancelled());
            assertTrue(receipt.isDone());
            latch.await();
            assertEquals(receipt.getState(), TaskState.CANCELED_EXECUTING);
        }
        {
            Process process = JieProcess.virtualProcess();
            ProcessReceipt receipt = JieProcess.receipt(process);
            assertEquals(receipt.getState(), TaskState.EXECUTING);
            process.destroy();
            assertEquals(receipt.getState(), TaskState.SUCCEEDED);
            assertEquals(receipt.await(Duration.ofMillis(1)), 0);
            assertEquals(receipt.getInputStream(), JieIO.emptyInStream());
            assertEquals(receipt.getOutputStream(), JieIO.nullOutStream());
            assertEquals(receipt.getErrorStream(), JieIO.emptyInStream());
            assertNull(receipt.getException());
            assertNull(receipt.getDelay());
        }
        {
            String hello = "hello";
            Process process = JieProcess.virtualProcess(
                JieIO.inStream(hello.getBytes(JieChars.localCharset())),
                JieIO.inStream(hello.getBytes(JieChars.localCharset())),
                JieIO.nullOutStream()
            );
            ProcessReceipt receipt = JieProcess.receipt(process);
            assertEquals(receipt.getState(), TaskState.EXECUTING);
            process.destroyForcibly();
            assertEquals(receipt.getState(), TaskState.FAILED);
            assertEquals(receipt.readString(), hello);
        }
    }

    @Test
    public void testVirtualProcess() throws Exception {
        {
            Process process = JieProcess.virtualProcess();
            assertFalse(process.waitFor(1, TimeUnit.MILLISECONDS));
            expectThrows(IllegalThreadStateException.class, process::exitValue);
            CountDownLatch latch = new CountDownLatch(1);
            Jie.run(() -> {
                try {
                    int result = process.waitFor();
                    assertEquals(result, 0);
                    latch.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            process.destroy();
            latch.await();
            assertEquals(process.getInputStream(), JieIO.emptyInStream());
            assertEquals(process.getOutputStream(), JieIO.nullOutStream());
            assertEquals(process.getErrorStream(), JieIO.emptyInStream());
        }
        {
            Process process = JieProcess.virtualProcess();
            assertFalse(process.waitFor(1, TimeUnit.MILLISECONDS));
            expectThrows(IllegalThreadStateException.class, process::exitValue);
            CountDownLatch latch = new CountDownLatch(1);
            Jie.run(() -> {
                try {
                    int result = process.waitFor();
                    assertEquals(result, 1);
                    latch.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            process.destroyForcibly();
            latch.await();
        }

    }
}
