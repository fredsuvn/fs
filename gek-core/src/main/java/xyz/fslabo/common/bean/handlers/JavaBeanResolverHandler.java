package xyz.fslabo.common.bean.handlers;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.CaseFormatter;
import xyz.fslabo.common.base.JieString;
import xyz.fslabo.common.bean.BeanResolver;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * {@link BeanResolver.Handler} for the classical java bean, of which getters' names are start with:
 * <ul>
 *     <li>"get" -- for non-boolean returned type;</li>
 *     <li>"is" -- for boolean returned type;</li>
 * </ul>
 * and setters' names are start with "set".
 *
 * @author fredsuvn
 */
public class JavaBeanResolverHandler extends AbstractBeanResolverHandler {

    private final CaseFormatter caseFormatter = CaseFormatter.LOWER_CAMEL;

    @Nullable
    protected Getter resolveGetter(Method method) {
        if (method.getParameterCount() != 0) {
            return null;
        }
        String methodName = method.getName();
        Class<?> returnType = method.getReturnType();
        boolean isGetter = false;
        // getXxx
        if (methodName.length() > 3 && methodName.startsWith("get")
            && !Objects.equals(returnType, boolean.class) && !Objects.equals(returnType, Boolean.class)) {
            isGetter = true;
        }
        // isXxx
        else if (methodName.length() > 2 && methodName.startsWith("is")
            && (Objects.equals(returnType, boolean.class) || Objects.equals(returnType, Boolean.class))) {
            isGetter = true;
        }
        if (!isGetter) {
            return null;
        }
        List<CharSequence> wordList = caseFormatter.resolve(methodName);
        if (wordList.size() > 1 && (
            JieString.charEquals(wordList.get(0), "get") || JieString.charEquals(wordList.get(0), "is")
        )) {
            return buildGetter(caseFormatter.format(wordList.subList(1, wordList.size())), method);
        }
        return null;
    }

    @Nullable
    protected Setter resolveSetter(Method method) {
        if (method.getParameterCount() != 1) {
            return null;
        }
        String methodName = method.getName();
        boolean isSetter = methodName.length() > 3 && methodName.startsWith("set");
        // setXxx
        if (!isSetter) {
            return null;
        }
        List<CharSequence> wordList = caseFormatter.resolve(methodName);
        if (wordList.size() > 1 && JieString.charEquals(wordList.get(0), "set")) {
            return buildSetter(caseFormatter.format(wordList.subList(1, wordList.size())), method);
        }
        return null;
    }
}
