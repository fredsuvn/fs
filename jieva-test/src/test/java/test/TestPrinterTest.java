package test;

import org.testng.annotations.Test;
import xyz.sunqian.test.TestPrinter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.testng.Assert.assertEquals;

public class TestPrinterTest implements TestPrinter {

    @Test
    public void testPrinter() throws Exception {
        print("hello");
        println(" world");
        setPrinter(null);
        print("hello");
        println(" world");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printer = new PrintStream(outputStream);
        setPrinter(printer);
        print("hello");
        println(" world");
        printer.flush();
        assertEquals(
            outputStream.toString("UTF-8"),
            "hello world" + System.lineSeparator()
        );
        outputStream.reset();
        printFor("title", "hello world");
        printer.flush();
        assertEquals(
            outputStream.toString("UTF-8"),
            "title: hello world" + System.lineSeparator()
        );
        setPrinter(null);
    }
}
