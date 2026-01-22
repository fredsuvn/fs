/**
 * This package is a simple dependency injection framework.
 * <p>
 * Here is a simple example:
 * <pre>{@code
 * public class DISample {
 *
 *     public static void main(String[] args) {
 *         DIContainer diContainer = DIContainer.newBuilder()
 *             .componentTypes(
 *                 XController.class,
 *                 XServiceImpl.class,
 *                 XServiceProxy.class,
 *                 BeforeDestroyService.class
 *             )
 *             .componentAnnotation(XResource.class)
 *             .postConstructAnnotation(XPostConstruct.class)
 *             .preDestroyAnnotation(XPreDestroy.class)
 *             .build()
 *             .initialize();
 *         XService xService = diContainer.getObject(XService.class);
 *         System.out.println(xService.doService());
 *         DIContainer child = DIContainer.newBuilder()
 *             .parentContainers(diContainer)
 *             .componentTypes(
 *                 XController.class,
 *                 XServiceImpl.class,
 *                 XServiceProxy.class,
 *                 BeforeDestroyService.class
 *             )
 *             .componentAnnotation(XResource.class)
 *             .postConstructAnnotation(XPostConstruct.class)
 *             .preDestroyAnnotation(XPreDestroy.class)
 *             .build()
 *             .initialize();
 *         diContainer.shutdown();
 *         child.shutdown();
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
 *         public void destroy(BeforeDestroyService beforeDestroyService) {
 *             beforeDestroyService.doBeforeDestroyService();
 *             destroyService.doDestroy();
 *         }
 *     }
 *
 *     public static class BeforeDestroyService {
 *
 *         public void doBeforeDestroyService() {
 *             System.out.println("do before destroy");
 *         }
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
 *     public static class XServiceProxy implements DIAspectHandler {
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
 * do before destroy
 * destroy
 * }</pre>
 * <p>
 * In the example above, a DI container called {@code diContainer} is created.
 * <p>
 * {@code componentAnnotation}, {@code postConstructAnnotation} and {@code preDestroyAnnotation} methods specify the
 * annotation classes used to mark components, post-construct methods and pre-destroy methods. If not specified, the
 * default are {@code javax.annotation.Resource}/{@code jakarta.annotation.Resource},
 * {@code javax.annotation.PostConstruct}/{@code jakarta.annotation.PostConstruct} and
 * {@code javax.annotation.PreDestroy}/{@code jakarta.annotation.PreDestroy}. {@code componentTypes} method specifies
 * the root component types.
 * <p>
 * After configuration is complete, the {@code build} method returns a new {@link space.sunqian.fs.utils.di.DIContainer}
 * instance to assign to the {@code diContainer} variable. The {@code build} method executes the following process:
 * <ol>
 *   <li>
 *       Scan all component types starting from the specified root type. The types' fields annotated as components, and
 *       the parameter types of the methods annotated as post-construct methods and pre-destroy methods, will also be
 *       considered as components.
 *   </li>
 *   <li>
 *       Instantiate each component type using its no-argument constructor to create component instances in singleton
 *       mode.
 *   </li>
 *   <li>
 *       Inject component instances into their corresponding fields.
 *   </li>
 *   <li>
 *       After injection completes, find out all component instances that implement the
 *       {@link space.sunqian.fs.utils.di.DIAspectHandler} interface as AOP handlers.
 *   </li>
 *   <li>
 *       Pass the remaining component instances to each AOP handler sequentially to determine if they need to
 *       generate aspect instances. If any handler matches, it will generate the aspect instance and the component
 *       instance will not be passed to subsequent handlers. Then the newly generated aspect instances replace the
 *       corresponding original component instances.
 *   </li>
 *   <li>
 *       Re-execute the injection process to inject the aspect instances (which replaced the corresponding original
 *       component instances) into the corresponding fields.
 *   </li>
 * </ol>
 * Note the {@code build} method only instantiates and injects component instances, and does not initialize them
 * (post-construct methods are not executed). Call {@link space.sunqian.fs.utils.di.DIContainer#initialize()} to initialize
 * the components.
 * <p>
 * When {@code initialize} method is executed, all {@code post-construct} methods in the container will be executed
 * sequentially according to their dependency relationships. The dependencies of each post-construct method are
 * declared by its parameter types, each parameter is considered as a dependency (like Field in component Class). And
 * when a post-construct method is executed, all its dependency parameter instances will be injected into it.
 * <p>
 * An DI container can be inherited from a parent container:
 * <pre>{@code
 * DIContainer child = DIContainer.newBuilder()
 *     .parentContainers(diContainer)
 *     .componentTypes(
 *         SomeObject.class,
 *         XServiceImpl.class
 *     )
 *     .build();
 * }</pre>
 * The {@code child} inherits all components from {@code diContainer}. This simple DI framework only supports singleton
 * mode for components, so components of the same types will be reused from the {@code diContainer}. That is to say, the
 * instance of {@code XServiceImpl.class} in {@code child} is the same instance as in {@code diContainer}.
 * <p>
 * To destroy an DI container, call its {@code shutdown} method. When {@code shutdown} method is executed, all
 * {@code pre-destroy} methods in the container will be executed sequentially according to their dependency
 * relationships. The dependencies of each pre-destroy method are declared by its parameter types, each parameter is
 * considered as a dependency (like Field in component Class). And when a pre-destroy method is executed,
 * all its dependency parameter instances will be injected into it.
 * <p>
 * Once a DI container is shutdown, it becomes invalid and cannot be restarted. However, its child containers are not
 * automatically shutdown along with it. For a DI container, the components inherited from the parent containers will be
 * destroyed with the shutdown of the parent containers, but its own components can still be used.
 * <p>
 * Core interface of this package:
 * <ul>
 *     <li>{@link space.sunqian.fs.utils.di.DIContainer}</li>
 *     <li>{@link space.sunqian.fs.utils.di.DIComponent}</li>
 *     <li>{@link space.sunqian.fs.utils.di.DIAspectHandler}</li>
 * </ul>
 * Utilities of this package:
 * <ul>
 *     <li>{@link space.sunqian.fs.utils.di.DIKit}</li>
 * </ul>
 */
package space.sunqian.fs.utils.di;