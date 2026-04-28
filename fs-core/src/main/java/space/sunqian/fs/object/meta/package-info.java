/**
 * This package provides definitions and utilities for schemas, including map schema and non-map object schema. The top
 * interface for a data schema is {@link space.sunqian.fs.object.meta.DataMeta}, and the two main sub-interfaces are
 * {@link space.sunqian.fs.object.meta.MapMeta} which represents the schema for map, and
 * {@link space.sunqian.fs.object.meta.ObjectMeta} which represents the schema for non-map object. Using codes
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
 *     <li>{@link space.sunqian.fs.object.meta.DataMeta}</li>
 *     <li>{@link space.sunqian.fs.object.meta.ObjectMeta}</li>
 *     <li>{@link space.sunqian.fs.object.meta.PropertyMetaMeta}</li>
 *     <li>{@link space.sunqian.fs.object.meta.ObjectMetaManager}</li>
 *     <li>{@link space.sunqian.fs.object.meta.MapMeta}</li>
 *     <li>{@link space.sunqian.fs.object.meta.MapMetaManager}</li>
 *     <li>{@link space.sunqian.fs.object.meta.handlers.AbstractObjectMetaHandler}</li>
 *     <li>{@link space.sunqian.fs.object.meta.handlers.CommonMetaHandler}</li>
 *     <li>{@link space.sunqian.fs.object.meta.handlers.RecordMetaHandler}(only loaded on JDK 16+)</li>
 * </ul>
 * And utilities:
 * <ul>
 *     <li>{@link space.sunqian.fs.object.meta.MetaKit}</li>
 * </ul>
 */
package space.sunqian.fs.object.meta;