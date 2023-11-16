package com.jiand.tinyrouter.plugin;

import com.android.build.api.transform.Format;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class TinyRouterTransform extends Transform {
    @Override
    public String getName() {
        return TinyRouterConfig.TINY_ROUTER_PACKAGE;
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        System.out.println("转换: " + transformInvocation);
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        if (!transformInvocation.isIncremental()){
            outputProvider.deleteAll();
        }
        //扫描jar包
        transformInvocation.getInputs().forEach(e -> {
            e.getJarInputs().forEach(jarInput -> {
                File jarInputFile = jarInput.getFile();
                String toName = jarInput.getName();
                toName = toName.substring(0, toName.length() - 4);
                toName += "_new.jar";
                File destJar = outputProvider.getContentLocation(toName, jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
                try {
                    FileUtils.copyFile(jarInputFile, destJar);
                    //扫描所有jar包文件
                    RouterScanner.scanJarFile(destJar);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            //扫描目录
            e.getDirectoryInputs().forEach(directoryInput -> {
                File contentLocation = outputProvider.getContentLocation(directoryInput.getName(), directoryInput.getContentTypes(), directoryInput.getScopes(), Format.DIRECTORY);
                File directoryInputFile = directoryInput.getFile();
                try {
                    FileUtils.copyDirectory(directoryInputFile, contentLocation);
                    handlerFile(contentLocation);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            });
        });

        TinyRouterInject.inject(RouterScanner.tinyRouterMapFileJar);
    }

    private void handlerFile(File inputFile) {
        if (inputFile.isDirectory()){
            File[] listFiles = inputFile.listFiles();
            if (listFiles == null){
                return;
            }
            for (File file : listFiles) {
                handlerFile(file);
            }
        }else {
            String filePath = inputFile.getPath();
            filePath = filePath.replaceAll("\\\\", "/");
            if (filePath.contains(TinyRouterConfig.TINY_ROUTER_LOADER_PREFIX)){
                RouterScanner.scanFile(inputFile);
            }
        }
    }


}
