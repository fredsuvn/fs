//package codegen;
//
//import com.github.javaparser.ParserConfiguration;
//import com.github.javaparser.ast.CompilationUnit;
//import com.github.javaparser.ast.ImportDeclaration;
//import com.github.javaparser.ast.Node;
//import com.github.javaparser.ast.NodeList;
//import com.github.javaparser.ast.body.MethodDeclaration;
//import com.github.javaparser.ast.body.Parameter;
//import com.github.javaparser.ast.body.TypeDeclaration;
//import com.github.javaparser.ast.expr.AnnotationExpr;
//import com.github.javaparser.ast.expr.Expression;
//import com.github.javaparser.ast.expr.MemberValuePair;
//import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
//import com.github.javaparser.ast.stmt.BlockStmt;
//import com.github.javaparser.resolution.declarations.ResolvedAnnotationDeclaration;
//import com.github.javaparser.resolution.types.ResolvedType;
//import com.github.javaparser.symbolsolver.JavaSymbolSolver;
//import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
//import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
//import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
//import com.github.javaparser.utils.SourceRoot;
//import lombok.Data;
//import org.apache.commons.io.IOUtils;
//import org.apache.velocity.VelocityContext;
//import org.apache.velocity.app.VelocityEngine;
//import org.apache.velocity.runtime.RuntimeConstants;
//import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
//import xyz.srclab.annotations.Nullable;
//import xyz.srclab.build.annotations.FsMethod;
//import xyz.srclab.build.annotations.FsMethods;
//
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
///**
// * To generate Fs class.
// *
// * @author fredsuvn
// */
//public class FsGen {
//
//    private static final Path corePath;
//    private static final Path processorPath;
//    private static final Path fsPath;
//    private static final ParserConfiguration parserConfig;
//    private static final SourceRoot sourceRoot;
//    private static final boolean writeFs = true;
//
//    static {
//        String currentDir = System.getProperty("user.dir");
//        corePath = Paths.get(currentDir, "gek-core/src/main/java");
//        processorPath = Paths.get(currentDir, "gek-processor/src/main/java");
//        fsPath = Paths.get(currentDir, "gek-core/src/main/java/xyz/srclab/common/base/Fs.java");
//        parserConfig = new ParserConfiguration();
//        CombinedTypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver());
//        typeSolver.add(new JavaParserTypeSolver(corePath));
//        typeSolver.add(new JavaParserTypeSolver(processorPath));
//        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
//        parserConfig.setSymbolResolver(symbolSolver);
//        sourceRoot = new SourceRoot(corePath, parserConfig);
//    }
//
//    public static void main(String[] args) throws Exception {
//        List<FsPart> parts = resolveUtils();
//        writeFsJavaFile(parts);
//    }
//
//    private static void writeFsJavaFile(List<FsPart> parts) {
//        VelocityEngine engine = new VelocityEngine();
//        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
//        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
//        engine.init();
//        VelocityContext context = new VelocityContext();
//        context.put("utils", parts.stream().map(FsPart::toUtil).collect(Collectors.toList()));
//        StringWriter writer = new StringWriter();
//        engine.evaluate(context, writer, "vm", getFsVm());
//        if (writeFs) {
//            try {
//                PrintStream printStream = new PrintStream(new FileOutputStream(fsPath.toFile(), false));
//                printStream.println(writer);
//                printStream.close();
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            System.out.println("\r\n" + writer);
//        }
//    }
//
//    private static List<FsPart> resolveUtils() {
//        List<FsPart> parts = new LinkedList<>();
//        try {
//            sourceRoot.parse("", (localPath, absolutePath, result) -> {
//                if (!result.isSuccessful()) {
//                    return SourceRoot.Callback.Result.DONT_SAVE;
//                }
//                CompilationUnit cu = result.getResult().get();
//                NodeList<TypeDeclaration<?>> types = cu.getTypes();
//                if (types == null) {
//                    return SourceRoot.Callback.Result.DONT_SAVE;
//                }
//                for (TypeDeclaration<?> type : types) {
//                    for (AnnotationExpr annotation : type.getAnnotations()) {
//                        if (Objects.equals(annotation.resolve().getQualifiedName(), FsMethods.class.getName())) {
//                            List<MethodDeclaration> methods = new LinkedList<>();
//                            List<ImportDeclaration> imports = new LinkedList<>();
//                            resolveForType(type, methods, imports);
//                            FsPart part = new FsPart();
//                            part.setDesc(" Methods from " + type.getNameAsString() + ":");
//                            part.setMethods(methods);
//                            part.setImports(imports);
//                            imports.addAll(cu.findAll(ImportDeclaration.class));
//                            parts.add(part);
//                        }
//                    }
//                }
//                return SourceRoot.Callback.Result.DONT_SAVE;
//            });
//            return parts;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private static void resolveForType(
//        TypeDeclaration<?> type,
//        List<MethodDeclaration> methods,
//        List<ImportDeclaration> imports
//    ) {
//        methods.addAll(type.findAll(MethodDeclaration.class, it -> {
//                    if (!it.isStatic()) {
//                        return false;
//                    }
//                    return it.isPublic() || type.asClassOrInterfaceDeclaration().isInterface();
//                })
//                .stream().filter(method -> {
//                    for (AnnotationExpr annotation : method.getAnnotations()) {
//                        ResolvedAnnotationDeclaration re = annotation.resolve();
//                        if (Objects.equals(re.getQualifiedName(), FsMethod.class.getName())) {
//                            boolean ignored = isFsMethodIgnored(annotation);
//                            if (ignored) {
//                                return false;
//                            }
//                        }
//                    }
//                    return true;
//                }).peek(method -> {
//                    imports.add(new ImportDeclaration(type.resolve().getQualifiedName(), false, false));
//                    ResolvedType returnType = method.getType().resolve();
//                    if (returnType.isReferenceType()) {
//                        ImportDeclaration returnImport =
//                            new ImportDeclaration(returnType.asReferenceType().getQualifiedName(), false, false);
//                        imports.add(returnImport);
//                    }
//                    BlockStmt body = new BlockStmt();
//                    String callState;
//                    NodeList<Parameter> parameters = method.getParameters();
//                    if (parameters.isEmpty()) {
//                        callState = type.getNameAsString() + "." + method.getNameAsString() + "();";
//                    } else {
//                        callState = type.getNameAsString() + "." + method.getNameAsString() + "(" +
//                            parameters.stream().map(NodeWithSimpleName::getNameAsString).collect(Collectors.joining(", "))
//                            + ");";
//                        for (Parameter parameter : parameters) {
//                            ResolvedType paramType = parameter.resolve().getType();
//                            if (paramType.isReferenceType()) {
//                                ImportDeclaration returnImport =
//                                    new ImportDeclaration(paramType.asReferenceType().getQualifiedName(), false, false);
//                                imports.add(returnImport);
//                            }
//                        }
//                    }
//                    if (Objects.equals(returnType.describe(), "void")) {
//                        body.addStatement(callState);
//                    } else {
//                        body.addStatement("return " + callState);
//                    }
//                    method.setBody(body);
//                }).peek(method -> {
//                    Iterator<AnnotationExpr> it = method.getAnnotations().iterator();
//                    while (it.hasNext()) {
//                        AnnotationExpr annotation = it.next();
//                        ResolvedAnnotationDeclaration re = annotation.resolve();
//                        if (Objects.equals(re.getQualifiedName(), FsMethod.class.getName())) {
//                            it.remove();
//                            String newName = getFsMethodName(annotation);
//                            if (newName == null || newName.isEmpty()) {
//                                continue;
//                            }
//                            method.setName(newName);
//                        }
//                    }
//                }).collect(Collectors.toList())
//        );
//    }
//
//    @Nullable
//    private static String getFsMethodName(AnnotationExpr annotation) {
//        Expression expression = null;
//        if (annotation.isNormalAnnotationExpr()) {
//            expression = annotation.asNormalAnnotationExpr().getPairs().stream()
//                .filter(it -> Objects.equals(it.getNameAsString(), "name"))
//                .findFirst().orElse(NullMember.INSTANCE).getValue();
//        }
//        if (annotation.isSingleMemberAnnotationExpr()) {
//            expression = annotation.asSingleMemberAnnotationExpr().getMemberValue();
//        }
//        if (expression == null) {
//            return null;
//        }
//        return expression.toString().replaceAll("\"", "");
//    }
//
//    private static boolean isFsMethodIgnored(AnnotationExpr annotation) {
//        Expression expression = null;
//        if (annotation.isNormalAnnotationExpr()) {
//            expression = annotation.asNormalAnnotationExpr().getPairs().stream()
//                .filter(it -> Objects.equals(it.getNameAsString(), "ignored"))
//                .findFirst().orElse(NullMember.INSTANCE).getValue();
//        }
//        if (annotation.isSingleMemberAnnotationExpr()) {
//            expression = annotation.asSingleMemberAnnotationExpr().getMemberValue();
//        }
//        if (expression == null) {
//            return false;
//        }
//        return Boolean.parseBoolean(expression.toString());
//    }
//
//    private static String getFsVm() {
//        try {
//            InputStream in = FsGen.class.getResourceAsStream("/codegen/Fs.vm");
//            return IOUtils.toString(in, StandardCharsets.UTF_8);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private static class NullMember extends MemberValuePair {
//        public static final NullMember INSTANCE = new NullMember();
//    }
//
//    @Data
//    public static class FsPart {
//        private String desc;
//        private List<MethodDeclaration> methods;
//        private List<ImportDeclaration> imports;
//
//        public UtilPart toUtil() {
//            UtilPart utilPart = new UtilPart();
//            utilPart.setDesc(desc);
//            if (methods != null) {
//                utilPart.setMethods(methods.stream().map(Node::toString).collect(Collectors.toList()));
//            }
//            if (imports != null) {
//                utilPart.setImports(imports.stream().map(it -> "import " + it.getNameAsString() + ";").collect(Collectors.toList()));
//            }
//            return utilPart;
//        }
//    }
//
//    @Data
//    public static class UtilPart {
//        private String desc;
//        private List<String> methods;
//        private List<String> imports;
//    }
//}
