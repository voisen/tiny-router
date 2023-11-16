package com.jiand.tinyrouter.processor.base;

import com.jiand.tinyrouter.annotation.meta.route.RouteType;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author jiand
 */
public abstract class BaseProcessor extends AbstractProcessor {


    protected Messager mMessager;
    protected Filer mFiler;
    protected Types mTypes;
    protected Elements mElements;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
        mTypes = processingEnv.getTypeUtils();
        mElements = processingEnv.getElementUtils();
    }

    protected RouteType getRouteType(Element element) {
        TypeMirror typeMirror = element.asType();
        for (RouteType value : RouteType.values()) {
            TypeElement typeElement = mElements.getTypeElement(value.getBase());
            if (typeElement == null){
                continue;
            }
            TypeMirror mirror = typeElement.asType();
            if (mTypes.isSubtype(typeMirror, mirror)){
                return value;
            }
        }
        return RouteType.UNKNOWN;
    }

}
