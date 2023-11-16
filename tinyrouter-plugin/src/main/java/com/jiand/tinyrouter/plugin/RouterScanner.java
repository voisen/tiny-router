package com.jiand.tinyrouter.plugin;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * @author jiand
 */
public class RouterScanner {
    public static File tinyRouterMapFileJar = null;
    public static Set<String> tinyRouterClasses = new HashSet<>();

    public static void scanJarFile(File jarFile) {
        try {
            JarFile file = new JarFile(jarFile);
            Enumeration<JarEntry> entries = file.entries();
            while (entries.hasMoreElements()){
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entry.isDirectory()){
                    continue;
                }
                if (!entryName.endsWith(TinyRouterConfig.CLASS_EXT)){
                    continue;
                }
                if (entryName.contentEquals(TinyRouterConfig.TINY_ROUTER_ROUTER_MAP_FILE)){
                    tinyRouterMapFileJar = jarFile;
                }
                if (entryName.startsWith(TinyRouterConfig.TINY_ROUTER_LOADER_PREFIX)){
                    InputStream inputStream = file.getInputStream(entry);
                    if (inputStream == null){
                        continue;
                    }
                    scanInputStream(inputStream);
                    inputStream.close();
                }
            }
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void scanFile(File file) {
        if (!file.getName().endsWith(TinyRouterConfig.CLASS_EXT)){
            return;
        }
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            scanInputStream(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void scanInputStream(InputStream inputStream){
        try {
            ClassReader classReader = new ClassReader(inputStream);
            ClassWriter classWriter = new ClassWriter(classReader, 0);
            RouterClassVisitor classVisitor = new RouterClassVisitor(Opcodes.ASM5, classWriter);
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reset() {
        tinyRouterMapFileJar = null;
        tinyRouterClasses.clear();
    }


    static class RouterClassVisitor extends ClassVisitor {

        public RouterClassVisitor(int i, ClassVisitor classVisitor) {
            super(i, classVisitor);
        }

        @Override
        public void visit(int version, int access, String name, String signature,
                          String superName, String[] interfaces) {

            Arrays.asList(interfaces).forEach(interfaceName -> {
                if (interfaceName.contentEquals(TinyRouterConfig.TINY_ROUTER_INTERCEPTOR_LOADER)){
                    System.out.println("拦截器: " + name);
                    tinyRouterClasses.add(name);
                }
                if (interfaceName.contentEquals(TinyRouterConfig.TINY_ROUTER_ROUTE_META_LOADER)){
                    System.out.println("路由: " + name);
                    tinyRouterClasses.add(name);
                }
            });
        }
    }

}
