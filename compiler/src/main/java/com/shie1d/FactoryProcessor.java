package com.shie1d;

import com.google.auto.service.AutoService;
import com.shie1d.annotations.Factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class FactoryProcessor extends AbstractProcessor {

    private Messager mMessager;
    private Types mTypeUtils;
    private Filer mFiler;
    private Elements mElementUtils;
    private HashMap<String, FactoryGroupedClasses> factoryMap;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mTypeUtils = processingEnv.getTypeUtils();
        mMessager = processingEnv.getMessager();
        factoryMap = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Factory.class);
        for (Element element : elements) {
            if (ElementKind.CLASS != element.getKind()) {
                msg("@Factory wrong use in " + element.getSimpleName());
                return true;
            }

            TypeElement curTypeElement = (TypeElement) element;

            try {

                FactoryAnnotatedClass factoryAnnotatedClass = new FactoryAnnotatedClass(curTypeElement);

                if (invalidateAnnotatedClass(factoryAnnotatedClass)) {
                    return true;
                }

                String typeName = factoryAnnotatedClass.getTypeName();
                FactoryGroupedClasses factoryGroupedClasses = factoryMap.get(typeName);
                if (factoryGroupedClasses == null) {
                    factoryGroupedClasses = new FactoryGroupedClasses(typeName);
                    factoryMap.put(typeName, factoryGroupedClasses);
                }
                factoryGroupedClasses.put(factoryAnnotatedClass);

            } catch (IllegalArgumentException | IdAlreadyUsedException e) {
                msg("error when processing ");
                return true;
            }
        }

//        if(factoryMap == null || factoryMap.size() == 0){
//            msg("no find");
//            return true;
//        }
        for (FactoryGroupedClasses group :
                factoryMap.values()) {
            try {
                group.generateCode(mFiler, mTypeUtils, mElementUtils);
            } catch (Exception e) {
                msg("error when generate");
                return true;
            }
        }

        factoryMap.clear();
        return true;
    }

    private boolean invalidateAnnotatedClass(FactoryAnnotatedClass factoryAnnotatedClass) {
        TypeElement element = factoryAnnotatedClass.getElement();
        if (!element.getModifiers().contains(Modifier.PUBLIC)) {
            msg("@Factory annotated class " + element.getQualifiedName().toString() + " must be public");
            return true;
        }

        if (element.getModifiers().contains(Modifier.ABSTRACT)) {
            msg("@Factory annotated class " + element.getQualifiedName().toString() + " can't ne abstract.");
            return true;
        }

        TypeElement annoTypeElement = mElementUtils.getTypeElement(factoryAnnotatedClass.getTypeName());
        if (annoTypeElement.getKind() == ElementKind.INTERFACE) {
            List<? extends TypeMirror> interfaces = element.getInterfaces();
            if (interfaces == null || !interfaces.contains(annoTypeElement.asType())) {
                msg(element.getQualifiedName() + " doesn't implement " + factoryAnnotatedClass.getTypeName() + " interface.");
                return true;
            }
        } else {
            TypeElement curElement = element;
            while (true) {
                TypeMirror superclass = curElement.getSuperclass();
                if (superclass == null || superclass.getKind() == TypeKind.NONE) {
                    msg(element.getQualifiedName() + " does't extend " + factoryAnnotatedClass.getTypeName() + " class.");
                    return true;
                }
                if (mTypeUtils.isSameType(superclass, annoTypeElement.asType())) {
                    break;
                }
                curElement = (TypeElement) mTypeUtils.asElement(superclass);
            }
        }

        List<? extends Element> enclosedElements = element.getEnclosedElements();
        for (Element enclosedElement : enclosedElements) {
            if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructorElement = (ExecutableElement) enclosedElement;
                if (constructorElement.getModifiers().contains(Modifier.PUBLIC) &&
                        (constructorElement.getParameters() == null || constructorElement.getParameters().size() == 0)) {
                    return false;
                }
            }
        }
        msg(element.getQualifiedName() + " must have a public constructor with empty params.");

        return true;
    }

    private void msg(String msg) {
        if (mMessager != null) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, "" + msg);
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annos = new LinkedHashSet<>();
        annos.add(Factory.class.getCanonicalName());
        return annos;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
