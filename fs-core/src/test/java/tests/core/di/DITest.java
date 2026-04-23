package tests.core.di;

import internal.utils.TestPrint;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.di.DIException;
import space.sunqian.fs.di.DIKit;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DITest implements TestPrint {

    @Test
    public void testCycleDependency1() {
        // 1 -> 2 -> 3 -> 4 -> 1
        Dep dep1 = createDep();
        Dep dep2 = createDep();
        Dep dep3 = createDep();
        Dep dep4 = createDep();
        dep1.dependencies.add(dep2);
        dep2.dependencies.add(dep3);
        dep3.dependencies.add(dep4);
        dep4.dependencies.add(dep1);
        assertThrows(DIException.class, () -> DIKit.checkCycleDependencies(dep1, Dep::getDependencies));
        try {
            DIKit.checkCycleDependencies(dep1, Dep::getDependencies);
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

    @Test
    public void testCycleDependency2() {
        // 1 -> 2 -> 2, 3, 4 -> 1
        Dep dep1 = createDep();
        Dep dep2 = createDep();
        Dep dep3 = createDep();
        Dep dep4 = createDep();
        dep1.dependencies.add(dep2);
        dep2.dependencies.add(dep2);
        dep2.dependencies.add(dep3);
        dep2.dependencies.add(dep4);
        dep2.dependencies.add(dep1);
        assertThrows(DIException.class, () -> DIKit.checkCycleDependencies(dep1, Dep::getDependencies));
        try {
            DIKit.checkCycleDependencies(dep1, Dep::getDependencies);
        } catch (DIException e) {
            assertEquals("Cycle dependency: "
                    + dep1 + " -> "
                    + dep2 + " -> "
                    + dep1 + ".",
                e.getMessage());
        }
    }

    @Test
    public void testNoCycleDependency() {
        // 1 -> 2 -> 3 -> 4
        Dep dep1 = createDep();
        Dep dep2 = createDep();
        Dep dep3 = createDep();
        Dep dep4 = createDep();
        dep1.dependencies.add(dep2);
        dep2.dependencies.add(dep3);
        dep2.dependencies.add(dep4);
        DIKit.checkCycleDependencies(dep1, Dep::getDependencies);
        DIKit.checkCycleDependencies(dep4, Dep::getDependencies);
    }

    private Dep createDep() {
        return new Dep();
    }

    private static class Dep {

        List<Dep> dependencies = new ArrayList<>();

        public List<Dep> getDependencies() {
            return dependencies;
        }
    }
}
