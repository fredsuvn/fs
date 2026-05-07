package tests.core.object.meta;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.object.meta.DataMetaException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MetaTest {

    @Test
    public void testDataMetaExceptionConstructors() {
        assertThrows(DataMetaException.class, () -> {throw new DataMetaException();});
        assertThrows(DataMetaException.class, () -> {throw new DataMetaException("");});
        assertThrows(DataMetaException.class, () -> {throw new DataMetaException("", new RuntimeException());});
        assertThrows(DataMetaException.class, () -> {throw new DataMetaException(new RuntimeException());});
        assertThrows(DataMetaException.class, () -> {throw new DataMetaException(Object.class);});
        assertThrows(DataMetaException.class, () -> {
            throw new DataMetaException(Object.class, new RuntimeException());
        });
    }
}