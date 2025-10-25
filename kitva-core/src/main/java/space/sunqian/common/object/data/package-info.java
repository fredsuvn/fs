/**
 * Package for data object. A data object may be an instance of {@link java.util.Map} parsed by
 * {@link space.sunqian.common.object.data.MapSchemaParser}, or be a non-map object, such as a simple java bean, parsed
 * by {@link space.sunqian.common.object.data.ObjectSchemaParser}, using code similar to the following:
 * <pre>{@code
 * // Parse a non-map object:
 * ObjectSchema objectSchema = ObjectSchema.parse(type);
 * String str = objectSchema.getProperty("someString").getValue(instant);
 *
 * // Parse a map:
 * MapSchema mapSchema = MapSchema.parse(type);
 * Type keyType = mapSchema.keyType();
 *
 * // Object builder
 * ObjectBuilder objectBuilder = ObjectBuilder.get(type);
 * Object builder = objectBuilder.newBuilder();
 * // copy properties for builder ...
 * Object result = objectBuilder.build(builder);
 * }</pre>
 * <p>
 * The core interfaces of this package:
 * <ul>
 *     <li>{@link space.sunqian.common.object.data.DataSchema}</li>
 *     <li>{@link space.sunqian.common.object.data.ObjectSchema}</li>
 *     <li>{@link space.sunqian.common.object.data.ObjectProperty}</li>
 *     <li>{@link space.sunqian.common.object.data.ObjectSchemaParser}</li>
 *     <li>{@link space.sunqian.common.object.data.handlers.AbstractObjectSchemaHandler}</li>
 *     <li>{@link space.sunqian.common.object.data.handlers.SimpleBeanSchemaHandler}</li>
 *     <li>{@link space.sunqian.common.object.data.MapSchema}</li>
 *     <li>{@link space.sunqian.common.object.data.MapSchemaParser}</li>
 *     <li>{@link space.sunqian.common.object.data.ObjectBuilder}</li>
 *     <li>{@link space.sunqian.common.object.data.ObjectBuilderProvider}</li>
 * </ul>
 * And utilities:
 * <ul>
 *     <li>{@link space.sunqian.common.object.data.DataObjectKit}</li>
 * </ul>
 */
package space.sunqian.common.object.data;