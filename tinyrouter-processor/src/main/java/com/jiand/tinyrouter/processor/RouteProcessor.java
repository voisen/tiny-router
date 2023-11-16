package com.jiand.tinyrouter.processor;

import com.google.auto.service.AutoService;
import com.jiand.tinyrouter.annotation.Autowired;
import com.jiand.tinyrouter.annotation.Route;
import com.jiand.tinyrouter.annotation.meta.route.AutowiredInfo;
import com.jiand.tinyrouter.annotation.meta.route.RouteType;
import com.jiand.tinyrouter.annotation.meta.route.TinyRouteMetaInfo;
import com.jiand.tinyrouter.processor.base.BaseProcessor;
import com.jiand.tinyrouter.processor.config.ProcessorConfig;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * @author jiand
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class RouteProcessor extends BaseProcessor {


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> set = new HashSet<>();
        set.add(Route.class.getCanonicalName());
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()){
            return true;
        }
        Set<? extends Element> rootElements = roundEnv.getRootElements();
        for (Element element : rootElements) {
            Route annotation = element.getAnnotation(Route.class);
            if (annotation != null){
                generateRouteMetaInfo(element, annotation);
            }
        }
        return false;
    }

    private void generateRouteMetaInfo(Element element, Route annotation) {
        RouteType routeType = getRouteType(element);
        String routePath = annotation.path();
        String[] strings = routePath.split("/");
        System.out.println("Info: " + routeType + "(" + routePath + ") => " + element);
        if (strings.length <= 2){
            mMessager.printMessage(Diagnostic.Kind.WARNING, routeType + "(" + routePath + ") => " + element + " : 路由路径建议使用两级结构，例如: /user/login");
        }
        StringBuilder className = new StringBuilder("_");
        for (String s : strings) {
            if (s.length() < 1){
                continue;
            }
            className.append(s.substring(0, 1).toUpperCase());
            className.append(s.substring(1));
        }
        className.append("TinyRouterLoader");
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        codeBlockBuilder.add("if(metaMap.containsKey($S)){\n\tthrow new $T($S);\n}\n", routePath, RuntimeException.class, "路由重复"+routePath+"，请设置正确的路由路径！");
        codeBlockBuilder.addStatement("$T params = new $T()", List.class, ArrayList.class);
        MethodSpec.Builder loadMetaMethodBuilder = MethodSpec.methodBuilder("loadMeta");
        if (element instanceof TypeElement){
            List<? extends Element> allMembers = mElements.getAllMembers((TypeElement) element);
            allMembers.forEach(member -> {
                Autowired autowired = member.getAnnotation(Autowired.class);
                if (autowired != null){
                    String name = autowired.name();
                    String fieldName = member.getSimpleName().toString();
                    if (name.isEmpty()){
                        name = fieldName;
                    }
                    codeBlockBuilder.addStatement("params.add(new $T($S,$S))", AutowiredInfo.class, name, fieldName);
                }
            });
        }
        codeBlockBuilder.add("try{\n");
        codeBlockBuilder.addStatement("\t$T metaInfo = new $T($L, Class.forName($S), $S, $L, params)",
                TinyRouteMetaInfo.class,
                TinyRouteMetaInfo.class,
                routeType,
                element,
                routePath,
                annotation.extras());
        codeBlockBuilder.addStatement("\tmetaMap.put($S, metaInfo)", routePath);

        codeBlockBuilder.add("}catch($T e){\n", Throwable.class);
        codeBlockBuilder.add("\te.printStackTrace();\n}");

        loadMetaMethodBuilder.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        loadMetaMethodBuilder.addModifiers(Modifier.PUBLIC);
        loadMetaMethodBuilder.addParameter(Map.class, "metaMap");
        loadMetaMethodBuilder.addCode(codeBlockBuilder.build());
        JavaFile.Builder javaFileBuilder = JavaFile.builder(ProcessorConfig.GENERATE_PACKAGE_NAME, TypeSpec.classBuilder(className.toString())
                        .addSuperinterface(mElements.getTypeElement("com.jiand.tinyrouter.core.loader._ITinyRouteLoader").asType())
                        .addMethod(loadMetaMethodBuilder.build())
                        .addJavadoc("这是 TinyRouter 自动生成的文件，请不要修改此文件。")
                .build());
        try {
            javaFileBuilder.addStaticImport(RouteType.class, "*");
            javaFileBuilder.build().writeTo(mFiler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}