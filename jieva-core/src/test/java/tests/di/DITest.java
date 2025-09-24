package tests.di;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.collect.ListKit;
import xyz.sunqian.common.di.SimpleApp;
import xyz.sunqian.common.di.SimpleAppAspect;
import xyz.sunqian.common.di.SimpleAppException;
import xyz.sunqian.common.di.SimpleDependsOn;
import xyz.sunqian.common.di.SimpleResource;
import xyz.sunqian.common.runtime.reflect.TypeRef;
import xyz.sunqian.test.PrintTest;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class DITest implements PrintTest {

    private static final List<String> postList = new ArrayList<>();
    private static final List<String> preList = new ArrayList<>();

    @Test
    public void testDIResources() {
        // app
        postList.clear();
        SimpleApp app = SimpleApp.newBuilder()
            .resources(
                Starter.class, ServiceAaa.class, ServiceBbb.class, InterServiceImpl.class,
                AspectServiceImpl.class, AspectHandler.class,
                new TypeRef<Generic<String>>() {}.type(), new TypeRef<Generic<Integer>>() {}.type(),
                NeedExecution.class, NeedExecution2.class, NeedExecution3.class
            )
            .resources(ListKit.list(NeedExecution.class, NeedExecution2.class, NeedExecution3.class))
            .resourceAnnotation(TestRes.class)
            .postConstructAnnotation(TestPost.class)
            .preDestroyAnnotation(TestPre.class)
            .aspect(true)
            .build();
        assertEquals(
            postList,
            ListKit.list(NeedExecution.class.getName(), NeedExecution2.class.getName(), NeedExecution3.class.getName())
        );
        List<SimpleResource> appResources = app.allResources();
        for (SimpleResource appResource : appResources) {
            assertTrue(appResource.isLocal());
        }
        assertEquals(appResources, app.localResources());
        assertEquals(app.dependencyApps(), Collections.emptyList());
        testDIResources(app);
        preList.clear();
        app.shutdown();
        assertEquals(
            preList,
            ListKit.list(NeedExecution.class.getName(), NeedExecution2.class.getName(), NeedExecution3.class.getName())
        );

        // app2
        postList.clear();
        SimpleApp app2 = SimpleApp.newBuilder()
            .resources(SubService2.class)
            .dependencyApps(app)
            .dependencyApps(ListKit.list(app))
            .build();
        assertEquals(
            postList,
            ListKit.list(SubService2.class.getName())
        );
        List<SimpleResource> app2Resources = app2.allResources();
        assertEquals(app2Resources.size(), 3);
        List<SimpleResource> app2LocalResources = app2.localResources();
        assertEquals(app2LocalResources.size(), 2);
        for (SimpleResource app2Resource : app2Resources) {
            if (app2Resource.type().equals(SubService.class)) {
                assertTrue(app2Resource.isLocal());
            } else if (app2Resource.type().equals(SubService2.class)) {
                assertTrue(app2Resource.isLocal());
            } else {
                assertFalse(app2Resource.isLocal());
            }
        }
        SubService subService = app2.getResource(SubService.class);
        assertEquals(subService.subService(), SubService.class.getName());
        SubService2 subService2 = app2.getResource(SubService2.class);
        assertEquals(subService2.subService2(), subService.subService());
        ServiceAaa serviceAaa = app2.getResource(ServiceAaa.class);
        assertEquals(serviceAaa.getLocalName(), "A");
        assertEquals(app2.dependencyApps(), ListKit.list(app));
        preList.clear();
        app2.shutdown();
        assertEquals(
            preList,
            ListKit.list(SubService2.class.getName())
        );
    }

    private void testDIResources(SimpleApp app) {
        printFor("Resources", app.allResources().stream()
            .map(r -> r.type().getTypeName() + ": " + r.instance())
            .collect(Collectors.joining(System.lineSeparator() + "    ")));
        // starter
        Starter starter = app.getResource(Starter.class);
        // common resource
        ServiceAaa serviceAaa = app.getResource(ServiceAaa.class);
        assertSame(serviceAaa, app.getResource(ServiceAaa.class));
        assertEquals(serviceAaa.getLocalName(), "A");
        assertEquals(serviceAaa.getRemoteName(), "B");
        ServiceBbb serviceBbb = app.getResource(ServiceBbb.class);
        assertSame(serviceBbb, app.getResource(ServiceBbb.class));
        assertEquals(serviceBbb.getLocalName(), "B");
        assertEquals(serviceBbb.getRemoteName(), "A");
        InterService interService = app.getResource(InterService.class);
        assertEquals(interService.interService(), InterServiceImpl.class.getName());
        assertEquals(starter.getNames(), "AB");
        assertEquals(starter.interService(), interService.interService());
        // aspect
        assertEquals(starter.aspectService(), AspectServiceImpl.class.getName() + ";" + interService.interService());
        // generic resource
        Generic<String> stringGeneric = app.getResource(new TypeRef<Generic<String>>() {});
        Generic<Integer> integerGeneric = app.getResource(new TypeRef<Generic<Integer>>() {});
        assertNotSame(stringGeneric, integerGeneric);
        assertEquals(stringGeneric.generic("X"), "X");
        assertEquals(integerGeneric.generic(100), 100);
        assertEquals(starter.generic("X"), "X");
        assertEquals(starter.generic(100), 100);
        GenericInter<String> stringGenericInter = app.getResource(new TypeRef<GenericInter<String>>() {});
        GenericInter<Integer> integerGenericInter = app.getResource(new TypeRef<GenericInter<Integer>>() {});
        assertSame(stringGenericInter, stringGeneric);
        assertSame(integerGenericInter, integerGeneric);
        assertEquals(starter.genericInter("X"), "X");
        assertEquals(starter.genericInter(100), 100);
    }

    @Test
    public void testException() throws Exception {
        {
            // SimpleAppException
            expectThrows(SimpleAppException.class, () -> {
                throw new SimpleAppException();
            });
            expectThrows(SimpleAppException.class, () -> {
                throw new SimpleAppException("");
            });
            expectThrows(SimpleAppException.class, () -> {
                throw new SimpleAppException("", new RuntimeException());
            });
            expectThrows(SimpleAppException.class, () -> {
                throw new SimpleAppException(new RuntimeException());
            });
        }
    }

    public static class Starter {

        @TestRes
        private ServiceAaa serviceAaa;
        @TestRes
        private ServiceBbb serviceBbb;
        @TestRes
        private InterService interService;
        @TestRes
        private AspectService aspectService;
        @TestRes
        private Generic<String> stringGeneric;
        @TestRes
        private Generic<Integer> integerGeneric;
        @TestRes
        private GenericInter<String> stringGenericInter;
        @TestRes
        private GenericInter<Integer> integerGenericInter;

        public String getNames() {
            return serviceAaa.getLocalName() + serviceBbb.getLocalName();
        }

        public String interService() {
            return interService.interService();
        }

        public String aspectService() {
            return aspectService.aspectService();
        }

        public String generic(String s) {
            return stringGeneric.generic(s);
        }

        public int generic(int i) {
            return integerGeneric.generic(i);
        }

        public String genericInter(String s) {
            return stringGenericInter.genericInter(s);
        }

        public int genericInter(int i) {
            return integerGenericInter.genericInter(i);
        }
    }

    public static class ServiceAaa {

        @TestRes
        private ServiceBbb serviceBbb;

        public String getLocalName() {
            return "A";
        }

        public String getRemoteName() {
            return serviceBbb.getLocalName();
        }
    }

    public static class ServiceBbb {

        @TestRes
        private ServiceAaa serviceAaa;

        public String getLocalName() {
            return "B";
        }

        public String getRemoteName() {
            return serviceAaa.getLocalName();
        }
    }

    public interface InterService {
        String interService();
    }

    public static class InterServiceImpl implements InterService {
        @Override
        public String interService() {
            return InterServiceImpl.class.getName();
        }
    }

    public interface AspectService {
        String aspectService();
    }

    public static class AspectServiceImpl implements AspectService {
        @Override
        public String aspectService() {
            return AspectServiceImpl.class.getName();
        }
    }

    public static class AspectHandler implements SimpleAppAspect {

        @TestRes
        private InterService interService;

        @Override
        public boolean needsAspect(@Nonnull Type type) {
            return type.equals(AspectServiceImpl.class);
        }

        @Override
        public boolean needsAspect(@Nonnull Method method) {
            return !method.getDeclaringClass().equals(Object.class);
        }

        @Override
        public void beforeInvoking(@Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
        }

        @Override
        public @Nullable Object afterReturning(@Nullable Object result, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
            return result + ";" + interService.interService();
        }

        @Override
        public @Nullable Object afterThrowing(@Nonnull Throwable ex, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) {
            return null;
        }
    }

    public interface GenericInter<T> {

        default T genericInter(T t) {
            return t;
        }
    }

    public static class Generic<T> implements GenericInter<T> {

        public T generic(T t) {
            return t;
        }
    }

    public static class NeedExecution {

        @TestPost
        public void postConstruct() {
            postList.add(getClass().getName());
        }

        @TestPre
        public void preDestroy() {
            preList.add(getClass().getName());
        }
    }

    public static class NeedExecution2 {

        @TestPost
        @SimpleDependsOn(NeedExecution.class)
        public void postConstruct() {
            postList.add(getClass().getName());
        }

        @TestPre
        @SimpleDependsOn(NeedExecution.class)
        public void preDestroy() {
            preList.add(getClass().getName());
        }
    }

    public static class NeedExecution3 {

        @TestPost
        @SimpleDependsOn({NeedExecution.class, NeedExecution2.class})
        public void postConstruct() {
            postList.add(getClass().getName());
        }

        @TestPre
        @SimpleDependsOn({NeedExecution.class, NeedExecution2.class})
        public void preDestroy() {
            preList.add(getClass().getName());
        }
    }

    public static class SubService {
        public String subService() {
            return SubService.class.getName();
        }
    }

    public static class SubService2 {

        @Resource
        private SubService subService;
        @Resource
        private ServiceAaa serviceAaa;

        public String subService2() {
            return subService.subService();
        }

        @PostConstruct
        public void postConstruct() {
            postList.add(getClass().getName());
        }

        @PreDestroy
        public void preDestroy() {
            preList.add(getClass().getName());
        }
    }
}
