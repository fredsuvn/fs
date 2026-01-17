/**
 * Package for data object schema. A data object schema may be an instance of
 * {@link space.sunqian.fs.object.schema.MapSchema} which is parsed from a map, or an instance of
 * {@link space.sunqian.fs.object.schema.ObjectSchema} which is parsed from a non-map object. Using codes similar to the
 * following to parse:
 * <pre>{@code
 * // Parse a non-map object:
 * ObjectSchema objectSchema = ObjectSchema.parse(type);
 * // Get a property value:
 * Object value = objectSchema.getProperty("someProperty").getValue(instant);
 *
 * // Parse a map:
 * MapSchema mapSchema = MapSchema.parse(type);
 * // Get the key type of the map:
 * Type keyType = mapSchema.keyType();
 * }</pre>
 * <p>
 * The core interfaces of this package:
 * <ul>
 *     <li>{@link space.sunqian.fs.object.schema.DataSchema}</li>
 *     <li>{@link space.sunqian.fs.object.schema.ObjectSchema}</li>
 *     <li>{@link space.sunqian.fs.object.schema.ObjectProperty}</li>
 *     <li>{@link space.sunqian.fs.object.schema.ObjectSchemaParser}</li>
 *     <li>{@link space.sunqian.fs.object.schema.handlers.AbstractObjectSchemaHandler}</li>
 *     <li>{@link space.sunqian.fs.object.schema.handlers.SimpleBeanSchemaHandler}</li>
 *     <li>{@link space.sunqian.fs.object.schema.MapSchema}</li>
 *     <li>{@link space.sunqian.fs.object.schema.MapSchemaParser}</li>
 * </ul>
 * And utilities:
 * <ul>
 *     <li>{@link space.sunqian.fs.object.schema.DataSchemaKit}</li>
 * </ul>
 */
package space.sunqian.fs.object.schema;