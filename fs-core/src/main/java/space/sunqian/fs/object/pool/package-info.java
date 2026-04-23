/**
 * This package provides simple way to create simple object pools:
 * <pre>{@code
 * SimplePool<X> pool = SimplePool.newBuilder()
 *     .coreSize(2)
 *     .maxSize(5)
 *     .idleTimeout(Duration.ofSeconds(60))
 *     .supplier(() -> new X())
 *     .validator(x -> x.isValid())
 *     .discarder(x -> x.close())
 *     .build();
 * }</pre>
 * The core interfaces of this package:
 * <ul>
 *     <li>{@link space.sunqian.fs.object.pool.SimplePool}</li>
 * </ul>
 */
package space.sunqian.fs.object.pool;