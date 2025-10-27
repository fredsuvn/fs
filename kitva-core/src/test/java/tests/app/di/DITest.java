package tests.app.di;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.app.di.InjectedApp;
import space.sunqian.common.app.di.InjectedAppException;
import space.sunqian.common.app.di.InjectedAspect;
import space.sunqian.common.app.di.InjectedDependsOn;
import space.sunqian.common.app.di.InjectedResource;
import space.sunqian.common.app.di.InjectedResourceDestructionException;
import space.sunqian.common.app.di.InjectedResourceInitializationException;
import space.sunqian.common.collect.ListKit;
import space.sunqian.common.runtime.reflect.TypeRef;
import internal.test.KitvaTestException;
import internal.test.PrintTest;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DITest implements PrintTest {

    private static final List<String> postList = new ArrayList<>();
    private static final List<String> preList = new ArrayList<>();

    @Test
    public void testDIResources() {
        // app
        postList.clear();
        InjectedApp app = InjectedApp.newBuilder()
            .resourceTypes(
                Starter.class, ServiceAaa.class, ServiceBbb.class, InterServiceImpl.class,
                AspectService1Impl.class, AspectHandler.class,
                new TypeRef<Generic<String>>() {}.type(), new TypeRef<Generic<Integer>>() {}.type(),
                NeedExecution.class, NeedExecution2.class, NeedExecution3.class
            )
            .resourceTypes(ListKit.list(NeedExecution.class, NeedExecution2.class, NeedExecution3.class))
            .resourceAnnotation(TestRes.class)
            .postConstructAnnotation(TestPost.class)
            .preDestroyAnnotation(TestPre.class)
            .build();
        assertEquals(
            postList,
            ListKit.list(NeedExecution.class.getName(), NeedExecution2.class.getName(), NeedExecution3.class.getName())
        );
        List<InjectedResource> appResources = app.resources();
        for (InjectedResource appResource : appResources) {
            assertTrue(appResource.isLocal());
        }
        assertEquals(appResources, app.localResources());
        assertEquals(app.parentApps(), Collections.emptyList());
        testDIResources(app);
        preList.clear();
        app.shutdown();
        assertEquals(
            preList,
            ListKit.list(NeedExecution.class.getName(), NeedExecution2.class.getName(), NeedExecution3.class.getName())
        );

        // app2
        postList.clear();
        InjectedApp app2 = InjectedApp.newBuilder()
            .resourceTypes(SubService2.class, AspectHandler2.class)
            .parentApps(app)
            .parentApps(ListKit.list(app))
            .build();
        assertEquals(
            postList,
            ListKit.list(SubService2.class.getName())
        );
        List<InjectedResource> app2Resources = app2.resources();
        assertEquals(app2Resources.size(), appResources.size() + 3);
        List<InjectedResource> app2LocalResources = app2.localResources();
        assertEquals(app2LocalResources.size(), 3);
        for (InjectedResource app2Resource : app2Resources) {
            if (app2Resource.type().equals(SubService.class)) {
                assertTrue(app2Resource.isLocal());
            } else if (app2Resource.instance() instanceof SubService2) {
                assertTrue(app2Resource.isLocal());
            } else if (app2Resource.instance() instanceof AspectHandler2) {
                assertTrue(app2Resource.isLocal());
            } else {
                assertFalse(app2Resource.isLocal());
            }
        }
        SubService subService = app2.getResource(SubService.class);
        assertEquals(subService.subService(), SubService.class.getName());
        SubService2 subService2 = app2.getResource(SubService2.class);
        assertEquals(subService2.subService2(), subService.subService() + "[" + AspectHandler2.class.getName() + "]");
        ServiceAaa serviceAaa = app2.getResource(ServiceAaa.class);
        assertEquals(serviceAaa.getLocalName(), "A");
        assertEquals(app2.parentApps(), ListKit.list(app));
        preList.clear();
        app2.shutdown();
        assertEquals(
            preList,
            ListKit.list(SubService2.class.getName())
        );

        {
            // error
            assertNull(app2.getResource(String.class));
        }
    }

    private void testDIResources(InjectedApp app) {
        printFor("Resources", app.resources().stream()
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

    public static class AspectHandler implements InjectedAspect {

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
        @InjectedDependsOn(NeedExecution.class)
        public void postConstruct() {
            postList.add(getClass().getName());
        }

        @TestPre
        @InjectedDependsOn(NeedExecution.class)
        public void preDestroy() {
            preList.add(getClass().getName());
        }
    }

    public static class NeedExecution3 {

        @TestPost
        @InjectedDependsOn({NeedExecution.class, NeedExecution2.class})
        public void postConstruct() {
            postList.add(getClass().getName());
        }

        @TestPre
        @InjectedDependsOn({NeedExecution.class, NeedExecution2.class})
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
            postList.add(SubService2.class.getName());
        }

        @PreDestroy
        public void preDestroy() {
            preList.add(SubService2.class.getName());
        }
    }

    public static class AspectHandler2 implements InjectedAspect {

        @Override
        public boolean needsAspect(@Nonnull Type type) {
            return type.equals(SubService2.class);
        }

        @Override
        public boolean needsAspect(@Nonnull Method method) {
            return method.getName().equals("subService2");
        }

        @Override
        public void beforeInvoking(@Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
        }

        @Override
        public @Nullable Object afterReturning(@Nullable Object result, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
            return result + "[" + AspectHandler2.class.getName() + "]";
        }

        @Override
        public @Nullable Object afterThrowing(@Nonnull Throwable ex, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) {
            return null;
        }
    }

    @Test
    public void testDependency() throws Exception {
        {
            Dep.postList.clear();
            Dep.destroyList.clear();
            InjectedApp app = InjectedApp.newBuilder()
                .resourceTypes(Dep1.class, Dep2.class, Dep3.class)
                .build();
            app.shutdown();
            assertEquals(Dep.postList, ListKit.list(1, 2, 3));
            assertEquals(Dep.destroyList, ListKit.list(1, 2, 3));
        }
        {
            Dep.postList.clear();
            Dep.destroyList.clear();
            InjectedApp app = InjectedApp.newBuilder()
                .resourceTypes(Dep1.class, Dep3.class, Dep2.class)
                .build();
            app.shutdown();
            assertEquals(Dep.postList, ListKit.list(1, 2, 3));
            assertEquals(Dep.destroyList, ListKit.list(1, 2, 3));
        }
        {
            Dep.postList.clear();
            Dep.destroyList.clear();
            InjectedApp app = InjectedApp.newBuilder()
                .resourceTypes(Dep2.class, Dep1.class, Dep3.class)
                .build();
            app.shutdown();
            assertEquals(Dep.postList, ListKit.list(1, 2, 3));
            assertEquals(Dep.destroyList, ListKit.list(1, 2, 3));
        }
        {
            Dep.postList.clear();
            Dep.destroyList.clear();
            InjectedApp app = InjectedApp.newBuilder()
                .resourceTypes(Dep2.class, Dep3.class, Dep1.class)
                .build();
            app.shutdown();
            assertEquals(Dep.postList, ListKit.list(1, 2, 3));
            assertEquals(Dep.destroyList, ListKit.list(1, 2, 3));
        }
        {
            Dep.postList.clear();
            Dep.destroyList.clear();
            InjectedApp app = InjectedApp.newBuilder()
                .resourceTypes(Dep3.class, Dep1.class, Dep2.class)
                .build();
            app.shutdown();
            assertEquals(Dep.postList, ListKit.list(1, 2, 3));
            assertEquals(Dep.destroyList, ListKit.list(1, 2, 3));
        }
        {
            Dep.postList.clear();
            Dep.destroyList.clear();
            InjectedApp app = InjectedApp.newBuilder()
                .resourceTypes(Dep3.class, Dep2.class, Dep1.class)
                .build();
            app.shutdown();
            assertEquals(Dep.postList, ListKit.list(1, 2, 3));
            assertEquals(Dep.destroyList, ListKit.list(1, 2, 3));
        }
        {
            assertThrows(InjectedAppException.class, () ->
                InjectedApp.newBuilder().resourceTypes(Dep4.class, Dep5.class).build());
            assertThrows(InjectedAppException.class, () ->
                InjectedApp.newBuilder().resourceTypes(Dep6.class, Dep7.class).build());
            assertThrows(InjectedAppException.class, () ->
                InjectedApp.newBuilder().resourceTypes(DepErr1.class).build());
            assertThrows(InjectedAppException.class, () ->
                InjectedApp.newBuilder().resourceTypes(DepErr2.class).build());
            assertThrows(InjectedAppException.class, () ->
                InjectedApp.newBuilder().resourceTypes(DepErr3.class).build());
        }
        {
            InjectedApp app = InjectedApp.newBuilder()
                .resourceTypes(Dep8.class, Dep10.class, Dep9.class)
                .build();
            app.shutdown();
        }
        {
            InjectedApp app = InjectedApp.newBuilder()
                .resourceTypes(Dep9.class, Dep10.class, Dep8.class)
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
        @InjectedDependsOn(Dep1.class)
        public void postConstruct() {
            Dep.postList.add(2);
        }

        @PreDestroy
        @InjectedDependsOn(Dep1.class)
        public void preDestroy() {
            Dep.destroyList.add(2);
        }
    }

    public static class Dep3 {

        @PostConstruct
        @InjectedDependsOn({Dep1.class, Dep2.class})
        public void postConstruct() {
            Dep.postList.add(3);
        }

        @PreDestroy
        @InjectedDependsOn({Dep2.class, Dep1.class})
        public void preDestroy() {
            Dep.destroyList.add(3);
        }
    }

    public static class Dep4 {

        @PostConstruct
        @InjectedDependsOn(Dep5.class)
        public void postConstruct() {
        }
    }

    public static class Dep5 {

        @PostConstruct
        @InjectedDependsOn(Dep4.class)
        public void postConstruct() {
        }
    }

    public static class Dep6 {

        @PreDestroy
        @InjectedDependsOn(Dep7.class)
        public void preDestroy() {
        }
    }

    public static class Dep7 {

        @PreDestroy
        @InjectedDependsOn(Dep6.class)
        public void preDestroy() {
        }
    }

    public static class Dep8 {

        @PostConstruct
        @InjectedDependsOn({})
        public void postConstruct() {
        }

        @PreDestroy
        @InjectedDependsOn({})
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

    public static class Dep11 {

        @PostConstruct
        @PreDestroy
        public void postConstructDestroy() {
        }
    }

    public static class DepErr1 {

        @PostConstruct
        @InjectedDependsOn(String.class)
        public void postConstruct() {
        }
    }

    public static class DepErr2 {

        @PreDestroy
        @InjectedDependsOn(String.class)
        public void preDestroy() {
        }
    }

    public static class DepErr3 {

        @Resource
        private Number field;
    }

    @Test
    public void testStartAndShutdown() {
        {
            // startup
            InjectedResourceInitializationException startErr = assertThrows(InjectedResourceInitializationException.class, () -> {
                InjectedApp.newBuilder()
                    .resourceTypes(Dep8.class, Dep9.class, ConstructErr.class, Dep10.class)
                    .build();
            });
            assertEquals(startErr.failedResource().type(), ConstructErr.class);
            for (InjectedResource initializedResource : startErr.initializedResources()) {
                assertTrue(initializedResource.isInitialized());
            }
            assertEquals(
                startErr.initializedResources().stream().map(InjectedResource::type).collect(Collectors.toList()),
                ListKit.list(Dep8.class, Dep9.class)
            );
            for (InjectedResource uninitializedResource : startErr.uninitializedResources()) {
                assertFalse(uninitializedResource.isInitialized());
            }
            assertEquals(
                startErr.uninitializedResources().stream().map(InjectedResource::type).collect(Collectors.toList()),
                ListKit.list(Dep10.class)
            );
        }
        {
            // shutdown
            InjectedResourceDestructionException shutErr = assertThrows(InjectedResourceDestructionException.class, () -> {
                InjectedApp.newBuilder()
                    .resourceTypes(Dep8.class, Dep9.class, DestroyErr.class, Dep10.class)
                    .build()
                    .shutdown();
            });
            assertEquals(shutErr.failedResource().type(), DestroyErr.class);
            for (InjectedResource destroyedResource : shutErr.destroyedResources()) {
                assertTrue(destroyedResource.isDestroyed());
            }
            assertEquals(
                shutErr.destroyedResources().stream().map(InjectedResource::type).collect(Collectors.toList()),
                ListKit.list(Dep8.class, Dep9.class)
            );
            for (InjectedResource undestroyedResource : shutErr.undestroyedResources()) {
                assertFalse(undestroyedResource.isDestroyed());
            }
            assertEquals(
                shutErr.undestroyedResources().stream().map(InjectedResource::type).collect(Collectors.toList()),
                ListKit.list(Dep10.class)
            );
        }
        {
            // PostConstruct and PreDestroy at same method
            InjectedApp app = InjectedApp.newBuilder()
                .resourceTypes(Dep11.class)
                .build();
            for (InjectedResource resource : app.resources()) {
                assertSame(resource.postConstructMethod(), resource.preDestroyMethod());
            }
            app.shutdown();
        }
    }

    public static class ConstructErr {

        @PostConstruct
        public void postConstruct() {
            throw new KitvaTestException();
        }
    }

    public static class DestroyErr {

        @PreDestroy
        public void preDestroy() {
            throw new KitvaTestException();
        }
    }

    @Test
    public void testException() throws Exception {
        {
            // InjectedSimpleAppException
            assertThrows(InjectedAppException.class, () -> {
                throw new InjectedAppException();
            });
            assertThrows(InjectedAppException.class, () -> {
                throw new InjectedAppException("");
            });
            assertThrows(InjectedAppException.class, () -> {
                throw new InjectedAppException("", new RuntimeException());
            });
            assertThrows(InjectedAppException.class, () -> {
                throw new InjectedAppException(new RuntimeException());
            });
        }
        assertThrows(InjectedAppException.class, () -> {
            InjectedApp.newBuilder()
                .resourceTypes(DepErr5.class)
                .build();
        });
        assertThrows(InjectedAppException.class, () -> {
            InjectedApp.newBuilder()
                .resourceTypes(DepErr5.class.getTypeParameters()[0])
                .build();
        });
        assertThrows(InjectedAppException.class, () -> {
            InjectedApp.newBuilder()
                .resourceTypes(DepErr6.class)
                .build();
        });
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
