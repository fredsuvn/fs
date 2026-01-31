package tests.data.properties;

import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.base.system.ResKit;
import space.sunqian.fs.data.properties.PropertiesData;
import space.sunqian.fs.data.properties.PropertiesException;
import space.sunqian.fs.io.IOKit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.nio.channels.Channels;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PropertiesTest implements PrintTest {

    @Test
    public void testPropertiesData() throws Exception {
        {
            // common
            PropertiesData properties = PropertiesData.load(ResKit.findStream("data/x.properties"));
            printFor("x.properties", properties.asMap());
            checkProperties(properties);

            // update
            properties.set("x1", "1000");
            assertEquals(1000, properties.getInt("x1"));
            properties.set("x100", "1000");
            assertEquals(1000, properties.getInt("x100"));
            properties.remove("x2");
            assertNull(properties.getString("x2"));
            printFor("x.properties - modified", properties.asMap());

            // write
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            properties.writeTo(output);
            checkOutput(output);
            output.reset();
            properties.writeTo(Channels.newChannel(output));
            checkOutput(output);
            output.reset();
        }
        {
            // with charset
            PropertiesData properties = PropertiesData.load(
                ResKit.findStream("data/x.properties"), CharsKit.defaultCharset()
            );
            checkProperties(properties);
        }
        {
            // wrapper
            PropertiesData properties = PropertiesData.load(
                ResKit.findStream("data/x.properties"), CharsKit.defaultCharset()
            );
            checkProperties(PropertiesData.wrap(properties.asProperties()));
        }
    }

    private void checkProperties(PropertiesData properties) {
        assertEquals(1, properties.getInt("x1"));
        assertEquals(100, properties.getInt("x100", 100));
        assertEquals(2L, properties.getLong("x2"));
        assertEquals(200L, properties.getLong("x200", 200L));
        assertEquals(3.3f, properties.getFloat("x3"));
        assertEquals(300.0f, properties.getFloat("x300", 300.0f));
        assertEquals(4.4, properties.getDouble("x4"));
        assertEquals(400.0, properties.getDouble("x400", 400.0));
        assertEquals(true, properties.getBoolean("x5"));
        assertEquals(false, properties.getBoolean("x6"));
        assertEquals("hello", properties.getString("x7"));
        assertEquals("world", properties.getString("x700", "world"));
        assertEquals("hello", properties.getString("x7", "world"));
        assertEquals(false, properties.getBoolean("x7"));
        assertEquals(true, properties.getBoolean("x8"));
        assertEquals(true, properties.getBoolean("x9"));
        assertEquals(true, properties.getBoolean("x10"));
        assertEquals(false, properties.getBoolean("x11"));
        assertEquals(false, properties.getBoolean("x1100"));
        assertEquals(true, properties.getBoolean("x1"));
        assertEquals(null, properties.getString("x100"));
        assertEquals("中文", properties.getString("x12"));
    }

    private void checkOutput(ByteArrayOutputStream output) throws Exception {
        Properties properties = new Properties();
        Reader reader = IOKit.newReader(new ByteArrayInputStream(output.toByteArray()), CharsKit.defaultCharset());
        properties.load(reader);
        assertEquals("1000", properties.getProperty("x1"));
        assertNull(properties.getProperty("x2"));
        assertEquals("3.3", properties.getProperty("x3"));
        assertEquals("4.4", properties.getProperty("x4"));
        assertEquals("true", properties.getProperty("x5"));
        assertEquals("false", properties.getProperty("x6"));
        assertEquals("hello", properties.getProperty("x7"));
        assertEquals("yes", properties.getProperty("x8"));
        assertEquals("Y", properties.getProperty("x9"));
        assertEquals("Enabled", properties.getProperty("x10"));
        assertEquals("Disabled", properties.getProperty("x11"));
        assertEquals("1000", properties.getProperty("x100"));
        assertEquals("中文", properties.getProperty("x12"));
        assertEquals(12, properties.size());
        printFor("x.properties - output", properties);
    }

    @Test
    public void testException() throws Exception {
        {
            // PropertiesException
            assertThrows(PropertiesException.class, () -> {
                throw new PropertiesException();
            });
            assertThrows(PropertiesException.class, () -> {
                throw new PropertiesException("");
            });
            assertThrows(PropertiesException.class, () -> {
                throw new PropertiesException("", new RuntimeException());
            });
            assertThrows(PropertiesException.class, () -> {
                throw new PropertiesException(new RuntimeException());
            });
        }
    }
}
