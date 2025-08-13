// package test.concurrent;
//
// import org.testng.annotations.Test;
// import test.utils.FlagException;
// import xyz.sunqian.common.base.exception.WrappedException;
//
// import java.util.concurrent.Callable;
//
// import static org.testng.Assert.assertEquals;
// import static org.testng.Assert.expectThrows;
//
// public class FutureTest {
//
//     @Test
//     public void testToRunnable() {
//         int[] i = {0};
//         Runnable runnable = TaskKit.toRunnable(() -> {
//             i[0]++;
//             return null;
//         });
//         assertEquals(i[0], 0);
//         runnable.run();
//         assertEquals(i[0], 1);
//         RunnableCall<?> work1 = new RunnableCall<Object>() {
//             @Override
//             public void run() {
//                 i[0]++;
//             }
//
//             @Override
//             public Object call() {
//                 i[0]++;
//                 return null;
//             }
//         };
//         Runnable workRunnable1 = TaskKit.toRunnable(work1);
//         workRunnable1.run();
//         assertEquals(i[0], 2);
//         Callable<?> work2 = (Callable<Object>) () -> {
//             throw new FlagException(i);
//         };
//         Runnable workRunnable2 = TaskKit.toRunnable(work2);
//         expectThrows(WrappedException.class, workRunnable2::run);
//         assertEquals(i[0], 3);
//     }
//
//     @Test
//     public void testExceptionConstructors() {
//         // SubmissionException
//         expectThrows(ExecutorException.class, () -> {
//             throw new ExecutorException();
//         });
//         expectThrows(ExecutorException.class, () -> {
//             throw new ExecutorException("");
//         });
//         expectThrows(ExecutorException.class, () -> {
//             throw new ExecutorException("", new RuntimeException());
//         });
//         expectThrows(ExecutorException.class, () -> {
//             throw new ExecutorException(new RuntimeException());
//         });
//     }
//
//     private interface RunnableCall<T> extends Runnable, Callable<T> {
//     }
// }
