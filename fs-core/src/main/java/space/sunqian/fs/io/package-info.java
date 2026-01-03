/**
 * Provides utilities and interfaces for IO related, including subpackages.
 * <p>
 * Utilities:
 * <ul>
 *     <li>{@link space.sunqian.fs.io.IOKit}</li>
 *     <li>{@link space.sunqian.fs.io.BufferKit}</li>
 * </ul>
 * The {@code IOKit} is based on the core I/O operator interface: {@link space.sunqian.fs.io.IOOperator}.
 * In addition, this package provides convenient data reading interfaces to simplify the complexity of reading.
 * Here are data reading related interfaces:
 * <ul>
 *     <li>{@link space.sunqian.fs.io.ByteReader}</li>
 *     <li>{@link space.sunqian.fs.io.CharReader}</li>
 *     <li>{@link space.sunqian.fs.io.ByteSegment}</li>
 *     <li>{@link space.sunqian.fs.io.CharSegment}</li>
 * </ul>
 * This package also provides data transformation interfaces that can be used for complex data transformations:
 * <ul>
 *     <li>{@link space.sunqian.fs.io.ByteProcessor}</li>
 *     <li>{@link space.sunqian.fs.io.ByteTransformer}</li>
 *     <li>{@link space.sunqian.fs.io.CharProcessor}</li>
 *     <li>{@link space.sunqian.fs.io.CharTransformer}</li>
 * </ul>
 * These abstract classes are used to implement IO streams with minimal effort.
 * <ul>
 *     <li>{@link space.sunqian.fs.io.DoReadStream}</li>
 *     <li>{@link space.sunqian.fs.io.DoReadReader}</li>
 *     <li>{@link space.sunqian.fs.io.DoWriteStream}</li>
 *     <li>{@link space.sunqian.fs.io.DoWriteWriter}</li>
 * </ul>
 * Subpackages:
 * <ul>
 *     <li>{@link space.sunqian.fs.io.file}</li>
 *     <li>{@link space.sunqian.fs.io.communicate}</li>
 * </ul>
 */
package space.sunqian.fs.io;