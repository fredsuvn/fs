/**
 * Package for data object. A data object may be an instance of {@link java.util.Map} parsed by
 * {@link xyz.sunqian.common.object.data.MapSchemaParser}, or be a non-map object, such as a simple java bean, parsed by
 * {@link xyz.sunqian.common.object.data.ObjectSchemaParser}, using code similar to the following:
 * <pre>{@code
 * // Parse a non-map object:
 * ObjectSchema objectSchema = ObjectSchema.parse(type);
 * String str = objectSchema.getProperty("someString").getValue(instant);
 * // Parse a map:
 * MapSchema mapSchema = MapSchema.parse(type);
 * Type keyType = mapSchema.keyType();
 * }</pre>
 * <p>
 * The core interfaces of this package:
 * <ul>
 *     <li>{@link xyz.sunqian.common.object.data.DataSchema}</li>
 *     <li>{@link xyz.sunqian.common.object.data.ObjectSchema}</li>
 *     <li>{@link xyz.sunqian.common.object.data.ObjectProperty}</li>
 *     <li>{@link xyz.sunqian.common.object.data.ObjectSchemaParser}</li>
 *     <li>{@link xyz.sunqian.common.object.data.handlers.AbstractObjectSchemaHandler}</li>
 *     <li>{@link xyz.sunqian.common.object.data.handlers.SimpleBeanSchemaHandler}</li>
 *     <li>{@link xyz.sunqian.common.object.data.MapSchema}</li>
 *     <li>{@link xyz.sunqian.common.object.data.MapSchemaParser}</li>
 * </ul>
 * And utilities:
 * <ul>
 *     <li>{@link xyz.sunqian.common.object.data.DataObjectKit}</li>
 * </ul>
 */
package xyz.sunqian.common.object.data;