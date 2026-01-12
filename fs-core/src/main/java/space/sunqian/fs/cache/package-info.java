/**
 * Provides simple cache interfaces, with soft/weak reference implementation by default. Example usage:
 * <pre>{@code
 * SimpleCache soft = SimpleCache.ofSoft();
 * SimpleCache weak = SimpleCache.ofWeak();
 * }</pre>
 * And cache function:
 * <pre>{@code
 * CacheFunction.ofMap(new HashMap<>());
 * }</pre>
 * <p>
 * Cache interfaces:
 * <ul>
 *     <li>{@link space.sunqian.fs.cache.SimpleCache}</li>
 *     <li>{@link space.sunqian.fs.cache.CacheFunction}</li>
 * </ul>
 */
package space.sunqian.fs.cache;