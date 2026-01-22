/**
 * This package provides core interfaces and utilities for object conversion and copying. Using codes similar to the
 * following to convert object and copy object properties:
 * <pre>{@code
 * // convert object
 * ObjectConverter converter = ObjectConverter.defaultConverter();
 * converter.convert(src, target, options);
 * // copy object properties
 * PropertyCopier copier = PropertyCopier.defaultCopier();
 * copier.copyProperties(src, dst, options);
 * }</pre>
 * The core interfaces of this package:
 * <ul>
 *     <li>{@link space.sunqian.fs.object.convert.ObjectConverter}</li>
 *     <li>{@link space.sunqian.fs.object.convert.handlers.AssignableConvertHandler}</li>
 *     <li>{@link space.sunqian.fs.object.convert.handlers.CommonConvertHandler}</li>
 *     <li>{@link space.sunqian.fs.object.convert.PropertyCopier}</li>
 *     <li>{@link space.sunqian.fs.object.convert.PropertyCopier.PropertyMapper}</li>
 * </ul>
 */
package space.sunqian.fs.object.convert;