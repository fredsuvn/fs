package test.base.process;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.process.JieProcess;
import xyz.sunqian.common.base.process.ProcessReceipt;

public class ProcessTest {

    @Test
    public void testPing() {
        ProcessReceipt receipt = JieProcess.start("ping", "127.0.0.1");
        System.out.println(receipt.readString());
    }
}
