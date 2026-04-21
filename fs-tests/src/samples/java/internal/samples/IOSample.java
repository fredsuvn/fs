package internal.samples;

import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.io.file.FileKit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Sample: I/O Operations Usage
 * <p>
 * Purpose: Demonstrate how to use the I/O utilities provided by fs-core module.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     File operations (read, write, copy, delete)
 *   </li>
 *   <li>
 *     Byte and char processing
 *   </li>
 *   <li>
 *     Stream operations
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link FileKit}: File operation utilities
 *   </li>
 *   <li>
 *     {@link IOKit}: I/O operation utilities
 *   </li>
 * </ul>
 */
public class IOSample {

    public static void main(String[] args) {
        try {
            demonstrateFileOperations();
            demonstrateStreamOperations();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Demonstrates file operations.
     */
    public static void demonstrateFileOperations() throws IOException {
        System.out.println("=== File Operations ===");

        // Create a temporary file
        Path tempPath = FileKit.createTempFile("test", ".txt");
        File tempFile = tempPath.toFile();
        System.out.println("Created temporary file: " + tempFile.getAbsolutePath());

        try {
            // Write to file
            String content = "Hello, I/O operations!";
            Files.writeString(tempPath, content);
            System.out.println("Written content: " + content);

            // Read from file
            String readContent = Files.readString(tempPath);
            System.out.println("Read content: " + readContent);

            // Check if file exists
            System.out.println("File exists: " + Files.exists(tempPath));

            // Get file size
            System.out.println("File size: " + Files.size(tempPath) + " bytes");

        } finally {
            // Delete temporary file
            try {
                Files.deleteIfExists(tempPath);
                System.out.println("Deleted temporary file");
            } catch (IOException e) {
                System.err.println("Failed to delete temporary file: " + e.getMessage());
            }
        }
    }

    /**
     * Demonstrates stream operations.
     */
    public static void demonstrateStreamOperations() throws IOException {
        System.out.println("\n=== Stream Operations ===");

        // Create input stream
        String input = "Hello, Stream Operations!";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());

        // Create output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            // Copy from input to output
            IOKit.readTo(inputStream, outputStream);

            // Get result
            String result = outputStream.toString();
            System.out.println("Stream copy result: " + result);

        } finally {
            // Close streams
            inputStream.close();
            outputStream.close();
        }
    }
}