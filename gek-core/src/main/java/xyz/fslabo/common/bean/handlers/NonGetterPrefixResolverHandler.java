package xyz.fslabo.common.bean.handlers;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.CaseFormatter;
import xyz.fslabo.common.base.JieString;
import xyz.fslabo.common.bean.BeanResolver;

import java.lang.reflect.Method;
import java.util.List;

/**
 * {@link BeanResolver.Handler} for non-getter-prefix style (but setters have),
 * of which getters' names are themselves, and setters' names are start with "set".
 * For example:
 * <pre>
 *     // Getter of "foo"
 *     someData.foo();
 *     // Setter of "foo"
 *     someData.setFoo(String foo);
 * </pre>
 * Note the name cannot same with any name of method in {@link Object} such as "toString", "hashCode", "clone", etc.
 *
 * @author fredsuvn
 */
public class NonGetterPrefixResolverHandler extends NonPrefixResolverHandler {

    private final CaseFormatter namingCase = CaseFormatter.LOWER_CAMEL;

    @Nullable
    protected Setter resolveSetter(Method method) {
        if (belongObject(method)) {
            return null;
        }
        if (method.getParameterCount() != 1) {
            return null;
        }
        String methodName = method.getName();
        boolean isSetter = methodName.length() > 3 && methodName.startsWith("set");
        // setXxx
        if (!isSetter) {
            return null;
        }
        List<CharSequence> wordList = namingCase.resolve(methodName);
        if (wordList.size() > 1 && JieString.charEquals(wordList.get(0), "set")) {
            return buildSetter(namingCase.format(wordList.subList(1, wordList.size())), method);
        }
        return null;
    }
}
