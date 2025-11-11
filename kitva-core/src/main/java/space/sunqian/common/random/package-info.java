/**
 * Provides interfaces for random, including random number-{@code Rng} and random object-{@code Rog}. Using codes
 * similar to the following can easily generate random objects:
 * <pre>{@code
 * Rog<String> rog = Rog.newBuilder()
 *     .weight(10, "a")
 *     .weight(10, "b")
 *     .build();
 * String randomString = rog.next();
 * }</pre>
 * <p>
 * Interfaces:
 * <ul>
 *     <li>{@link space.sunqian.common.random.Rng}</li>
 *     <li>{@link space.sunqian.common.random.Rog}</li>
 * </ul>
 */
package space.sunqian.common.random;