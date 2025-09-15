/**
 * Package for object conversion and data mapping.
 * <p>
 * Supports copying object properties, including instances of {@link java.util.Map} and non-map objects, using code
 * similar to the following:
 * <pre>{@code
 * DataMapper dataMapper = DataMapper.defaultMapper();
 * dataMapper.copyProperties(src, dst, options);
 * }</pre>
 * The core interface for data mapping:
 * <ul>
 *     <li>{@link xyz.sunqian.common.object.convert.DataMapper}</li>
 * </ul>
 * <p>
 * Supports object type conversion, using code similar to the following:
 * <pre>{@code
 * ObjectConverter converter = ObjectConverter.defaultConverter();
 * converter.convert(src, target, options);
 * }</pre>
 * The core interface for object type conversion:
 * <ul>
 *     <li>{@link xyz.sunqian.common.object.convert.ObjectConverter}</li>
 * </ul>
 * <p>
 * Other interfaces and classes:
 * <ul>
 *     <li>{@link xyz.sunqian.common.object.convert.DataBuilderFactory}</li>
 *     <li>{@link xyz.sunqian.common.object.convert.ConvertOption}</li>
 * </ul>
 */
package xyz.sunqian.common.object.convert;