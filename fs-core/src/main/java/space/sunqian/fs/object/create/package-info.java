/**
 * This package provides definitions and utilities for creating objects. Using codes similar to the following to create
 * objects in steps:
 * <pre>{@code
 * // Create object without builder and configured by setter methods:
 * ObjectCreator creator = ObjectCreator.forType(type);
 * Foo object = (Foo) creator.createBuilder();
 * foo.setBar("bar");
 * Bar bar = (Bar) creator.buildTarget(foo);
 *
 * // Create object with builder:
 * ObjectCreator creator = CreatorProvider.newProvider(your-builder-handlers).forType(type);
 * FooBuilder builder = (FooBuilder) creator.createBuilder();
 * builder.bar("bar");
 * Bar bar = (Bar) creator.buildTarget(builder);
 * }</pre>
 * The core interfaces of this package:
 * <ul>
 *     <li>{@link space.sunqian.fs.object.create.ObjectCreator}</li>
 *     <li>{@link space.sunqian.fs.object.create.CreatorProvider}</li>
 * </ul>
 */
package space.sunqian.fs.object.create;