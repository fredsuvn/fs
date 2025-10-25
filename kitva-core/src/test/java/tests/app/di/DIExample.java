package tests.app.di;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.app.di.InjectedApp;
import space.sunqian.common.app.di.InjectedAspect;
import space.sunqian.common.app.di.InjectedDependsOn;
import internal.test.PrintTest;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class DIExample implements PrintTest {

    public static void main(String[] args) {
        InjectedApp app = InjectedApp.newBuilder()
            .resourceTypes(
                XController.class,
                XServiceImpl.class,
                XServiceProxy.class,
                BeforeDestroyService.class
            )
            .resourceAnnotation(XResource.class)
            .postConstructAnnotation(XPostConstruct.class)
            .preDestroyAnnotation(XPreDestroy.class)
            .build();
        XService xService = app.getResource(XService.class);
        System.out.println(xService.doService());
        app.shutdown();
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
        @InjectedDependsOn(BeforeDestroyService.class)
        public void destroy() {
            destroyService.doDestroy();
        }
    }

    public static class BeforeDestroyService {

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

    public static class XServiceProxy implements InjectedAspect {

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
