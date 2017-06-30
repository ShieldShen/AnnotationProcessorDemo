package com.shie1d;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

/**
 * Created by shenli on 2017/6/30.
 */

public class FactoryGroupedClasses {
    private String typeName;
    private HashMap<String, FactoryAnnotatedClass> items = new HashMap<>();

    public FactoryGroupedClasses(String typeName) {
        this.typeName = typeName;
    }

    public void put(FactoryAnnotatedClass item) throws IllegalArgumentException, IdAlreadyUsedException {
        if (!StringUtil.equals(typeName, item.getTypeName())) {
            throw new IllegalArgumentException(item.getElement().getQualifiedName() + " is not belong to " + typeName + " factory.");
        }
        FactoryAnnotatedClass factoryAnnotatedClass = items.get(item.getId());
        if (factoryAnnotatedClass != null) {
            throw new IdAlreadyUsedException(item.getId() + " already exist");
        }
        items.put(item.getId(), item);
    }

    private void print(String msg) {
        System.out.print("\n");
        System.out.print(msg);
    }

    public void generateCode(Filer filer, Types typeUtils, Elements elementUtils) throws IOException {
        print(typeName);
        String[] split = typeName.split("\\.");
        String factoryName = split[split.length - 1] + "Factory";
        print("step 0");
        TypeElement baseTypeElement = elementUtils.getTypeElement(typeName);
        PackageElement packageElement = elementUtils.getPackageOf(baseTypeElement);
        String packageName;
        if (packageElement == null) {
            packageName = "";
        } else {
            packageName = packageElement.getQualifiedName().toString();
        }

        print("step 1");
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(factoryName)
                .addModifiers(Modifier.PUBLIC);
        print(factoryName);
        MethodSpec.Builder createMethod = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.get(baseTypeElement.asType()))
                .addParameter(String.class, "id", Modifier.FINAL)
                .beginControlFlow("if ( id == null || \"\".equals(id))")
                .addStatement("return null")
                .endControlFlow();
        print("step 2");

        for (FactoryAnnotatedClass annoClass :
                items.values()) {
            String nameForCreate = annoClass.element.getSimpleName().toString();
            print(nameForCreate);
            try {
                createMethod.beginControlFlow("if ( $S.equals( $N ) )", nameForCreate, "id")
                        .addStatement("return new $N()", nameForCreate)
                        .endControlFlow();
            }catch (Exception e){
                print(e.toString());
            }
        }
        print("step 3");

        createMethod.addStatement("throw new IllegalArgumentException($S + $N)","can't find", "id");
        TypeSpec typeSpec = typeBuilder.addMethod(createMethod.build()).build();
        JavaFile.builder(packageName, typeSpec).build().writeTo(filer);
        print("step 4");
//        createMethod.addException(TypeName.get(elementUtils.getTypeElement(IllegalArgumentException.class.getCanonicalName()).asType()));
    }

    @Override
    public String toString() {
        return typeName;
    }
}
