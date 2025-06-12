// package test;
//
// import org.testng.annotations.Test;
// import xyz.sunqian.common.base.*;
// import xyz.sunqian.common.io.JieIO;
//
// import java.io.ByteArrayOutputStream;
// import java.util.concurrent.Semaphore;
//
// public class ProcessTest {
//
//    private static final String ECHO_CONTENT = "hello world!";
//
//    @Test
//    public void testEcho() throws InterruptedException {
//        if (JieSystem.isLinux() || JieSystem.isMac() || JieSystem.isBsd()) {
//            testEcho("echo " + ECHO_CONTENT);
//        }
//        if (JieSystem.isWindows()) {
//            testEcho("cmd.exe /c echo " + ECHO_CONTENT);
//        }
//    }
//
//    private void testEcho(String command) throws InterruptedException {
//        Process process = Jie.processStarter().command(command).start();
//        process.waitFor();
//        String output = JieIO.readString(process.getInputStream(), JieChars.nativeCharset());
//        process.destroy();
//        System.out.println(output);
//    }
//
//    @Test
//    public void testPing() throws InterruptedException {
//        Process process = Jie.processStarter().command("ping", "-n", "5", "127.0.0.1").start();
//        Semaphore semaphore = new Semaphore(1);
//        semaphore.acquire();
//        Jie.threadStarter().runnable(() -> {
//            while (true) {
//                String output = JieIO.avalaibleString(process.getInputStream(), JieChars.nativeCharset());
//                if (output == null) {
//                    semaphore.release();
//                    return;
//                }
//                if (JieString.isNotEmpty(output)) {
//                    JieLog.system().info(output);
//                }
//                Jie.sleep(1);
//            }
//        }).start();
//        process.waitFor();
//        while (semaphore.hasQueuedThreads()) {
//            Jie.sleep(1000);
//        }
//        process.destroy();
//    }
//
//    @Test
//    public void testPingToOutput() throws InterruptedException {
//        ByteArrayOutputStream dest = new ByteArrayOutputStream();
//        Process process = Jie.processStarter().command("ping", "-n", "5", "127.0.0.1").output(dest).start();
//        process.waitFor();
//        process.destroy();
//        System.out.println(JieString.of(dest.toByteArray(), JieChars.nativeCharset()));
//    }
//}
