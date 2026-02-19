/**
 * This package provides definitions and utilities for creating and building objects. Using codes similar to the
 * following to create and build objects in steps:
 * <pre>{@code
 * // Create object without builder and configured by setter methods:
 * BuilderExecutor executor = BuilderExecutor.forType(type);
 * Foo object = (Foo) executor.createBuilder();
 * foo.setBar("bar");
 * Bar bar = (Bar) executor.buildTarget(foo);
 *
 * // Create object with builder:
 * BuilderExecutor executor = BuilderProvider.newProvider(your-builder-handlers).forType(type);
 * FooBuilder builder = (FooBuilder) executor.createBuilder();
 * builder.bar("bar");
 * Bar bar = (Bar) executor.buildTarget(builder);
 * }</pre>
 * The core interfaces of this package:
 * <ul>
 *     <li>{@link space.sunqian.fs.object.build.BuilderExecutor}</li>
 *     <li>{@link space.sunqian.fs.object.build.BuilderProvider}</li>
 * </ul>
 */
package space.sunqian.fs.object.build;