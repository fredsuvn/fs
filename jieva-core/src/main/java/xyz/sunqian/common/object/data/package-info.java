/**
 * Provides interfaces and utilities to rapidly parse a data class to its schema, using code similar to the following:
 * <pre>{@code
 * DataSchema schema = DataSchema.parse(type);
 * String str = schema.getProperty("someString").getValue(instant);
 * }</pre>
 * <p>
 * Core interfaces:
 * <ul>
 *     <li>{@link xyz.sunqian.common.object.data.ObjectSchema}</li>
 *     <li>{@link xyz.sunqian.common.object.data.ObjectProperty}</li>
 *     <li>{@link xyz.sunqian.common.object.data.ObjectSchemaParser}</li>
 *     <li>{@link xyz.sunqian.common.object.data.handlers.AbstractObjectSchemaHandler}</li>
 *     <li>{@link xyz.sunqian.common.object.data.handlers.SimpleBeanSchemaHandler}</li>
 * </ul>
 * Utilities:
 * <ul>
 *     <li>{@link xyz.sunqian.common.object.data.DataObjectKit}</li>
 * </ul>
 */
package xyz.sunqian.common.object.data;