/**
 * This package provides definitions and utilities for schemas, including map schema and non-map object schema. The top
 * interface for a data schema is {@link space.sunqian.fs.object.schema.DataSchema}, and the two main sub-interfaces are
 * {@link space.sunqian.fs.object.schema.MapSchema} which represents the schema for map, and
 * {@link space.sunqian.fs.object.schema.ObjectSchema} which represents the schema for non-map object. Using codes
 * similar to the following to parse:
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
 * The core interfaces of this package:
 * <ul>
 *     <li>{@link space.sunqian.fs.object.schema.DataSchema}</li>
 *     <li>{@link space.sunqian.fs.object.schema.ObjectSchema}</li>
 *     <li>{@link space.sunqian.fs.object.schema.ObjectProperty}</li>
 *     <li>{@link space.sunqian.fs.object.schema.ObjectSchemaParser}</li>
 *     <li>{@link space.sunqian.fs.object.schema.MapSchema}</li>
 *     <li>{@link space.sunqian.fs.object.schema.MapSchemaParser}</li>
 *     <li>{@link space.sunqian.fs.object.schema.handlers.AbstractObjectSchemaHandler}</li>
 *     <li>{@link space.sunqian.fs.object.schema.handlers.CommonSchemaHandler}</li>
 *     <li>{@link space.sunqian.fs.object.schema.handlers.RecordSchemaHandler}(only loaded on JDK 16+)</li>
 * </ul>
 * And utilities:
 * <ul>
 *     <li>{@link space.sunqian.fs.object.schema.SchemaKit}</li>
 * </ul>
 */
package space.sunqian.fs.object.schema;