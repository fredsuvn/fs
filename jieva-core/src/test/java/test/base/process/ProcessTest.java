package test.base.process;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.process.ProcessReceipt;
import xyz.sunqian.common.base.process.ProcessStarter;

public class ProcessTest {

    @Test
    public void testPing() {
        ProcessReceipt receipt = ProcessStarter.from("ping", "127.0.0.1").start();
        System.out.println(receipt.readString());
    }

    @Test
    public void testProcessStater() {
        ProcessReceipt receipt = ProcessStarter
            .from("ping", "127.0.0.1")
            .mergeErrorOutput()
            .start();
    }
}
