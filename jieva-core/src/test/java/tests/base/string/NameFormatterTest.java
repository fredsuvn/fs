package tests.base.string;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.string.NameFormatter;
import xyz.sunqian.common.collect.ListKit;

import static org.testng.Assert.assertEquals;

public class NameFormatterTest {

    @Test
    public void testSplit() {
        {
            // file
            NameFormatter fileFormat = NameFormatter.fileNaming();
            assertEquals(fileFormat.parse("a.b.c"), ListKit.list("a.b", "c"));
            assertEquals(fileFormat.parse(""), ListKit.list(""));
            assertEquals(fileFormat.parse("."), ListKit.list("."));
            assertEquals(fileFormat.parse("a."), ListKit.list("a."));
            assertEquals(fileFormat.parse("a.b"), ListKit.list("a", "b"));
        }
    }
}
