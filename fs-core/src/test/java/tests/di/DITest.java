package tests.di;

import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.di.DIException;
import space.sunqian.fs.di.DIKit;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DITest implements PrintTest {

    @Test
    public void testDependent() throws Exception {
        {
            // 1 -> 2 -> 3 -> 4 -> 1
            Dep dep1 = new Dep();
            Dep dep2 = new Dep();
            Dep dep3 = new Dep();
            Dep dep4 = new Dep();
            dep1.dependencies.add(dep2);
            dep2.dependencies.add(dep3);
            dep3.dependencies.add(dep4);
            dep4.dependencies.add(dep1);
            assertThrows(DIException.class, () -> DIKit.checkCycleDependencies(dep1, Dep::dependencies));
            try {
                DIKit.checkCycleDependencies(dep1, Dep::dependencies);
            } catch (DIException e) {
                assertEquals("Cycle dependency: "
                        + dep1 + " -> "
                        + dep2 + " -> "
                        + dep3 + " -> "
                        + dep4 + " -> "
                        + dep1 + ".",
                    e.getMessage());
            }
        }
        {
            // 1 -> 2 -> 2, 3, 4 -> 1
            Dep dep1 = new Dep();
            Dep dep2 = new Dep();
            Dep dep3 = new Dep();
            Dep dep4 = new Dep();
            dep1.dependencies.add(dep2);
            dep2.dependencies.add(dep2);
            dep2.dependencies.add(dep3);
            dep2.dependencies.add(dep4);
            dep2.dependencies.add(dep1);
            assertThrows(DIException.class, () -> DIKit.checkCycleDependencies(dep1, Dep::dependencies));
            try {
                DIKit.checkCycleDependencies(dep1, Dep::dependencies);
            } catch (DIException e) {
                assertEquals("Cycle dependency: "
                        + dep1 + " -> "
                        + dep2 + " -> "
                        + dep1 + ".",
                    e.getMessage());
            }
        }
        {
            // 1 -> 2 -> 3 -> 4
            Dep dep1 = new Dep();
            Dep dep2 = new Dep();
            Dep dep3 = new Dep();
            Dep dep4 = new Dep();
            dep1.dependencies.add(dep2);
            dep2.dependencies.add(dep3);
            dep2.dependencies.add(dep4);
            DIKit.checkCycleDependencies(dep1, Dep::dependencies);
            DIKit.checkCycleDependencies(dep4, Dep::dependencies);
        }
    }

    private static class Dep {

        List<Dep> dependencies = new ArrayList<>();

        public @Nonnull List<Dep> dependencies() {
            return dependencies;
        }
    }
}
