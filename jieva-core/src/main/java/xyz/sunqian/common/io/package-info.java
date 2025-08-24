/**
 * Provides utilities and interfaces for IO related, including subpackages.
 * <p>
 * Utilities:
 * <ul>
 *     <li>{@link xyz.sunqian.common.io.IOKit}</li>
 *     <li>{@link xyz.sunqian.common.io.BufferKit}</li>
 * </ul>
 * The {@code IOKit} is based on the core I/O operator interface: {@link xyz.sunqian.common.io.IOOperator}.
 * In addition, this package provides convenient data reading interfaces to simplify the complexity of reading.
 * Here are data reading related interfaces:
 * <ul>
 *     <li>{@link xyz.sunqian.common.io.ByteReader}</li>
 *     <li>{@link xyz.sunqian.common.io.CharReader}</li>
 *     <li>{@link xyz.sunqian.common.io.ByteSegment}</li>
 *     <li>{@link xyz.sunqian.common.io.CharSegment}</li>
 * </ul>
 * This package also provides data transformation interfaces that can be used for complex data transformations:
 * <ul>
 *     <li>{@link xyz.sunqian.common.io.ByteProcessor}</li>
 *     <li>{@link xyz.sunqian.common.io.ByteTransformer}</li>
 *     <li>{@link xyz.sunqian.common.io.CharProcessor}</li>
 *     <li>{@link xyz.sunqian.common.io.CharTransformer}</li>
 * </ul>
 * These abstract classes are used to implement IO streams with minimal effort.
 * <ul>
 *     <li>{@link xyz.sunqian.common.io.DoReadStream}</li>
 *     <li>{@link xyz.sunqian.common.io.DoReadReader}</li>
 *     <li>{@link xyz.sunqian.common.io.DoWriteStream}</li>
 *     <li>{@link xyz.sunqian.common.io.DoWriteWriter}</li>
 * </ul>
 * Subpackages:
 * <ul>
 *     <li>{@link xyz.sunqian.common.io.file}</li>
 *     <li>{@link xyz.sunqian.common.io.communicate}</li>
 * </ul>
 * <p>
 */
package xyz.sunqian.common.io;