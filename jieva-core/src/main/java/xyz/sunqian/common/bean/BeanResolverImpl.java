package xyz.sunqian.common.bean;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Flag;
import xyz.sunqian.common.bean.handlers.JavaBeanResolverHandler;
import xyz.sunqian.common.coll.JieColl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

final class BeanResolverImpl implements BeanResolver, BeanResolver.Handler {

    static BeanResolverImpl DEFAULT_RESOLVER =
        new BeanResolverImpl(Collections.singletonList(new JavaBeanResolverHandler()));

    private final List<BeanResolver.Handler> handlers;

    BeanResolverImpl(Iterable<BeanResolver.Handler> handlers) {
        this.handlers = JieColl.toList(handlers);
    }

    @Override
    public BeanInfo resolve(Type type) throws BeanResolvingException {
        try {
            Context builder = new Context(type);
            for (Handler handler : handlers) {
                Flag flag = handler.resolve(builder);
                if (Objects.equals(flag, Flag.BREAK)) {
                    break;
                }
            }
            return builder.build();
        } catch (Exception e) {
            throw new BeanResolvingException(type, e);
        }
    }

    @Override
    public List<BeanResolver.Handler> getHandlers() {
        return handlers;
    }

    @Override
    public BeanResolver addFirstHandler(Handler handler) {
        List<BeanResolver.Handler> newHandlers = new ArrayList<>(handlers.size() + 1);
        newHandlers.add(handler);
        newHandlers.addAll(handlers);
        return new BeanResolverImpl(newHandlers);
    }

    @Override
    public BeanResolver addLastHandler(Handler handler) {
        List<BeanResolver.Handler> newHandlers = new ArrayList<>(handlers.size() + 1);
        newHandlers.addAll(handlers);
        newHandlers.add(handler);
        return new BeanResolverImpl(newHandlers);
    }

    @Override
    public BeanResolver replaceFirstHandler(Handler handler) {
        if (Objects.equals(handlers.get(0), handler)) {
            return this;
        }
        List<BeanResolver.Handler> newHandlers = new ArrayList<>(handlers.size());
        newHandlers.addAll(handlers);
        newHandlers.set(0, handler);
        return new BeanResolverImpl(newHandlers);
    }

    @Override
    public BeanResolver replaceLastHandler(Handler handler) {
        if (Objects.equals(handlers.get(handlers.size() - 1), handler)) {
            return this;
        }
        List<BeanResolver.Handler> newHandlers = new ArrayList<>(handlers.size());
        newHandlers.addAll(handlers);
        newHandlers.set(newHandlers.size() - 1, handler);
        return new BeanResolverImpl(newHandlers);
    }

    @Override
    public BeanResolver.Handler asHandler() {
        return this;
    }

    @Override
    public @Nullable Flag resolve(BeanResolver.Context builder) {
        for (BeanResolver.Handler handler : getHandlers()) {
            Flag flag = handler.resolve(builder);
            if (Objects.equals(flag, Flag.BREAK)) {
                return Flag.BREAK;
            }
        }
        return null;
    }

    static final class Context implements BeanResolver.Context {

        private final Type type;
        private final Map<String, BasePropertyInfo> properties = new LinkedHashMap<>();
        private final List<BaseMethodInfo> methods = new LinkedList<>();

        Context(Type type) {
            this.type = type;
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Map<String, BasePropertyInfo> getProperties() {
            return properties;
        }

        @Override
        public List<BaseMethodInfo> getMethods() {
            return methods;
        }

        private BeanInfo build() {
            return new BeanInfoImpl(type, properties, methods);
        }
    }

    private static final class BeanInfoImpl implements BeanInfo {

        private final Type type;
        private final Map<String, PropertyInfo> properties;
        private final List<MethodInfo> methods;

        private BeanInfoImpl(Type type, Map<String, BasePropertyInfo> properties, List<BaseMethodInfo> methods) {
            this.type = type;
            this.properties = JieColl.toMap(properties, name -> name, PropertyInfoImpl::new);
            this.methods = JieColl.toList(methods, MethodInfoImpl::new);
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public Map<String, PropertyInfo> getProperties() {
            return properties;
        }

        @Override
        public List<MethodInfo> getMethods() {
            return methods;
        }

        @Override
        public boolean equals(Object o) {
            return JieBean.equals(this, o);
        }

        @Override
        public int hashCode() {
            return JieBean.hashCode(this);
        }

        @Override
        public String toString() {
            return JieBean.toString(this);
        }

        private final class PropertyInfoImpl implements PropertyInfo {

            private final BasePropertyInfo base;

            private PropertyInfoImpl(BasePropertyInfo propBase) {
                this.base = propBase;
            }

            @Override
            public BeanInfo getOwner() {
                return BeanInfoImpl.this;
            }

            @Override
            public String getName() {
                return base.getName();
            }

            @Override
            @Nullable
            public Object getValue(Object bean) {
                return base.getValue(bean);
            }

            @Override
            public void setValue(Object bean, @Nullable Object value) {
                base.setValue(bean, value);
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
                return JieBean.equals(this, o);
            }

            @Override
            public int hashCode() {
                return JieBean.hashCode(this);
            }

            @Override
            public String toString() {
                return JieBean.toString(this);
            }
        }

        private final class MethodInfoImpl implements MethodInfo {

            private final BaseMethodInfo base;

            private MethodInfoImpl(BaseMethodInfo propBase) {
                this.base = propBase;
            }

            @Override
            public BeanInfo getOwner() {
                return BeanInfoImpl.this;
            }

            @Override
            public String getName() {
                return base.getName();
            }

            @Override
            public Object invoke(Object bean, Object... args) {
                return base.invoke(bean, args);
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
                return JieBean.equals(this, o);
            }

            @Override
            public int hashCode() {
                return JieBean.hashCode(this);
            }

            @Override
            public String toString() {
                return JieBean.toString(this);
            }
        }
    }
}
