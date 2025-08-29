package tests.base.string;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.exception.UnreachablePointException;
import xyz.sunqian.common.base.string.StringView;
import xyz.sunqian.common.collect.ListKit;
import xyz.sunqian.test.PrintTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class StringViewTest implements PrintTest {

    @Test
    public void testStringView() throws Exception {
        String fullString = "0123456789";
        List<String> strings = ListKit.list("012", "345", "678", "9");
        StringView sv = StringView.of(strings);
        assertEquals(sv.length(), 10);
        assertEquals(sv.toString(), fullString);
        for (int i = 0; i < 10; i++) {
            assertEquals(sv.charAt(i), i + '0');
        }
        for (int start = 0; start < fullString.length(); start++) {
            for (int end = start; end < fullString.length(); end++) {
                assertEquals(sv.subSequence(start, end).toString(), fullString.subSequence(start, end).toString());
            }
        }
        expectThrows(IndexOutOfBoundsException.class, () -> sv.charAt(-1));
        expectThrows(IndexOutOfBoundsException.class, () -> sv.subSequence(-1, 5));
        expectThrows(IndexOutOfBoundsException.class, () -> sv.subSequence(0, 11));
        expectThrows(IndexOutOfBoundsException.class, () -> sv.subSequence(6, 5));
        Method findNode = sv.getClass().getDeclaredMethod("findNode", int.class);
        findNode.setAccessible(true);
        Throwable invokeEx = expectThrows(InvocationTargetException.class, () -> findNode.invoke(sv, 100));
        assertTrue(invokeEx.getCause() instanceof UnreachablePointException);
    }
}
