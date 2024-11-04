package xyz.sunqian.common.bean.handlers;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.bean.BeanResolver;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * {@link BeanResolver.Handler} for non-prefix style, of which getters' and setters' names are themselves. For example:
 * <pre>
 *     // Getter of "foo"
 *     someData.foo();
 *     // Setter of "foo"
 *     someData.foo(String foo);
 * </pre>
 * Note the name cannot same with any name of method in {@link Object} such as "toString", "hashCode", "clone", etc.
 *
 * @author fredsuvn
 */
public class NonPrefixResolverHandler extends AbstractBeanResolverHandler {

    private static final Method[] BELONG_OBJECT = Object.class.getMethods();

    @Nullable
    protected AbstractBeanResolverHandler.GetterInfo resolveGetter(Method method) {
        if (belongObject(method)) {
            return null;
        }
        if (method.getParameterCount() == 0) {
            return buildGetter(method.getName(), method);
        }
        return null;
    }

    @Nullable
    protected AbstractBeanResolverHandler.SetterInfo resolveSetter(Method method) {
        if (belongObject(method)) {
            return null;
        }
        if (method.getParameterCount() == 1) {
            return buildSetter(method.getName(), method);
        }
        return null;
    }

    protected boolean belongObject(Method method) {
        for (Method objMethod : BELONG_OBJECT) {
            if (Objects.equals(objMethod.getName(), method.getName())) {
                return true;
            }
        }
        return false;
    }
}
