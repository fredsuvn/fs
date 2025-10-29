/**
 * Provides utilities and interfaces for IO related, including subpackages.
 * <p>
 * Utilities:
 * <ul>
 *     <li>{@link space.sunqian.common.io.IOKit}</li>
 *     <li>{@link space.sunqian.common.io.BufferKit}</li>
 * </ul>
 * The {@code IOKit} is based on the core I/O operator interface: {@link space.sunqian.common.io.IOOperator}.
 * In addition, this package provides convenient data reading interfaces to simplify the complexity of reading.
 * Here are data reading related interfaces:
 * <ul>
 *     <li>{@link space.sunqian.common.io.ByteReader}</li>
 *     <li>{@link space.sunqian.common.io.CharReader}</li>
 *     <li>{@link space.sunqian.common.io.ByteSegment}</li>
 *     <li>{@link space.sunqian.common.io.CharSegment}</li>
 * </ul>
 * This package also provides data transformation interfaces that can be used for complex data transformations:
 * <ul>
 *     <li>{@link space.sunqian.common.io.ByteProcessor}</li>
 *     <li>{@link space.sunqian.common.io.ByteTransformer}</li>
 *     <li>{@link space.sunqian.common.io.CharProcessor}</li>
 *     <li>{@link space.sunqian.common.io.CharTransformer}</li>
 * </ul>
 * These abstract classes are used to implement IO streams with minimal effort.
 * <ul>
 *     <li>{@link space.sunqian.common.io.DoReadStream}</li>
 *     <li>{@link space.sunqian.common.io.DoReadReader}</li>
 *     <li>{@link space.sunqian.common.io.DoWriteStream}</li>
 *     <li>{@link space.sunqian.common.io.DoWriteWriter}</li>
 * </ul>
 * Subpackages:
 * <ul>
 *     <li>{@link space.sunqian.common.io.file}</li>
 *     <li>{@link space.sunqian.common.io.communicate}</li>
 * </ul>
 */
package space.sunqian.common.io;