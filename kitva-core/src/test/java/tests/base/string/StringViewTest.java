package tests.base.string;

import org.junit.jupiter.api.Test;
import space.sunqian.common.base.exception.UnreachablePointException;
import space.sunqian.common.base.string.StringView;
import space.sunqian.common.collect.ListKit;
import internal.test.PrintTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StringViewTest implements PrintTest {

    @Test
    public void testStringView() throws Exception {
        String fullString = "0123456789";
        List<String> strings = ListKit.list("012", "345", "678", "9");
        testStringView(fullString, strings);
        testStringView(fullString, ListKit.list("01", "23", "45", "67", "89"));
        testStringView(fullString, ListKit.list("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
        testStringView(fullString, ListKit.list("0", "1", "2", "3", "", "4", "", "5", "", "6", "7", "8", "9"));
        testStringView(fullString, ListKit.list("0", "1", "2", "3", "", "4", "", "5678", "", "", "9", "", ""));
        StringView sv = StringView.of(strings);
        assertThrows(IndexOutOfBoundsException.class, () -> sv.charAt(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> sv.subSequence(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> sv.subSequence(0, 11));
        assertThrows(IndexOutOfBoundsException.class, () -> sv.subSequence(6, 5));
        Method findNode = sv.getClass().getDeclaredMethod("findNode", int.class);
        findNode.setAccessible(true);
        Throwable invokeEx = assertThrows(InvocationTargetException.class, () -> findNode.invoke(sv, 100));
        assertTrue(invokeEx.getCause() instanceof UnreachablePointException);
    }

    private void testStringView(String fullString, List<String> strings) {
        StringView sv = StringView.of(strings);
        assertEquals(sv.length(), fullString.length());
        assertEquals(sv.toString(), fullString);
        for (int i = 0; i < 10; i++) {
            assertEquals(sv.charAt(i), i + '0');
        }
        for (int start = 0; start < fullString.length(); start++) {
            for (int end = start; end < fullString.length(); end++) {
                assertEquals(sv.subSequence(start, end).toString(), fullString.subSequence(start, end).toString());
            }
        }
    }
}
