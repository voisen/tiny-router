package com.jiand.tinyrouter.processor;

import com.google.auto.service.AutoService;
import com.jiand.tinyrouter.annotation.Interceptor;
import com.jiand.tinyrouter.annotation.meta.interceptor.RouteInterceptorInfo;
import com.jiand.tinyrouter.processor.base.BaseProcessor;
import com.jiand.tinyrouter.processor.config.ProcessorConfig;
import com.jiand.tinyrouter.processor.utils.StringUtils;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
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
public class InterceptorProcessor extends BaseProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> set = new HashSet<>();
        set.add(Interceptor.class.getCanonicalName());
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()){
            return true;
        }
        Set<? extends Element> rootElements = roundEnv.getRootElements();
        for (Element element : rootElements) {
            Interceptor annotation = element.getAnnotation(Interceptor.class);
            if (annotation != null){
                generateRouteMetaInfo(element, annotation);
            }
        }
        return false;
    }

    private void generateRouteMetaInfo(Element element, Interceptor annotation) {

        TypeElement typeElement = mElements.getTypeElement("com.jiand.tinyrouter.interfaces.ITinyRouterInterceptor");
        if (!mTypes.isSubtype(element.asType(), typeElement.asType())){
            mMessager.printMessage(Diagnostic.Kind.ERROR, "路由拦截器("+element+")必须实现`ITinyRouterInterceptor`接口 !");
            return;
        }
        System.out.println("Info: Interceptor" + " => " + element);
        String interceptorName = annotation.name();
        int priority = annotation.priority();
        int skipWhenExtras = annotation.skipWhenExtras();
        StringBuilder className = new StringBuilder("_");
        if (!interceptorName.isEmpty()){
            className.append(interceptorName.substring(0,1).toUpperCase());
            className.append(interceptorName.substring(1));
        }
        className.append("TinyRouteInterceptor$");
        className.append(StringUtils.randomClassSuffix(6));
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        MethodSpec.Builder loadMetaMethodBuilder = MethodSpec.methodBuilder("loadInterceptor");
        codeBlockBuilder.add("try{\n");

        codeBlockBuilder.addStatement("\tinterceptorInfoList.add(new $T($S, $L, $L, $T.forName($S)))",
                RouteInterceptorInfo.class,
                interceptorName,
                priority,
                skipWhenExtras,
                Class.class,
                element);

        codeBlockBuilder.add("}catch($T e){\n", Throwable.class);
        codeBlockBuilder.add("\te.printStackTrace();\n}");

        loadMetaMethodBuilder.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        loadMetaMethodBuilder.addParameter(List.class, "interceptorInfoList");
        loadMetaMethodBuilder.addCode(codeBlockBuilder.build());
        JavaFile.Builder javaFileBuilder = JavaFile.builder(ProcessorConfig.GENERATE_PACKAGE_NAME, TypeSpec.classBuilder(className.toString())
                        .addSuperinterface(mElements.getTypeElement("com.jiand.tinyrouter.core.loader._IInterceptorLoader").asType())
                        .addMethod(loadMetaMethodBuilder.build())
                        .addJavadoc("这是 TinyRouter 自动生成的文件，请不要修改此文件。")
                .build());
        try {
            javaFileBuilder.build().writeTo(mFiler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}