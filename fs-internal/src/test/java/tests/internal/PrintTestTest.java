package tests.internal;

import internal.utils.PrintTest;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrintTestTest implements PrintTest {

    @Test
    public void testPrinter() throws Exception {
        // Test 1: Default state (printer is null, using System.out)
        ByteArrayOutputStream systemOutStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(systemOutStream));
        try {
            print("hello");
            println(" world");
            System.out.flush();
            // Verify output is correctly printed through System.out
            assertEquals(
                systemOutStream.toString("UTF-8"),
                "hello world" + System.lineSeparator()
            );
        } finally {
            System.setOut(originalOut);
        }

        // Test 2: Explicitly set printer to null (using System.out)
        setPrinter(null);
        systemOutStream.reset();
        System.setOut(new PrintStream(systemOutStream));
        try {
            print("hello");
            println(" world");
            System.out.flush();
            // Verify output is correctly printed through System.out
            assertEquals(
                systemOutStream.toString("UTF-8"),
                "hello world" + System.lineSeparator()
            );
        } finally {
            System.setOut(originalOut);
        }

        // Test 3: Set custom printer (using custom PrintStream)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printer = new PrintStream(outputStream);
        setPrinter(printer);

        // Test print method
        print("hello");
        // Test println method
        println(" world");
        printer.flush();
        assertEquals(
            outputStream.toString("UTF-8"),
            "hello world" + System.lineSeparator()
        );

        // Test printFor method
        outputStream.reset();
        printFor("title", "hello world");
        printer.flush();
        assertEquals(
            outputStream.toString("UTF-8"),
            "title: hello world" + System.lineSeparator()
        );

        // Test 4: Set printer to null again (using System.out)
        setPrinter(null);
        systemOutStream.reset();
        System.setOut(new PrintStream(systemOutStream));
        try {
            print("hello");
            println(" world");
            System.out.flush();
            // Verify output is correctly printed through System.out
            assertEquals(
                systemOutStream.toString("UTF-8"),
                "hello world" + System.lineSeparator()
            );
        } finally {
            System.setOut(originalOut);
        }
    }
}
