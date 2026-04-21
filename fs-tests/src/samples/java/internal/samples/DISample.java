package internal.samples;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.di.DIAspectHandler;
import space.sunqian.fs.di.DIContainer;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Sample: Dependency Injection Usage
 * <p>
 * Purpose: Demonstrate how to use the dependency injection container provided by fs-core module.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     Create and initialize a DI container
 *   </li>
 *   <li>
 *     Register components and their dependencies
 *   </li>
 *   <li>
 *     Use lifecycle annotations (@PostConstruct, @PreDestroy)
 *   </li>
 *   <li>
 *     Implement aspect-oriented programming with DI
 *   </li>
 *   <li>
 *     Create parent-child container relationships
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link DIContainer}: The main DI container interface
 *   </li>
 *   <li>
 *     {@link DIAspectHandler}: Interface for aspect-oriented programming
 *   </li>
 * </ul>
 */
public class DISample {

    public static void main(String[] args) {
        demonstrateBasicDI();
        demonstrateParentChildContainer();
    }

    /**
     * Demonstrates basic dependency injection setup and usage.
     */
    public static void demonstrateBasicDI() {
        System.out.println("=== Basic DI Demonstration ===");

        // Create and initialize DI container
        DIContainer diContainer = DIContainer.newBuilder()
            .componentTypes(
                XController.class,
                XServiceImpl.class,
                XServiceProxy.class,
                BeforeDestroyService.class,
                ConstructService.class,
                DestroyService.class
            )
            .componentAnnotation(XResource.class)
            .postConstructAnnotation(XPostConstruct.class)
            .preDestroyAnnotation(XPreDestroy.class)
            .build()
            .initialize();

        // Get service from container
        XService xService = diContainer.getObject(XService.class);
        System.out.println("Service result: " + xService.doService());

        // Shutdown container
        diContainer.shutdown();
    }

    /**
     * Demonstrates parent-child container relationship.
     */
    public static void demonstrateParentChildContainer() {
        System.out.println("\n=== Parent-Child Container Demonstration ===");

        // Create parent container
        DIContainer parentContainer = DIContainer.newBuilder()
            .componentTypes(
                XServiceImpl.class,
                ConstructService.class,
                DestroyService.class
            )
            .componentAnnotation(XResource.class)
            .postConstructAnnotation(XPostConstruct.class)
            .preDestroyAnnotation(XPreDestroy.class)
            .build()
            .initialize();

        // Create child container with parent
        DIContainer childContainer = DIContainer.newBuilder()
            .parentContainers(parentContainer)
            .componentTypes(
                XController.class,
                XServiceProxy.class,
                BeforeDestroyService.class
            )
            .componentAnnotation(XResource.class)
            .postConstructAnnotation(XPostConstruct.class)
            .preDestroyAnnotation(XPreDestroy.class)
            .build()
            .initialize();

        // Get service from child container
        XService xService = childContainer.getObject(XService.class);
        System.out.println("Service result from child container: " + xService.doService());

        // Shutdown containers
        parentContainer.shutdown();
        childContainer.shutdown();
    }

    /**
     * Service interface for demonstration.
     */
    public interface XService {
        String doService();
    }

    /**
     * Custom resource annotation for dependency injection.
     */
    @Target(FIELD)
    @Retention(RUNTIME)
    public @interface XResource {
    }

    /**
     * Custom post-construct annotation for lifecycle management.
     */
    @Target(METHOD)
    @Retention(RUNTIME)
    public @interface XPostConstruct {
    }

    /**
     * Custom pre-destroy annotation for lifecycle management.
     */
    @Target(METHOD)
    @Retention(RUNTIME)
    public @interface XPreDestroy {
    }

    /**
     * Controller class demonstrating dependency injection and lifecycle methods.
     */
    public static class XController {

        @XResource
        private XService xService;

        @XResource
        private ConstructService constructService;

        @XResource
        private DestroyService destroyService;

        /**
         * Post-construct method called after dependency injection.
         */
        @XPostConstruct
        public void init() {
            System.out.println("XController initialized");
            constructService.doConstruct();
        }

        /**
         * Pre-destroy method called before container shutdown.
         */
        @XPreDestroy
        public void destroy(BeforeDestroyService beforeDestroyService) {
            System.out.println("XController destroying");
            beforeDestroyService.doBeforeDestroyService();
            destroyService.doDestroy();
        }
    }

    /**
     * Service demonstrating pre-destroy lifecycle method.
     */
    public static class BeforeDestroyService {

        public void doBeforeDestroyService() {
            System.out.println("BeforeDestroyService: do before destroy");
        }

        @XPreDestroy
        public void destroy() {
            System.out.println("BeforeDestroyService: destroying");
        }
    }

    /**
     * Implementation of XService interface.
     */
    public static class XServiceImpl implements XService {

        @Override
        public String doService() {
            return "XServiceImpl: do service...";
        }
    }

    /**
     * Aspect handler for XService implementation.
     */
    public static class XServiceProxy implements DIAspectHandler {

        @Override
        public boolean needsAspect(@Nonnull Type type) {
            return type.equals(XServiceImpl.class);
        }

        @Override
        public boolean needsAspect(@Nonnull Method method) {
            return method.getName().equals("doService");
        }

        @Override
        public void beforeInvoking(@Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
            System.out.println("XServiceProxy: before invoking doService");
        }

        @Override
        public @Nullable Object afterReturning(@Nullable Object result, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
            System.out.println("XServiceProxy: after returning from doService");
            return result + "[proxied]";
        }

        @Override
        public @Nullable Object afterThrowing(@Nonnull Throwable ex, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) {
            System.out.println("XServiceProxy: after throwing exception");
            return null;
        }
    }

    /**
     * Service for construction tasks.
     */
    public static class ConstructService {

        public void doConstruct() {
            System.out.println("ConstructService: constructing");
        }
    }

    /**
     * Service for destruction tasks.
     */
    public static class DestroyService {

        public void doDestroy() {
            System.out.println("DestroyService: destroying");
        }
    }
}
