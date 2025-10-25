package tests;

import org.testng.annotations.Test;
import internal.test.PrintTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.testng.Assert.assertEquals;

public class PrintTestTest implements PrintTest {

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
