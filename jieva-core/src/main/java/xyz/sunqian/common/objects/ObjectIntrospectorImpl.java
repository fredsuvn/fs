package xyz.sunqian.common.objects;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.objects.handlers.JavaBeanResolverHandler;
import xyz.sunqian.common.coll.JieColl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

final class ObjectIntrospectorImpl implements ObjectIntrospector, ObjectIntrospector.Handler {

    static ObjectIntrospectorImpl SINGLETON =
        new ObjectIntrospectorImpl(Collections.singletonList(new JavaBeanResolverHandler()));

    private final List<ObjectIntrospector.Handler> handlers;

    ObjectIntrospectorImpl(Iterable<ObjectIntrospector.Handler> handlers) {
        this.handlers = JieColl.toList(handlers);
    }

    @Override
    public ObjectDef introspect(Type type) throws ObjectIntrospectionException {
        try {
            ContextImpl builder = new ContextImpl(type);
            for (Handler handler : handlers) {
                if (!handler.introspect(builder)){
                    break;
                }
            }
            return builder.build();
        } catch (Exception e) {
            throw new ObjectIntrospectionException(type, e);
        }
    }

    @Override
    public List<ObjectIntrospector.Handler> getHandlers() {
        return handlers;
    }

    @Override
    public ObjectIntrospector addFirstHandler(Handler handler) {
        List<ObjectIntrospector.Handler> newHandlers = new ArrayList<>(handlers.size() + 1);
        newHandlers.add(handler);
        newHandlers.addAll(handlers);
        return new ObjectIntrospectorImpl(newHandlers);
    }

    @Override
    public ObjectIntrospector addLastHandler(Handler handler) {
        List<ObjectIntrospector.Handler> newHandlers = new ArrayList<>(handlers.size() + 1);
        newHandlers.addAll(handlers);
        newHandlers.add(handler);
        return new ObjectIntrospectorImpl(newHandlers);
    }

    @Override
    public ObjectIntrospector replaceFirstHandler(Handler handler) {
        if (Objects.equals(handlers.get(0), handler)) {
            return this;
        }
        List<ObjectIntrospector.Handler> newHandlers = new ArrayList<>(handlers.size());
        newHandlers.addAll(handlers);
        newHandlers.set(0, handler);
        return new ObjectIntrospectorImpl(newHandlers);
    }

    @Override
    public ObjectIntrospector replaceLastHandler(Handler handler) {
        if (Objects.equals(handlers.get(handlers.size() - 1), handler)) {
            return this;
        }
        List<ObjectIntrospector.Handler> newHandlers = new ArrayList<>(handlers.size());
        newHandlers.addAll(handlers);
        newHandlers.set(newHandlers.size() - 1, handler);
        return new ObjectIntrospectorImpl(newHandlers);
    }

    @Override
    public ObjectIntrospector.Handler asHandler() {
        return this;
    }

    @Override
    public @Nullable boolean introspect(ObjectIntrospector.Context builder) {
        for (ObjectIntrospector.Handler handler : getHandlers()) {
            if (!handler.introspect(builder)){
                return false;
            }
        }
        return true;
    }

    static final class ContextImpl implements ObjectIntrospector.Context {

        private final Type type;
        private final Map<String, PropertyIntro> properties = new LinkedHashMap<>();
        private final List<MethodIntro> methods = new LinkedList<>();

        ContextImpl(Type type) {
            this.type = type;
        }

        @Override
        public Type getObjectType() {
            return type;
        }

        @Override
        public Map<String, PropertyIntro> propertyIntros() {
            return properties;
        }

        @Override
        public List<MethodIntro> methodIntros() {
            return methods;
        }

        private ObjectDef build() {
            return new ObjectDefImpl(type, properties, methods);
        }
    }

    private static final class ObjectDefImpl implements ObjectDef {

        private final Type type;
        private final Map<String, PropertyDef> properties;
        private final List<MethodDef> methods;

        private ObjectDefImpl(
            Type type,
            Map<String, PropertyIntro> properties,
            List<MethodIntro> methods
        ) {
            this.type = type;
            this.properties = JieColl.toMap(properties, name -> name, PropertyDefImpl::new);
            this.methods = JieColl.toList(methods, MethodDefImpl::new);
        }

        @Override
        public ObjectIntrospector getIntrospector() {
            return SINGLETON;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Map<String, PropertyDef> getProperties() {
            return properties;
        }

        @Override
        public List<MethodDef> getMethods() {
            return methods;
        }

        @Override
        public @Nullable MethodDef getMethod(String name, Class<?>... parameterTypes) {
            return null;
        }

        @Override
        public boolean equals(Object o) {
            return JieDef.equals(this, o);
        }

        @Override
        public int hashCode() {
            return JieDef.hashCode(this);
        }

        @Override
        public String toString() {
            return JieDef.toString(this);
        }

        private final class PropertyDefImpl implements PropertyDef {

            private final PropertyIntro base;

            private PropertyDefImpl(PropertyIntro propBase) {
                this.base = propBase;
            }

            @Override
            public ObjectDef getOwner() {
                return ObjectDefImpl.this;
            }

            @Override
            public String getName() {
                return base.getName();
            }

            @Override
            @Nullable
            public Object getValue(Object inst) {
                return base.getValue(inst);
            }

            @Override
            public void setValue(Object inst, @Nullable Object value) {
                base.setValue(inst, value);
            }

            @Override
            public Type getType() {
                return base.getType();
            }

            @Override
            public Class<?> getRawType() {
                return base.getRawType();
            }

            @Override
            public @Nullable Method getGetter() {
                return base.getGetter();
            }

            @Override
            public @Nullable Method getSetter() {
                return base.getSetter();
            }

            @Override
            public @Nullable Field getField() {
                return base.getField();
            }

            @Override
            public List<Annotation> getGetterAnnotations() {
                return base.getGetterAnnotations();
            }

            @Override
            public List<Annotation> getSetterAnnotations() {
                return base.getSetterAnnotations();
            }

            @Override
            public List<Annotation> getFieldAnnotations() {
                return base.getFieldAnnotations();
            }

            @Override
            public List<Annotation> getAnnotations() {
                return base.getAnnotations();
            }

            @Override
            public boolean isReadable() {
                return base.isReadable();
            }

            @Override
            public boolean isWriteable() {
                return base.isWriteable();
            }

            @Override
            public boolean equals(Object o) {
                return JieDef.equals(this, o);
            }

            @Override
            public int hashCode() {
                return JieDef.hashCode(this);
            }

            @Override
            public String toString() {
                return JieDef.toString(this);
            }
        }

        private final class MethodDefImpl implements MethodDef {

            private final MethodIntro base;

            private MethodDefImpl(MethodIntro propBase) {
                this.base = propBase;
            }

            @Override
            public ObjectDef getOwner() {
                return ObjectDefImpl.this;
            }

            @Override
            public String getName() {
                return base.getName();
            }

            @Override
            public Object invoke(Object inst, Object... args) {
                return base.invoke(inst, args);
            }

            @Override
            public Method getMethod() {
                return base.getMethod();
            }

            @Override
            public List<Annotation> getAnnotations() {
                return base.getAnnotations();
            }

            @Override
            public boolean equals(Object o) {
                return JieDef.equals(this, o);
            }

            @Override
            public int hashCode() {
                return JieDef.hashCode(this);
            }

            @Override
            public String toString() {
                return JieDef.toString(this);
            }
        }
    }
}
