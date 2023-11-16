package com.jiand.tinyrouter.plugin;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * @author jiand
 */
public class TinyRouterInject {

    private interface RewriteCallback{
        byte[] apply(InputStream inputStream, JarEntry jarEntry) throws IOException;
    }
    private static void rewriteJarFile(File jarFile, RewriteCallback callback){
        JarOutputStream jarOutputStream = null;
        JarFile inputJarFile = null;
        try {
            inputJarFile = new JarFile(jarFile);
            File outputFile = new File(jarFile.getParent(), jarFile.getName() + ".tmp");
            if (outputFile.exists()){
                outputFile.delete();
            }
            OutputStream outputStream = Files.newOutputStream(outputFile.toPath());
            jarOutputStream = new JarOutputStream(outputStream);
            Enumeration<JarEntry> entries = inputJarFile.entries();
            while (entries.hasMoreElements()){
                JarEntry entry = entries.nextElement();
                jarOutputStream.putNextEntry(new ZipEntry(entry.getName()));
                InputStream inputStream = inputJarFile.getInputStream(entry);
                if (entry.getName().endsWith(".class")){
                    jarOutputStream.write(callback.apply(inputStream, entry));
                }else{
                    jarOutputStream.write(IOUtils.toByteArray(inputStream));
                }
                jarOutputStream.closeEntry();
                inputStream.close();
            }
            inputJarFile.close();
            jarOutputStream.close();
            jarOutputStream = null;
            jarFile.delete();
            boolean renameTo = outputFile.renameTo(jarFile);
            if (!renameTo){
                throw new RuntimeException("重命名失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public static void inject(File jarFile){
        rewriteJarFile(jarFile, (inputStream, jarEntry) -> {
            try {
                if (jarEntry.getName().contentEquals(TinyRouterConfig.TINY_ROUTER_ROUTER_MAP_FILE)){
                    return getInjectedBytes(inputStream);
                }
                return IOUtils.toByteArray(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static byte[] getInjectedBytes(InputStream inputStream) throws IOException {
        ClassReader classReader = new ClassReader(inputStream);
        ClassWriter classWriter = new ClassWriter(classReader, 0);
        RouterClassVisitor routerClassVisitor = new RouterClassVisitor(Opcodes.ASM5, classWriter);
        classReader.accept(routerClassVisitor, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }


    private static class InjectMethodVisitor extends MethodVisitor{
        public InjectMethodVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack+4, maxLocals);
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode <= Opcodes.RETURN  && opcode >= Opcodes.IRETURN){
                RouterScanner.tinyRouterClasses.forEach(route -> {
                    String className = route.replaceAll("/", ".");
                    //加载this
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitLdcInsn(className);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                            TinyRouterConfig.TINY_ROUTER_ROUTER_PROVIDER_NAME,
                            TinyRouterConfig.TINY_ROUTER_ROUTER_PROVIDER_REGISTER_METHOD_NAME,
                            "(Ljava/lang/String;)V",
                            false);
                });
            }
            super.visitInsn(opcode);
        }
    }

    private static class RouterClassVisitor extends ClassVisitor {
        public RouterClassVisitor(int i, ClassVisitor classVisitor) {
            super(i, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                                         String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
            if ("init".contentEquals(name)){
                return new InjectMethodVisitor(Opcodes.ASM5, methodVisitor);
            }
            return methodVisitor;
        }
    }

}
