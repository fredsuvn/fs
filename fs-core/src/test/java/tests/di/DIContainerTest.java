package tests.di;

import internal.test.FsTestException;
import internal.test.PrintTest;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.collect.SetKit;
import space.sunqian.fs.di.DIAspectHandler;
import space.sunqian.fs.di.DIComponent;
import space.sunqian.fs.di.DIContainer;
import space.sunqian.fs.di.DIException;
import space.sunqian.fs.di.DIInitializeException;
import space.sunqian.fs.di.DIShutdownException;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DIContainerTest implements PrintTest {

    private static final List<String> postList = new ArrayList<>();
    private static final List<String> preList = new ArrayList<>();

    @Test
    public void testDIComponents() {
        // container1
        postList.clear();
        DIContainer container1 = DIContainer.newBuilder()
            .componentTypes(
                Starter.class, ServiceAaa.class, ServiceBbb.class, InterServiceImpl.class,
                AspectService1Impl.class, AspectHandler.class,
                new TypeRef<Generic<String>>() {}.type(), new TypeRef<Generic<Integer>>() {}.type(),
                NeedExecution.class, NeedExecution2.class, NeedExecution3.class
            )
            .componentTypes(ListKit.list(NeedExecution.class, NeedExecution2.class, NeedExecution3.class))
            .componentAnnotation(TestRes.class)
            .postConstructAnnotation(TestPost.class)
            .preDestroyAnnotation(TestPre.class)
            .componentResolver(DIComponent.defaultResolver())
            .fieldSetter(DIComponent.defaultFieldSetter())
            .build();
        assertFalse(container1.isInitialized());
        assertFalse(container1.isShutdown());
        assertThrows(DIException.class, container1::shutdown);
        container1.initialize();
        assertTrue(container1.isInitialized());
        assertThrows(DIException.class, container1::initialize);
        assertEquals(
            postList,
            ListKit.list(NeedExecution.class.getName(), NeedExecution2.class.getName(), NeedExecution3.class.getName())
        );
        Map<Type, DIComponent> c1Components = container1.components();
        for (DIComponent component : c1Components.values()) {
            assertTrue(component.isLocal());
        }
        assertEquals(c1Components, container1.localComponents());
        assertEquals(container1.parentContainers(), Collections.emptyList());
        testDIComponents(container1);
        preList.clear();
        container1.shutdown();
        assertTrue(container1.isShutdown());
        assertEquals(
            preList,
            ListKit.list(NeedExecution.class.getName(), NeedExecution2.class.getName(), NeedExecution3.class.getName())
        );
        assertThrows(DIException.class, container1::initialize);
        assertThrows(DIException.class, container1::shutdown);

        // container2
        postList.clear();
        DIContainer container2 = DIContainer.newBuilder()
            .componentTypes(SubService2.class, AspectHandler2.class)
            .parentContainers(container1)
            .parentContainers(ListKit.list(container1))
            .build();
        container2.initialize();
        assertEquals(
            postList,
            ListKit.list(SubService2.class.getName())
        );
        Map<Type, DIComponent> c2Components = container2.components();
        assertEquals(c2Components.size(), c1Components.size() + 3);
        Map<Type, DIComponent> c2LocalComponents = container2.localComponents();
        assertEquals(3, c2LocalComponents.size());
        for (DIComponent component : c2Components.values()) {
            if (component.type().equals(SubService.class)) {
                assertTrue(component.isLocal());
            } else if (component.instance() instanceof SubService2) {
                assertTrue(component.isLocal());
            } else if (component.instance() instanceof AspectHandler2) {
                assertTrue(component.isLocal());
            } else {
                assertFalse(component.isLocal());
            }
        }
        SubService subService = container2.getObject(SubService.class);
        assertEquals(subService.subService(), SubService.class.getName());
        SubService2 subService2 = container2.getObject(SubService2.class);
        assertEquals(subService2.subService2(), subService.subService() + "[" + AspectHandler2.class.getName() + "]");
        ServiceAaa serviceAaa = container2.getObject(ServiceAaa.class);
        assertEquals("A", serviceAaa.getLocalName());
        assertEquals(container2.parentContainers(), ListKit.list(container1));
        preList.clear();
        container2.shutdown();
        assertEquals(
            preList,
            ListKit.list(SubService2.class.getName())
        );

        // container3
        DIContainer container3 = DIContainer.newBuilder()
            .parentContainers(container1)
            .componentTypes(Starter.class)
            .build();
        container3.initialize();
        assertSame(container1.getObject(Starter.class), container3.getObject(Starter.class));

        {
            // error
            assertNull(container2.getObject(String.class));
        }
    }

    private void testDIComponents(DIContainer container) {
        printFor("Components", container.components().values().stream()
            .map(r -> r.type().getTypeName() + ": " + r.instance())
            .collect(Collectors.joining(System.lineSeparator() + "    ")));
        // starter
        Starter starter = container.getObject(Starter.class);
        assertNotNull(starter);
        // common components
        ServiceAaa serviceAaa = container.getObject(ServiceAaa.class);
        assertNotNull(serviceAaa);
        assertSame(serviceAaa, container.getObject(ServiceAaa.class));
        assertEquals("A", serviceAaa.getLocalName());
        assertEquals("B", serviceAaa.getRemoteName());
        ServiceBbb serviceBbb = container.getObject(ServiceBbb.class);
        assertNotNull(serviceBbb);
        assertSame(serviceBbb, container.getObject(ServiceBbb.class));
        assertEquals("B", serviceBbb.getLocalName());
        assertEquals("A", serviceBbb.getRemoteName());
        InterService interService = container.getObject(InterService.class);
        assertNotNull(interService);
        assertEquals(interService.interService(), InterServiceImpl.class.getName());
        assertEquals("AB", starter.getNames());
        assertEquals(starter.interService(), interService.interService());
        // dependencies
        DIComponent aaa = container.getComponent(ServiceAaa.class);
        assertNotNull(aaa);
        assertSame(serviceAaa, aaa.instance());
        DIComponent bbb = container.getComponent(ServiceBbb.class);
        assertNotNull(bbb);
        List<DIComponent> aaaDependencies = aaa.dependencies();
        assertEquals(2, aaaDependencies.size());
        assertTrue(aaaDependencies.contains(aaa));
        assertTrue(aaaDependencies.contains(bbb));
        // aspect
        assertEquals(
            starter.aspectService1(),
            AspectService1Impl.class.getName() + ";" + interService.interService()
        );
        assertEquals(
            starter.aspectService2(),
            "call: " + starter.aspectService1() + ";" + interService.interService()
        );
        // generic components
        Generic<String> stringGeneric = container.getObject(new TypeRef<Generic<String>>() {});
        Generic<Integer> integerGeneric = container.getObject(new TypeRef<Generic<Integer>>() {});
        assertNotSame(stringGeneric, integerGeneric);
        assertEquals("X", stringGeneric.generic("X"));
        assertEquals(100, integerGeneric.generic(100));
        assertEquals("X", starter.generic("X"));
        assertEquals(100, starter.generic(100));
        GenericInter<String> stringGenericInter = container.getObject(new TypeRef<GenericInter<String>>() {});
        GenericInter<Integer> integerGenericInter = container.getObject(new TypeRef<GenericInter<Integer>>() {});
        assertSame(stringGenericInter, stringGeneric);
        assertSame(integerGenericInter, integerGeneric);
        assertEquals("X", starter.genericInter("X"));
        assertEquals(100, starter.genericInter(100));
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
        private ServiceAaa serviceAaa;

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

        @TestPost
        public void checkSelf() {
            assertSame(serviceAaa, serviceAaa);
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

    public static class AspectHandler implements DIAspectHandler {

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
        public void postConstruct(NeedExecution dependent) {
            assertNotNull(dependent);
            postList.add(getClass().getName());
        }

        @TestPre
        public void preDestroy(NeedExecution dependent) {
            assertNotNull(dependent);
            preList.add(getClass().getName());
        }
    }

    public static class NeedExecution3 {

        @TestPost
        public void postConstruct(NeedExecution dependent, NeedExecution2 dependent2) {
            assertNotNull(dependent);
            assertNotNull(dependent2);
            postList.add(getClass().getName());
        }

        @TestPre
        public void preDestroy(NeedExecution dependent, NeedExecution2 dependent2) {
            assertNotNull(dependent);
            assertNotNull(dependent2);
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

    public static class AspectHandler2 implements DIAspectHandler {

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
            DIContainer container = DIContainer.newBuilder()
                .componentTypes(Dep1.class, Dep2.class, Dep3.class)
                .build()
                .initialize();
            DIComponent dep1 = container.getComponent(Dep1.class);
            assertNotNull(dep1);
            DIComponent dep2 = container.getComponent(Dep2.class);
            assertNotNull(dep2);
            assertEquals(1, dep2.postConstructDependencies().size());
            assertEquals(dep1, dep2.postConstructDependencies().get(0));
            assertEquals(1, dep2.preDestroyDependencies().size());
            assertEquals(dep1, dep2.preDestroyDependencies().get(0));
            container.shutdown();
            assertEquals(Dep.postList, ListKit.list(1, 2, 3));
            assertEquals(Dep.destroyList, ListKit.list(1, 2, 3));
        }
        {
            Dep.postList.clear();
            Dep.destroyList.clear();
            DIContainer container = DIContainer.newBuilder()
                .componentTypes(Dep1.class, Dep3.class, Dep2.class)
                .build()
                .initialize()
                .shutdown();
            assertEquals(Dep.postList, ListKit.list(1, 2, 3));
            assertEquals(Dep.destroyList, ListKit.list(1, 2, 3));
        }
        {
            Dep.postList.clear();
            Dep.destroyList.clear();
            DIContainer container = DIContainer.newBuilder()
                .componentTypes(Dep2.class, Dep1.class, Dep3.class)
                .build()
                .initialize()
                .shutdown();
            assertEquals(Dep.postList, ListKit.list(1, 2, 3));
            assertEquals(Dep.destroyList, ListKit.list(1, 2, 3));
        }
        {
            Dep.postList.clear();
            Dep.destroyList.clear();
            DIContainer container = DIContainer.newBuilder()
                .componentTypes(Dep2.class, Dep3.class, Dep1.class)
                .build()
                .initialize()
                .shutdown();
            assertEquals(Dep.postList, ListKit.list(1, 2, 3));
            assertEquals(Dep.destroyList, ListKit.list(1, 2, 3));
        }
        {
            Dep.postList.clear();
            Dep.destroyList.clear();
            DIContainer container = DIContainer.newBuilder()
                .componentTypes(Dep3.class, Dep1.class, Dep2.class)
                .build()
                .initialize()
                .shutdown();
            assertEquals(Dep.postList, ListKit.list(1, 2, 3));
            assertEquals(Dep.destroyList, ListKit.list(1, 2, 3));
        }
        {
            Dep.postList.clear();
            Dep.destroyList.clear();
            DIContainer container = DIContainer.newBuilder()
                .componentTypes(Dep3.class, Dep2.class, Dep1.class)
                .build()
                .initialize()
                .shutdown();
            assertEquals(Dep.postList, ListKit.list(1, 2, 3));
            assertEquals(Dep.destroyList, ListKit.list(1, 2, 3));
        }
        {
            DIContainer container = DIContainer.newBuilder().componentTypes(DepErr1.class).build();
            container.initialize();
            container.shutdown();
            DIContainer container2 = DIContainer.newBuilder().componentTypes(DepErr2.class).build();
            container2.initialize();
            container2.shutdown();
        }
        {
            assertThrows(DIException.class, () ->
                DIContainer.newBuilder().componentTypes(Dep4.class, Dep5.class).build().initialize());
            assertThrows(DIException.class, () ->
                DIContainer.newBuilder().componentTypes(Dep6.class, Dep7.class).build().initialize());
            assertThrows(DIException.class, () ->
                DIContainer.newBuilder().componentTypes(DepErr3.class).build().initialize());
        }
        {
            DIContainer container = DIContainer.newBuilder()
                .componentTypes(Dep8.class, Dep10.class, Dep9.class)
                .build()
                .initialize();
            container.shutdown();
        }
        {
            DIContainer container = DIContainer.newBuilder()
                .componentTypes(Dep9.class, Dep10.class, Dep8.class)
                .build()
                .initialize();
            container.shutdown();
        }
        {
            // no dependency
            DIContainer container = DIContainer.newBuilder()
                .componentTypes(Object.class)
                .build()
                .initialize();
            DIComponent res = container.getComponent(Object.class);
            assertNotNull(res);
            assertNull(res.postConstructMethod());
            res.postConstruct();
            assertNull(res.preDestroyMethod());
            res.preDestroy();
            container.shutdown();
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
        public void postConstruct(Dep1 dep1) {
            assertNotNull(dep1);
            Dep.postList.add(2);
        }

        @PreDestroy
        public void preDestroy(Dep1 dep1) {
            assertNotNull(dep1);
            Dep.destroyList.add(2);
        }
    }

    public static class Dep3 {

        @PostConstruct
        public void postConstruct(Dep1 dep1, Dep2 dep2) {
            assertNotNull(dep1);
            assertNotNull(dep2);
            Dep.postList.add(3);
        }

        @PreDestroy
        public void preDestroy(Dep2 dep2, Dep1 dep1) {
            assertNotNull(dep2);
            assertNotNull(dep1);
            Dep.destroyList.add(3);
        }
    }

    public static class Dep4 {

        @PostConstruct
        public void postConstruct(Dep5 dep5) {
            assertNotNull(dep5);
        }
    }

    public static class Dep5 {

        @PostConstruct
        public void postConstruct(Dep4 dep4) {
            assertNotNull(dep4);
        }
    }

    public static class Dep6 {

        @PreDestroy
        public void preDestroy(Dep7 dep7) {
            assertNotNull(dep7);
        }
    }

    public static class Dep7 {

        @PreDestroy
        public void preDestroy(Dep6 dep6) {
            assertNotNull(dep6);
        }
    }

    public static class Dep8 {

        @PostConstruct
        public void postConstruct() {
        }

        @PreDestroy
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
        public void postConstruct(String str) {
            assertEquals("", str);
        }
    }

    public static class DepErr2 {

        @PreDestroy
        public void preDestroy(String str) {
            assertEquals("", str);
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
            DIInitializeException startErr = assertThrows(DIInitializeException.class, () -> {
                DIContainer.newBuilder()
                    .componentTypes(Dep8.class, Dep9.class, ConstructErr.class, Dep10.class)
                    .build()
                    .initialize();
            });
            assertEquals(ConstructErr.class, startErr.failedComponent().type());
            for (DIComponent initializedComponent : startErr.initializedComponents()) {
                assertTrue(initializedComponent.isInitialized());
            }
            assertEquals(
                startErr.initializedComponents().stream().map(DIComponent::type).collect(Collectors.toSet()),
                SetKit.set(Dep8.class, Dep9.class)
            );
            for (DIComponent uninitializedComponent : startErr.uninitializedComponents()) {
                assertFalse(uninitializedComponent.isInitialized());
            }
            assertEquals(
                startErr.uninitializedComponents().stream().map(DIComponent::type).collect(Collectors.toSet()),
                SetKit.set(Dep10.class)
            );
        }
        {
            // shutdown
            DIShutdownException shutErr = assertThrows(DIShutdownException.class, () -> {
                DIContainer.newBuilder()
                    .componentTypes(Dep8.class, Dep9.class, DestroyErr.class, Dep10.class)
                    .build()
                    .initialize()
                    .shutdown();
            });
            assertEquals(DestroyErr.class, shutErr.failedComponent().type());
            for (DIComponent destroyedComponent : shutErr.destroyedComponents()) {
                assertTrue(destroyedComponent.isDestroyed());
            }
            assertEquals(
                shutErr.destroyedComponents().stream().map(DIComponent::type).collect(Collectors.toSet()),
                SetKit.set(Dep8.class, Dep9.class)
            );
            for (DIComponent undestroyedComponent : shutErr.undestroyedComponents()) {
                assertFalse(undestroyedComponent.isDestroyed());
            }
            assertEquals(
                shutErr.undestroyedComponents().stream().map(DIComponent::type).collect(Collectors.toSet()),
                SetKit.set(Dep10.class)
            );
        }
        {
            // PostConstruct and PreDestroy at same method
            DIContainer container = DIContainer.newBuilder()
                .componentTypes(Dep11.class)
                .build()
                .initialize();
            for (DIComponent component : container.components().values()) {
                assertSame(component.postConstructMethod(), component.preDestroyMethod());
            }
            container.shutdown();
        }
    }

    public static class ConstructErr {

        @PostConstruct
        public void postConstruct() {
            throw new FsTestException();
        }
    }

    public static class DestroyErr {

        @PreDestroy
        public void preDestroy() {
            throw new FsTestException();
        }
    }

    @Test
    public void testException() throws Exception {
        {
            // DIException
            assertThrows(DIException.class, () -> {
                throw new DIException();
            });
            assertThrows(DIException.class, () -> {
                throw new DIException("");
            });
            assertThrows(DIException.class, () -> {
                throw new DIException("", new RuntimeException());
            });
            assertThrows(DIException.class, () -> {
                throw new DIException(new RuntimeException());
            });
        }
        assertThrows(DIException.class, () -> {
            DIContainer.newBuilder()
                .componentTypes(DepErr5.class)
                .build();
        });
        assertThrows(DIException.class, () -> {
            DIContainer.newBuilder()
                .componentTypes(DepErr5.class.getTypeParameters()[0])
                .build();
        });
        assertThrows(DIException.class, () -> {
            DIContainer.newBuilder()
                .componentTypes(DepErr6.class)
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
