/**
 * A simple dependency injection framework to implement {@link space.sunqian.common.app.SimpleApp}.
 * <p>
 * Here is a simple example:
 * <pre>{@code
 * public class DIExample implements PrintTest {
 *
 *     public static void main(String[] args) {
 *         InjectedApp app = InjectedApp.newBuilder()
 *             .resourceTypes(
 *                 XController.class,
 *                 XServiceImpl.class,
 *                 XServiceProxy.class,
 *                 BeforeDestroyService.class
 *             )
 *             .resourceAnnotation(XResource.class)
 *             .postConstructAnnotation(XPostConstruct.class)
 *             .preDestroyAnnotation(XPreDestroy.class)
 *             .build();
 *         XService xService = app.getResource(XService.class);
 *         System.out.println(xService.doService());
 *         app.shutdown();
 *     }
 *
 *     public static class XController {
 *
 *         @XResource
 *         private XService xService;
 *         @XResource
 *         private ConstructService constructService;
 *         @XResource
 *         private DestroyService destroyService;
 *
 *         @XPostConstruct
 *         public void init() {
 *             constructService.doConstruct();
 *         }
 *
 *         @XPreDestroy
 *         @InjectedDependsOn(BeforeDestroyService.class)
 *         public void destroy() {
 *             destroyService.doDestroy();
 *         }
 *     }
 *
 *     public static class BeforeDestroyService {
 *
 *         @XPreDestroy
 *         public void destroy() {
 *             System.out.println("before destroy");
 *         }
 *     }
 *
 *     public interface XService {
 *
 *         String doService();
 *     }
 *
 *     public static class XServiceImpl implements XService {
 *
 *         @Override
 *         public String doService() {
 *             return "do service...";
 *         }
 *     }
 *
 *     public static class XServiceProxy implements InjectedAspect {
 *
 *         @Override
 *         public boolean needsAspect(@Nonnull Type type) {
 *             return type.equals(XServiceImpl.class);
 *         }
 *
 *         @Override
 *         public boolean needsAspect(@Nonnull Method method) {
 *             return method.getName().equals("doService");
 *         }
 *
 *         @Override
 *         public void beforeInvoking(@Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
 *         }
 *
 *         @Override
 *         public @Nullable Object afterReturning(@Nullable Object result, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
 *             return result + "[proxied]";
 *         }
 *
 *         @Override
 *         public @Nullable Object afterThrowing(@Nonnull Throwable ex, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) {
 *             return null;
 *         }
 *     }
 *
 *     public static class ConstructService {
 *
 *         public void doConstruct() {
 *             System.out.println("construct");
 *         }
 *     }
 *
 *     public static class DestroyService {
 *
 *         public void doDestroy() {
 *             System.out.println("destroy");
 *         }
 *     }
 *
 *     @Target(FIELD)
 *     @Retention(RUNTIME)
 *     public @interface XResource {
 *     }
 *
 *     @Target(METHOD)
 *     @Retention(RUNTIME)
 *     public @interface XPostConstruct {
 *     }
 *
 *     @Target(METHOD)
 *     @Retention(RUNTIME)
 *     public @interface XPreDestroy {
 *     }
 * }
 * }</pre>
 * The example will output:
 * <pre>{@code
 * construct
 * do service...[proxied]
 * before destroy
 * destroy
 * }</pre>
 * <p>
 * In the example above, {@code resourceAnnotation}, {@code postConstructAnnotation} and {@code preDestroyAnnotation}
 * methods specify the annotation classes used to mark resources, post-construct methods and pre-destroy methods. If not
 * specified, the default is are {@code javax.annotation.Resource}/{@code jakarta.annotation.Resource},
 * {@code javax.annotation.PostConstruct}/{@code jakarta.annotation.PostConstruct} and
 * {@code javax.annotation.PreDestroy}/{@code jakarta.annotation.PreDestroy}. {@code resourceTypes} method specifies the
 * root resource types.
 * <p>
 * After configuration is complete, the {@code build} method executes the following process:
 * <ol>
 *   <li>Starting from the specified root type, scan each type's fields annotated as resources until all
 *       resource types are found (the root type itself is included, and duplicate types are counted only once).</li>
 *   <li>Instantiate each type using its no-argument constructor to create resource instances in singleton mode.</li>
 *   <li>Inject resource instances into their corresponding fields.</li>
 *   <li>After injection completes, identify all resource instances that implement the
 *   {@link space.sunqian.common.app.di.InjectedAspect} interface s AOP handlers.</li>
 *   <li>Pass the remaining resource instances to each AOP handler sequentially to determine if they need to
 *       generate aspect instances. If any handler matches, it will generate the aspect instance and the resource
 *       instance will not be passed to subsequent handlers. The newly generated aspect instance replaces the
 *       original resource instance.</li>
 *   <li>Re-execute the injection process to inject the aspect instances (which replaced the original resource
 *       instances) into the corresponding fields.</li>
 *   <li>Execute post-construct methods in dependency order as specified by
 *   {@link space.sunqian.common.app.di.InjectedDependsOn} (if any). Note if any method execution fails, an exception is
 *   thrown and already executed methods are not rolled back.</li>
 * </ol>
 * <p>
 * An app can be inherited from a parent app:
 * <pre>{@code
 * InjectedApp child = InjectedApp.newBuilder()
 *     .parentApps(app)
 *     .resourceTypes(
 *         SomeObject.class,
 *         XServiceImpl.class
 *     )
 *     .build();
 * }</pre>
 * The {@code child} inherits all resources from its parent {@code app}. This simple DI framework supports singleton
 * mode for resources, so resources of the same types will be reused from the parent {@code app}, that is to say,
 * the resource of {@code XServiceImpl.class} in {@code child} will directly use its parent's instead of creating a new
 * one.
 * <p>
 * When {@code shutdown} method is executed, all {@code pre-destroy} methods in its app's resources are executed
 * sequentially according to their dependency relationships. If an exception occurs during the execution, an exception
 * will be thrown and the shutdown process will terminate immediately, and the remaining {@code pre-destroy} methods
 * will not be executed.
 * <p>
 * Once an app is shut down, it becomes invalid and cannot be restarted. However, its child-apps are not
 * automatically shut down along with it. Child-apps that depend on resources from this app will generally continue to
 * function normally, as long as those resources haven't been destroyed (by {@code pre-destroy} methods) during this
 * shutdown process.
 */
package space.sunqian.common.app.di;