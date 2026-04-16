package tests.internal;

import internal.utils.DataTest;
import internal.utils.ErrorList;
import internal.utils.ErrorMap;
import internal.utils.PrintTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ErrorCollectionTest implements DataTest, PrintTest {

    @Test
    public void testErrorCollection() throws Exception {
        // Test ErrorList
        assertThrows(UnsupportedOperationException.class, () -> new ErrorList<>().get(1));
        assertThrows(UnsupportedOperationException.class, () -> new ErrorList<>().size());

        // Test ErrorMap
        assertThrows(UnsupportedOperationException.class, () -> new ErrorMap<>().get("key"));
        assertThrows(UnsupportedOperationException.class, () -> new ErrorMap<>().size());
    }
}
