//package xyz.srclab.build.processor;
//
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.ToString;
//import org.apache.commons.io.IOUtils;
//import org.apache.velocity.VelocityContext;
//import org.apache.velocity.app.VelocityEngine;
//import org.apache.velocity.runtime.RuntimeConstants;
//import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
//
//import javax.annotation.processing.*;
//import javax.lang.model.SourceVersion;
//import javax.lang.model.element.*;
//import javax.lang.model.type.*;
//import javax.lang.model.util.Elements;
//import javax.lang.model.util.Types;
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Path;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Objects;
//import java.util.Set;
//import java.util.stream.Collectors;
//
///**
// * To join static methods into Fs.
// *
// * @author fredsuvn
// */
//@SupportedSourceVersion(SourceVersion.RELEASE_8)
//@SupportedAnnotationTypes({
//    "xyz.srclab.build.annotations.FsMethods",
//    "xyz.srclab.build.annotations.FsMethod"
//})
//public class FsBuildingProcossor extends AbstractProcessor {
//
//    private Elements elementUtils;
//    private Types typeUtils;
//    private Path fsPath;
//    private String fsTemplate;
//    private final List<MethodInfo> methodInfos = new LinkedList<>();
//    private final boolean writeFs = false;
//
//    @Override
//    public synchronized void init(ProcessingEnvironment processingEnv) {
//        super.init(processingEnv);
//        //elementUtils = processingEnv.getElementUtils();
//        //typeUtils = processingEnv.getTypeUtils();
//        //String rootProjectPath = System.getProperty("rootProjectPath");
//        //fsPath = Paths.get(rootProjectPath, "gek-core/src/main/java/xyz/srclab/common/base/Fs.java");
//        //fsTemplate = getFsVm();
//        //
//        //System.out.println(">>>[[[ " + System.getProperties());
//    }
//
//    @Override
//    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        return false;
//        //try {
//        //    for (Element element : roundEnv.getElementsAnnotatedWith(FsMethods.class)) {
//        //        if (!(element instanceof TypeElement)) {
//        //            continue;
//        //        }
//        //        TypeElement typeElement = (TypeElement) element;
//        //        for (Element enclosedElement : typeElement.getEnclosedElements()) {
//        //            if (!(enclosedElement instanceof ExecutableElement)) {
//        //                continue;
//        //            }
//        //            ExecutableElement executableElement = (ExecutableElement) enclosedElement;
//        //            Set<Modifier> modifiers = executableElement.getModifiers();
//        //            if (!(modifiers.contains(Modifier.PUBLIC) && modifiers.contains(Modifier.STATIC))) {
//        //                continue;
//        //            }
//        //            MethodInfo methodInfo = buildMethodSource(executableElement);
//        //            //if (!methodInfos.contains(methodInfo)) {
//        //            methodInfos.add(methodInfo);
//        //            //}
//        //        }
//        //    }
//        //    if (roundEnv.processingOver()) {
//        //        writeFsJavaFile(fsPath.toString(), fsTemplate, methodInfos);
//        //    }
//        //    return false;
//        //} catch (Exception e) {
//        //    throw new RuntimeException(e);
//        //}
//    }
//
//    private MethodInfo buildMethodSource(ExecutableElement element) {
//        String comment = elementUtils.getDocComment(element);
//        String returnType = element.getReturnType().toString();
//        MethodInfo methodInfo = new MethodInfo();
//        methodInfo.setComment(comment);
//        methodInfo.setReturnType(getShortName(returnType));
//        methodInfo.setMethodName(element.getSimpleName().toString());
//
//        List<? extends TypeParameterElement> typeParameterElements = element.getTypeParameters();
//        if (typeParameterElements != null && !typeParameterElements.isEmpty()) {
//            methodInfo.setTypeParams(typeParameterElements.stream().map(it ->
//                buildElementName(it)).collect(Collectors.toList()));
//            //it.toString() + ":" + Arrays.toString(it.getClass().getInterfaces())).collect(Collectors.toList()));
//        }
//
//        //List<? extends VariableElement> paramElements = element.getParameters();
//        //if (!paramElements.isEmpty()) {
//        //    List<String> params = paramElements.stream().map(it ->
//        //        buildElementName(it) + " " + it.getSimpleName().toString()).collect(Collectors.toList());
//        //        //it.toString() + ":" + Arrays.toString(it.getClass().getInterfaces())).collect(Collectors.toList());
//        //    methodInfo.setParams(params);
//        //}
//        ExecutableType executableType = (ExecutableType) element.asType();
//        List<? extends TypeMirror> paramTypes = executableType.getParameterTypes();
//        if (!paramTypes.isEmpty()) {
//            List<String> params = paramTypes.stream().map(it ->
//                buildTypeName(it) + " ").collect(Collectors.toList());
//            //it.toString() + ":" + Arrays.toString(it.getClass().getInterfaces())).collect(Collectors.toList());
//            methodInfo.setParams(params);
//        }
//
//        List<String> ims = new LinkedList<>();
//        ims.add(returnType);
//        methodInfo.setIms(ims);
//        return methodInfo;
//    }
//
//    private String buildElementName(Element element) {
//        String annotations = "";
//        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
//        if (!annotationMirrors.isEmpty()) {
//            annotations = annotationMirrors.stream().map(i -> i.toString() + " ").collect(Collectors.joining());
//        }
//        if (element instanceof TypeParameterElement) {
//            TypeParameterElement typeParameterElement = (TypeParameterElement) element;
//            List<? extends TypeMirror> bounds = typeParameterElement.getBounds();
//            if (bounds.isEmpty() ||
//                (bounds.size() == 1 && Objects.equals(bounds.get(0).toString(), "java.lang.Object"))) {
//                return annotations + typeParameterElement;
//            }
//            String boundNames = bounds.stream().map(it ->
//                buildElementName(typeUtils.asElement(it))).collect(Collectors.joining(" & "));
//            return annotations + typeParameterElement + " extends " + boundNames;
//        }
//        if (element instanceof VariableElement) {
//            VariableElement variableElement = (VariableElement) element;
//            //List<? extends Element> elements = variableElement.getEnclosedElements();
//            //if (elements.isEmpty()) {
//            //    return annotations + buildTypeName(variableElement.asType());
//            //}
//            //String typeVars = elements.stream().map(this::buildElementName).collect(Collectors.joining(", "));
//            //return annotations + buildTypeName(variableElement.asType());// + "<" + typeVars + ">";
//            return variableElement + ":" + variableElement.getClass();
//        }
//        //if (typeMirror instanceof WildcardType) {
//        //    WildcardType wildcardType = (WildcardType) typeMirror;
//        //    TypeMirror up = wildcardType.getSuperBound();
//        //    if (up != null) {
//        //        return "? super " + buildTypeName(up);
//        //    }
//        //    TypeMirror sp = wildcardType.getExtendsBound();
//        //    if (sp != null) {
//        //        return "? extends " + buildTypeName(sp);
//        //    }
//        //}
//        //if (typeMirror instanceof IntersectionType) {
//        //    IntersectionType intersectionType = (IntersectionType) typeMirror;
//        //}
//        //if (typeMirror instanceof TypeVariable) {
//        //    TypeVariable typeVariable = (TypeVariable) typeMirror;
//        //    TypeMirror lower = typeVariable.getLowerBound();
//        //    if (lower != null && !(lower instanceof NullType)) {
//        //        return annotations + typeMirror + " super " + buildTypeName(lower);
//        //    }
//        //    TypeMirror upper = typeVariable.getUpperBound();
//        //    if (upper != null && !(upper instanceof NullType) && !Objects.equals("java.lang.Object", upper.toString())) {
//        //        return annotations + typeMirror + " extends " + buildTypeName(upper);
//        //    }
//        //}
//        return annotations + element;
//    }
//
//    private String buildTypeName(TypeMirror typeMirror) {
//        Element element = typeUtils.asElement(typeMirror);
//        String annotations = "";
//        List<? extends AnnotationMirror> annotationMirrors;
//        if (element != null) {
//            annotationMirrors = element.getAnnotationMirrors();
//        } else {
//            annotationMirrors = typeMirror.getAnnotationMirrors();
//        }
//        if (!annotationMirrors.isEmpty()) {
//            annotations = annotationMirrors.stream().map(i -> i.toString() + " ").collect(Collectors.joining());
//        }
//        if (typeMirror instanceof DeclaredType) {
//            DeclaredType declaredType = (DeclaredType) typeMirror;
//            List<? extends TypeMirror> args = declaredType.getTypeArguments();
//            if (args.isEmpty()) {
//                return annotations + declaredType.asElement().toString();
//            }
//            String argsNames = args.stream().map(it -> {
//                //Element e = typeUtils.asElement(it);
//                //if (e != null) {
//                //    return buildElementName(e);
//                //}
//                return buildTypeName(it);
//            }).collect(Collectors.joining(", "));
//            return annotations + declaredType.asElement().toString() + "<" + argsNames + ">";
//        }
//        if (typeMirror instanceof WildcardType) {
//            WildcardType wildcardType = (WildcardType) typeMirror;
//            TypeMirror up = wildcardType.getSuperBound();
//            if (up != null) {
//                return "? super " + buildTypeName(up);
//            }
//            TypeMirror sp = wildcardType.getExtendsBound();
//            if (sp != null) {
//                return "? extends " + buildTypeName(sp);
//            }
//        }
//        if (typeMirror instanceof IntersectionType) {
//            IntersectionType intersectionType = (IntersectionType) typeMirror;
//            List<? extends TypeMirror> bounds = intersectionType.getBounds();
//            String boundNames = bounds.stream().map(this::buildTypeName).collect(Collectors.joining(" & "));
//            return annotations + typeUtils.asElement(intersectionType) + " extends " + boundNames;
//        }
//        if (typeMirror instanceof TypeVariable) {
//            TypeVariable typeVariable = (TypeVariable) typeMirror;
//            annotations += ":" + typeVariable.asElement().getAnnotationMirrors();
//            TypeMirror lower = typeVariable.getLowerBound();
//            if (lower != null && !(lower instanceof NullType)) {
//                return annotations + typeMirror + " super " + buildTypeName(lower);
//            }
//            TypeMirror upper = typeVariable.getUpperBound();
//            if (upper != null && !(upper instanceof NullType) && !Objects.equals("java.lang.Object", upper.toString())) {
//                return annotations + typeMirror + " extends " + buildTypeName(upper);
//            }
//        }
//        return annotations + typeMirror;
//    }
//
//    private String getShortName(String name) {
//        int fromIndex = name.length() - 1;
//        int dotIndex = name.lastIndexOf(".", fromIndex);
//        int wordCount = 0;
//        while (true) {
//            if (dotIndex > 0) {
//                wordCount++;
//                if (wordCount > 1) {
//                    if (Character.isLowerCase(name.charAt(dotIndex + 1))) {
//                        return name.substring(fromIndex + 2);
//                    }
//                }
//            } else {
//                if (fromIndex == name.length() - 1) {
//                    return name;
//                }
//                if (Character.isLowerCase(name.charAt(0))) {
//                    return name.substring(fromIndex + 2);
//                }
//            }
//            fromIndex = dotIndex - 1;
//            dotIndex = name.lastIndexOf(".", fromIndex);
//        }
//    }
//
//    private String getFsVm() {
//        try {
//            InputStream in = FsBuildingProcossor.class.getResourceAsStream("/templates/Fs.vm");
//            return IOUtils.toString(in, StandardCharsets.UTF_8);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void writeFsJavaFile(String destPath, String template, List<MethodInfo> methodInfos) {
//        VelocityEngine engine = new VelocityEngine();
//        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
//        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
//        engine.init();
//        VelocityContext context = new VelocityContext();
//        context.put("methods", methodInfos);
//        List<String> ims = new LinkedList<>();
//        for (MethodInfo methodInfo : methodInfos) {
//            for (String im : methodInfo.getIms()) {
//                if (!ims.contains(im) && im.indexOf(".") > 0) {
//                    ims.add(im);
//                }
//            }
//        }
//        context.put("ims", ims);
//        StringWriter writer = new StringWriter();
//        engine.evaluate(context, writer, "vm", template);
//
//        if (writeFs) {
//            try {
//                PrintStream printStream = new PrintStream(new FileOutputStream(destPath, false));
//                printStream.println(writer);
//                printStream.close();
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            System.out.println("writer: " + writer);
//        }
//    }
//
//    @ToString
//    @EqualsAndHashCode
//    @Data
//    public static class MethodInfo {
//        private String comment;
//        private String returnType;
//        private String methodName;
//        private List<String> typeParams;
//        private List<String> params;
//        private List<String> ims;
//    }
//
//
//    //public static void main(String[] args) {
//    //    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//    //    int result = compiler.run(null, null, null,
//    //        "D:\\Projects\\SrcLab\\boat\\gek-core\\src\\main\\java\\xyz\\srclab\\common\\base\\FsCheck.java");
//    //    if (result == 0) {
//    //        System.out.println("Compilation completed successfully.");
//    //    } else {
//    //        System.err.println("Compilation failed.");
//    //    }
//    //}
//}
