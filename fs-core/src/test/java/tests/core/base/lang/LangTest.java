package tests.core.base.lang;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.lang.CloneException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LangTest {

    @Test
    public void testCloneException() {
        // Test CloneException constructors
        assertThrows(CloneException.class, () -> {throw new CloneException();});
        assertThrows(CloneException.class, () -> {throw new CloneException("", new RuntimeException());});
        assertThrows(CloneException.class, () -> {throw new CloneException("", new RuntimeException());});
        assertThrows(CloneException.class, () -> {throw new CloneException(new RuntimeException());});
    }
}
