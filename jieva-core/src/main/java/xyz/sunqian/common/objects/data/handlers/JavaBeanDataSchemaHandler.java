package xyz.sunqian.common.objects.data.handlers;

import lombok.Data;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.CaseFormatter;
import xyz.sunqian.common.base.string.StringKit;
import xyz.sunqian.common.objects.data.DataSchemaParser;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * This is an implementation of {@link DataSchemaParser.Handler} which basically follows the <a
 * href="https://www.oracle.com/java/technologies/javase/javabeans-spec.html">JavaBeans</a> style, inheriting from
 * {@link AbstractDataSchemaHandler} and overriding the {@link AbstractDataSchemaHandler#resolveAccessor(Method)}
 * method.
 * <p>
 * This implementation resolves {@code getXxx} or {@code isXxx} methods as getters and {@code setXxx} methods as setters
 * according to lower camel case naming conventions.
 *
 * @author sunqian
 */
public class JavaBeanDataSchemaHandler extends AbstractDataSchemaHandler {

    private final CaseFormatter caseFormatter = CaseFormatter.LOWER_CAMEL;

    @Override
    protected @Nullable AccessorInfo resolveAccessor(Method method) {
        int parameterCount = method.getParameterCount();
        switch (parameterCount) {
            case 0:// maybe getter
                return tryGetter(method);
            case 1:// maybe setter
                return trySetter(method);
        }
        return null;
    }

    @Nullable
    private AccessorInfo tryGetter(Method method) {
        Class<?> returnType = method.getReturnType();
        if (Objects.equals(returnType, void.class)) {
            return null;
        }
        String methodName = method.getName();
        // getter's name should be getXxx or isXxx
        boolean isGetterName = false;
        if (methodName.length() > 3 && methodName.startsWith("get")) {
            isGetterName = true;
        } else if (
            (methodName.length() > 2 && methodName.startsWith("is"))
                && (Objects.equals(returnType, boolean.class) || Objects.equals(returnType, Boolean.class))
        ) {
            isGetterName = true;
        }
        if (!isGetterName) {
            return null;
        }
        List<CharSequence> getterNameWords = caseFormatter.resolve(methodName);
        if (getterNameWords.size() <= 1) {
            return null;
        }
        CharSequence firstWord = getterNameWords.get(0);
        if (!StringKit.charEquals(firstWord, "get") && !StringKit.charEquals(firstWord, "is")) {
            return null;
        }
        // sure it is a getter
        List<CharSequence> propertyNameWords = getterNameWords.subList(1, getterNameWords.size());
        return new AccessorInfoImpl(caseFormatter.format(propertyNameWords), true);
    }

    @Nullable
    private AccessorInfo trySetter(Method method) {
        String methodName = method.getName();
        // setter's name should be setXxx
        boolean isSetterName = methodName.length() > 3 && methodName.startsWith("set");
        if (!isSetterName) {
            return null;
        }
        List<CharSequence> setterNameWords = caseFormatter.resolve(methodName);
        if (setterNameWords.size() <= 1) {
            return null;
        }
        CharSequence firstWord = setterNameWords.get(0);
        if (!StringKit.charEquals(firstWord, "set")) {
            return null;
        }
        // sure it is a setter
        List<CharSequence> propertyNameWords = setterNameWords.subList(1, setterNameWords.size());
        return new AccessorInfoImpl(caseFormatter.format(propertyNameWords), false);
    }

    @Data
    private static final class AccessorInfoImpl implements AccessorInfo {
        private final String propertyName;
        private final boolean isGetter;
    }
}
