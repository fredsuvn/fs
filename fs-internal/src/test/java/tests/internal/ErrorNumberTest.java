package tests.internal;

import internal.test.ErrorNumber;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ErrorNumberTest {

    @Test
    public void testErrorNumber() throws Exception {
        ErrorNumber errorNumber = new ErrorNumber();
        assertThrows(UnsupportedOperationException.class, errorNumber::intValue);
        assertThrows(UnsupportedOperationException.class, errorNumber::longValue);
        assertThrows(UnsupportedOperationException.class, errorNumber::floatValue);
        assertThrows(UnsupportedOperationException.class, errorNumber::doubleValue);
    }
}
