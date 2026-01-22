package internal.samples;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.utils.di.DIAspectHandler;
import space.sunqian.fs.utils.di.DIContainer;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class DISample {

    public static void main(String[] args) {
        DIContainer diContainer = DIContainer.newBuilder()
            .componentTypes(
                XController.class,
                XServiceImpl.class,
                XServiceProxy.class,
                BeforeDestroyService.class
            )
            .componentAnnotation(XResource.class)
            .postConstructAnnotation(XPostConstruct.class)
            .preDestroyAnnotation(XPreDestroy.class)
            .build()
            .initialize();
        XService xService = diContainer.getObject(XService.class);
        System.out.println(xService.doService());
        DIContainer child = DIContainer.newBuilder()
            .parentContainers(diContainer)
            .componentTypes(
                XController.class,
                XServiceImpl.class,
                XServiceProxy.class,
                BeforeDestroyService.class
            )
            .componentAnnotation(XResource.class)
            .postConstructAnnotation(XPostConstruct.class)
            .preDestroyAnnotation(XPreDestroy.class)
            .build()
            .initialize();
        diContainer.shutdown();
        child.shutdown();
    }

    public static class XController {

        @XResource
        private XService xService;
        @XResource
        private ConstructService constructService;
        @XResource
        private DestroyService destroyService;

        @XPostConstruct
        public void init() {
            constructService.doConstruct();
        }

        @XPreDestroy
        public void destroy(BeforeDestroyService beforeDestroyService) {
            beforeDestroyService.doBeforeDestroyService();
            destroyService.doDestroy();
        }
    }

    public static class BeforeDestroyService {

        public void doBeforeDestroyService() {
            System.out.println("do before destroy");
        }

        @XPreDestroy
        public void destroy() {
            System.out.println("before destroy");
        }
    }

    public interface XService {

        String doService();
    }

    public static class XServiceImpl implements XService {

        @Override
        public String doService() {
            return "do service...";
        }
    }

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
        }

        @Override
        public @Nullable Object afterReturning(@Nullable Object result, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
            return result + "[proxied]";
        }

        @Override
        public @Nullable Object afterThrowing(@Nonnull Throwable ex, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) {
            return null;
        }
    }

    public static class ConstructService {

        public void doConstruct() {
            System.out.println("construct");
        }
    }

    public static class DestroyService {

        public void doDestroy() {
            System.out.println("destroy");
        }
    }

    @Target(FIELD)
    @Retention(RUNTIME)
    public @interface XResource {
    }

    @Target(METHOD)
    @Retention(RUNTIME)
    public @interface XPostConstruct {
    }

    @Target(METHOD)
    @Retention(RUNTIME)
    public @interface XPreDestroy {
    }
}
