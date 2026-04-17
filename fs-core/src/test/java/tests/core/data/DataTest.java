package tests.core.data;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.data.DataException;
import space.sunqian.fs.data.DataFormattingException;
import space.sunqian.fs.data.DataParsingException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataTest {

    @Test
    public void testDataExceptions() {
        testDataException();
        testDataFormattingException();
        testDataParsingException();
    }

    private void testDataException() {
        assertThrows(DataException.class, () -> {throw new DataException();});
        assertThrows(DataException.class, () -> {throw new DataException("");});
        assertThrows(DataException.class, () -> {throw new DataException("", new RuntimeException());});
        assertThrows(DataException.class, () -> {throw new DataException(new RuntimeException());});
    }

    private void testDataFormattingException() {
        assertThrows(DataFormattingException.class, () -> {throw new DataFormattingException();});
        assertThrows(DataFormattingException.class, () -> {throw new DataFormattingException("");});
        assertThrows(DataFormattingException.class, () -> {
            throw new DataFormattingException("", new RuntimeException());
        });
        assertThrows(DataFormattingException.class, () -> {throw new DataFormattingException(new RuntimeException());});
    }

    private void testDataParsingException() {
        assertThrows(DataParsingException.class, () -> {throw new DataParsingException();});
        assertThrows(DataParsingException.class, () -> {throw new DataParsingException("");});
        assertThrows(DataParsingException.class, () -> {throw new DataParsingException("", new RuntimeException());});
        assertThrows(DataParsingException.class, () -> {throw new DataParsingException(new RuntimeException());});
    }
}
