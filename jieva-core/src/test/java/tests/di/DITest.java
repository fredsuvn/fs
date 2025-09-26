package tests.di;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.collect.ListKit;
import xyz.sunqian.common.di.SimpleApp;
import xyz.sunqian.common.di.SimpleAppAspect;
import xyz.sunqian.common.di.SimpleAppException;
import xyz.sunqian.common.di.SimpleDependsOn;
import xyz.sunqian.common.di.SimpleResource;
import xyz.sunqian.common.di.SimpleResourceDestroyException;
import xyz.sunqian.common.di.SimpleResourceInitialException;
import xyz.sunqian.common.runtime.reflect.TypeRef;
import xyz.sunqian.test.JieTestException;
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
                AspectService1Impl.class, AspectHandler.class,
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
        assertEquals(
            starter.aspectService1(),
            AspectService1Impl.class.getName() + ";" + interService.interService()
        );
        assertEquals(
            starter.aspectService2(),
            "call: " + starter.aspectService1() + ";" + interService.interService()
        );
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

    public static class Starter {

        @TestRes
        private ServiceAaa serviceAaa;
        @TestRes
        private ServiceBbb serviceBbb;
        @TestRes
        private InterService interService;
        @TestRes
        private AspectService1 aspectService1;
        @TestRes
        private AspectService2 aspectService2;
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

        public String aspectService1() {
            return aspectService1.aspectService1();
        }

        public String aspectService2() {
            return aspectService2.aspectService2();
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

        private final String name = "A";

        private String withoutAnnotation;

        @Nullable
        private String otherAnnotation;

        public String getLocalName() {
            return name;
        }

        public String getRemoteName() {
            return serviceBbb.getLocalName();
        }
    }

    public static class ServiceBbb {

        @TestRes
        private ServiceAaa serviceAaa;

        private final String name = "B";

        private String withoutAnnotation;

        @Nullable
        private String otherAnnotation;

        public String getLocalName() {
            return name;
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

    public interface AspectService1 {
        String aspectService1();
    }

    public static class AspectService1Impl implements AspectService1 {
        @Override
        public String aspectService1() {
            return AspectService1Impl.class.getName();
        }
    }

    public static class AspectService2 {

        @TestRes
        private AspectService1 aspectService1;

        public String aspectService2() {
            return "call: " + aspectService1.aspectService1();
        }
    }

    public static class AspectHandler implements SimpleAppAspect {

        @TestRes
        private InterService interService;

        @Override
        public boolean needsAspect(@Nonnull Type type) {
            return type.equals(AspectService1Impl.class) || type.equals(AspectService2.class);
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

    @Test
    public void testDependency() throws Exception {
        {
            Dep.postList.clear();
            Dep.destroyList.clear();
            SimpleApp app = SimpleApp.newBuilder()
                .resources(Dep1.class, Dep2.class, Dep3.class)
                .build();
            app.shutdown();
            assertEquals(Dep.postList, ListKit.list(1, 2, 3));
            assertEquals(Dep.destroyList, ListKit.list(1, 2, 3));
        }
        {
            Dep.postList.clear();
            Dep.destroyList.clear();
            SimpleApp app = SimpleApp.newBuilder()
                .resources(Dep1.class, Dep3.class, Dep2.class)
                .build();
            app.shutdown();
            assertEquals(Dep.postList, ListKit.list(1, 2, 3));
            assertEquals(Dep.destroyList, ListKit.list(1, 2, 3));
        }
        {
            Dep.postList.clear();
            Dep.destroyList.clear();
            SimpleApp app = SimpleApp.newBuilder()
                .resources(Dep2.class, Dep1.class, Dep3.class)
                .build();
            app.shutdown();
            assertEquals(Dep.postList, ListKit.list(1, 2, 3));
            assertEquals(Dep.destroyList, ListKit.list(1, 2, 3));
        }
        {
            Dep.postList.clear();
            Dep.destroyList.clear();
            SimpleApp app = SimpleApp.newBuilder()
                .resources(Dep2.class, Dep3.class, Dep1.class)
                .build();
            app.shutdown();
            assertEquals(Dep.postList, ListKit.list(1, 2, 3));
            assertEquals(Dep.destroyList, ListKit.list(1, 2, 3));
        }
        {
            Dep.postList.clear();
            Dep.destroyList.clear();
            SimpleApp app = SimpleApp.newBuilder()
                .resources(Dep3.class, Dep1.class, Dep2.class)
                .build();
            app.shutdown();
            assertEquals(Dep.postList, ListKit.list(1, 2, 3));
            assertEquals(Dep.destroyList, ListKit.list(1, 2, 3));
        }
        {
            Dep.postList.clear();
            Dep.destroyList.clear();
            SimpleApp app = SimpleApp.newBuilder()
                .resources(Dep3.class, Dep2.class, Dep1.class)
                .build();
            app.shutdown();
            assertEquals(Dep.postList, ListKit.list(1, 2, 3));
            assertEquals(Dep.destroyList, ListKit.list(1, 2, 3));
        }
        {
            expectThrows(SimpleAppException.class, () ->
                SimpleApp.newBuilder().resources(Dep4.class, Dep5.class).build());
            expectThrows(SimpleAppException.class, () ->
                SimpleApp.newBuilder().resources(Dep6.class, Dep7.class).build());
            expectThrows(SimpleAppException.class, () ->
                SimpleApp.newBuilder().resources(DepErr1.class).build());
            expectThrows(SimpleAppException.class, () ->
                SimpleApp.newBuilder().resources(DepErr2.class).build());
        }
        {
            SimpleApp app = SimpleApp.newBuilder()
                .resources(Dep8.class, Dep10.class, Dep9.class)
                .build();
            app.shutdown();
        }
        {
            SimpleApp app = SimpleApp.newBuilder()
                .resources(Dep9.class, Dep10.class, Dep8.class)
                .build();
            app.shutdown();
        }
    }

    public static class Dep {
        static final List<Integer> postList = new ArrayList<>();
        static final List<Integer> destroyList = new ArrayList<>();
    }

    public static class Dep1 {

        @PostConstruct
        public void postConstruct() {
            Dep.postList.add(1);
        }

        @PreDestroy
        public void preDestroy() {
            Dep.destroyList.add(1);
        }
    }

    public static class Dep2 {

        @PostConstruct
        @SimpleDependsOn(Dep1.class)
        public void postConstruct() {
            Dep.postList.add(2);
        }

        @PreDestroy
        @SimpleDependsOn(Dep1.class)
        public void preDestroy() {
            Dep.destroyList.add(2);
        }
    }

    public static class Dep3 {

        @PostConstruct
        @SimpleDependsOn({Dep1.class, Dep2.class})
        public void postConstruct() {
            Dep.postList.add(3);
        }

        @PreDestroy
        @SimpleDependsOn({Dep2.class, Dep1.class})
        public void preDestroy() {
            Dep.destroyList.add(3);
        }
    }

    public static class Dep4 {

        @PostConstruct
        @SimpleDependsOn(Dep5.class)
        public void postConstruct() {
        }
    }

    public static class Dep5 {

        @PostConstruct
        @SimpleDependsOn(Dep4.class)
        public void postConstruct() {
        }
    }

    public static class Dep6 {

        @PreDestroy
        @SimpleDependsOn(Dep7.class)
        public void preDestroy() {
        }
    }

    public static class Dep7 {

        @PreDestroy
        @SimpleDependsOn(Dep6.class)
        public void preDestroy() {
        }
    }

    public static class Dep8 {

        @PostConstruct
        @SimpleDependsOn({})
        public void postConstruct() {
        }

        @PreDestroy
        @SimpleDependsOn({})
        public void preDestroy() {
        }
    }

    public static class Dep9 {

        @PostConstruct
        public void postConstruct() {
        }

        @PreDestroy
        public void preDestroy() {
        }
    }

    public static class Dep10 {

        @PostConstruct
        public void postConstruct() {
        }

        @PreDestroy
        public void preDestroy() {
        }
    }

    public static class DepErr1 {

        @PostConstruct
        @SimpleDependsOn(String.class)
        public void postConstruct() {
        }
    }

    public static class DepErr2 {

        @PreDestroy
        @SimpleDependsOn(String.class)
        public void preDestroy() {
        }
    }

    @Test
    public void testStartAndShutdown() {
        {
            // startup
            SimpleResourceInitialException startErr = expectThrows(SimpleResourceInitialException.class, () -> {
                SimpleApp.newBuilder()
                    .resources(Dep8.class, Dep9.class, DepErr3.class, Dep10.class)
                    .build();
            });
            assertEquals(startErr.failedResource().type(), DepErr3.class);
            assertEquals(
                startErr.initializedResources().stream().map(SimpleResource::type).collect(Collectors.toList()),
                ListKit.list(Dep8.class, Dep9.class)
            );
            assertEquals(
                startErr.uninitializedResources().stream().map(SimpleResource::type).collect(Collectors.toList()),
                ListKit.list(Dep10.class)
            );
        }
        {
            // shutdown
            SimpleResourceDestroyException shutErr = expectThrows(SimpleResourceDestroyException.class, () -> {
                SimpleApp.newBuilder()
                    .resources(Dep8.class, Dep9.class, DepErr4.class, Dep10.class)
                    .build()
                    .shutdown();
            });
            assertEquals(shutErr.failedResource().type(), DepErr4.class);
            assertEquals(
                shutErr.destroyedResources().stream().map(SimpleResource::type).collect(Collectors.toList()),
                ListKit.list(Dep8.class, Dep9.class)
            );
            assertEquals(
                shutErr.undestroyedResources().stream().map(SimpleResource::type).collect(Collectors.toList()),
                ListKit.list(Dep10.class)
            );
        }
    }

    public static class DepErr3 {

        @PostConstruct
        public void postConstruct() {
            throw new JieTestException();
        }
    }

    public static class DepErr4 {

        @PreDestroy
        public void preDestroy() {
            throw new JieTestException();
        }
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
        {
            expectThrows(SimpleAppException.class, () -> {
                SimpleApp.newBuilder()
                    .resources(DepErr5.class)
                    .build();
            });
            expectThrows(SimpleAppException.class, () -> {
                SimpleApp.newBuilder()
                    .resources(DepErr5.class.getTypeParameters()[0])
                    .build();
            });
            expectThrows(SimpleAppException.class, () -> {
                SimpleApp.newBuilder()
                    .resources(DepErr6.class)
                    .build();
            });
        }
    }

    public static class DepErr5<T> {

        public DepErr5(int i) {
        }
    }

    public static class DepErr6 {

        @Resource
        private DepErr5 dep;
    }
}
