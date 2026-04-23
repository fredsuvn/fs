package internal.samples;

import space.sunqian.fs.utils.codec.Base64Kit;
import space.sunqian.fs.utils.codec.DigestKit;
import space.sunqian.fs.utils.codec.HexKit;

import java.security.MessageDigest;

/**
 * Sample: Codec Utilities Usage
 * <p>
 * Purpose: Demonstrate how to use the codec utilities provided by fs-core module.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     Base64 encoding and decoding
 *   </li>
 *   <li>
 *     Hex encoding and decoding
 *   </li>
 *   <li>
 *     Message digest operations
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link Base64Kit}: Base64 encoding and decoding utilities
 *   </li>
 *   <li>
 *     {@link HexKit}: Hex encoding and decoding utilities
 *   </li>
 *   <li>
 *     {@link DigestKit}: Message digest utilities
 *   </li>
 * </ul>
 */
public class CodecSample {

    public static void main(String[] args) {
        demonstrateBase64Operations();
        demonstrateHexOperations();
        demonstrateDigestOperations();
    }

    /**
     * Demonstrates Base64 encoding and decoding.
     */
    public static void demonstrateBase64Operations() {
        System.out.println("=== Base64 Operations ===");

        String original = "Hello, Base64!";
        System.out.println("Original: " + original);

        // Encode to Base64
        String encoded = Base64Kit.encoder().encodeToString(original.getBytes());
        System.out.println("Encoded: " + encoded);

        // Decode from Base64
        byte[] decodedBytes = Base64Kit.decoder().decode(encoded);
        String decoded = new String(decodedBytes);
        System.out.println("Decoded: " + decoded);
    }

    /**
     * Demonstrates Hex encoding and decoding.
     */
    public static void demonstrateHexOperations() {
        System.out.println("\n=== Hex Operations ===");

        String original = "Hello, Hex!";
        System.out.println("Original: " + original);

        // Encode to Hex
        String encoded = HexKit.encoder().encodeToString(original.getBytes());
        System.out.println("Encoded: " + encoded);

        // Decode from Hex
        byte[] decodedBytes = HexKit.decoder().decode(encoded);
        String decoded = new String(decodedBytes);
        System.out.println("Decoded: " + decoded);
    }

    /**
     * Demonstrates message digest operations.
     */
    public static void demonstrateDigestOperations() {
        System.out.println("\n=== Digest Operations ===");

        String original = "Hello, Digest!";
        System.out.println("Original: " + original);

        try {
            // MD5 digest
            String md5 = calculateDigest(original, "MD5");
            System.out.println("MD5: " + md5);

            // SHA-1 digest
            String sha1 = calculateDigest(original, "SHA-1");
            System.out.println("SHA-1: " + sha1);

            // SHA-256 digest
            String sha256 = calculateDigest(original, "SHA-256");
            System.out.println("SHA-256: " + sha256);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculates the digest for the given input using the specified algorithm.
     */
    private static String calculateDigest(String input, String algorithm) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] hash = digest.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}