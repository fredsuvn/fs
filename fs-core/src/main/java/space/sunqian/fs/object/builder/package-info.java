/**
 * This package provides builder related operations, it definitions and utilities for creating and building objects.
 * Using codes similar to the following to create and build objects in steps:
 * <pre>{@code
 * // Create object without builder and configured by setter methods:
 * BuilderOperator operator = BuilderOperator.forType(type);
 * Foo object = (Foo) operator.createBuilder();
 * foo.setBar("bar");
 * Bar bar = (Bar) operator.buildTarget(foo);
 *
 * // Create object with builder:
 * BuilderOperator operator = BuilderOperatorProvider.newProvider(your-builder-handlers).forType(type);
 * FooBuilder fooBuilder = (FooBuilder) operator.createBuilder();
 * fooBuilder.setBar("bar");
 * Bar bar = (Bar) operator.buildTarget(fooBuilder);
 * }</pre>
 * The core interfaces of this package:
 * <ul>
 *     <li>{@link space.sunqian.fs.object.builder.BuilderOperator}</li>
 *     <li>{@link space.sunqian.fs.object.builder.BuilderOperatorProvider}</li>
 * </ul>
 */
package space.sunqian.fs.object.builder;