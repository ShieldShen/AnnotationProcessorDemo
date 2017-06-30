package com.shie1d;

import com.shie1d.annotations.Factory;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

/**
 * Created by shenli on 2017/6/30.
 */

public class FactoryAnnotatedClass {
    public String id;
    public String typeName;
    public String simpleTypeName;

    public TypeElement element;

    public FactoryAnnotatedClass(TypeElement element) throws IllegalArgumentException {
        this.element = element;

        Factory annotation = element.getAnnotation(Factory.class);
        if (annotation == null) {
            throw new IllegalArgumentException(element.getQualifiedName().toString() + " can't find @Factory annotation.");
        }

        id = annotation.id();

        if (StringUtil.isEmpty(id)) {
            throw new IllegalArgumentException(element.getQualifiedName().toString() + " @Factory miss id value.");
        }

        try {
            Class clz = annotation.type();
            typeName = clz.getCanonicalName();
            simpleTypeName = clz.getSimpleName();
        } catch (MirroredTypeException e) {
            DeclaredType m = (DeclaredType) e.getTypeMirror();
            TypeElement typeElement = (TypeElement) m.asElement();
            typeName = typeElement.getQualifiedName().toString();
            simpleTypeName = typeElement.getSimpleName().toString();
        }

    }

    public String getId() {
        return id;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getSimpleTypeName() {
        return simpleTypeName;
    }

    public TypeElement getElement() {
        return element;
    }
}
