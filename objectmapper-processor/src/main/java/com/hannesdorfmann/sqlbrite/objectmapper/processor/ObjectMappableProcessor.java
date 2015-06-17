package com.hannesdorfmann.sqlbrite.objectmapper.processor;

import android.database.Cursor;
import com.google.auto.service.AutoService;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.ObjectMappable;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class) public class ObjectMappableProcessor extends AbstractProcessor {

  private Elements elements;
  private Types types;
  private Messager messager;
  private Filer filer;
  private Set<ObjectMappableAnnotatedClass> annotatedClasses = new HashSet<>();

  @Override public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    messager = processingEnv.getMessager();
    types = processingEnv.getTypeUtils();
    elements = processingEnv.getElementUtils();
    filer = processingEnv.getFiler();
  }

  @Override public Set<String> getSupportedAnnotationTypes() {
    Set<String> annotations = new LinkedHashSet<>();
    annotations.add(Column.class.getCanonicalName());
    return annotations;
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  /**
   * Checks if a Element is in class and returns the TypeElement of the surrounding class
   *
   * @param e The element to check
   * @return The TypeElement of the surroungin class
   * @throws ProcessingException if surround class is not a element
   */
  private TypeElement getSurroundingClass(Element e) throws ProcessingException {
    if (e.getEnclosingElement().getKind() == ElementKind.CLASS) {
      return (TypeElement) e.getEnclosingElement();
    }

    throw new ProcessingException(e,
        "Field %s  is not part of a class. Only fields in a class can be annotated with %s",
        e.getSimpleName().toString(), Column.class.getSimpleName());
  }

  /**
   * Check the Element if it's a class and returns the corresponding TypeElement
   *
   * @param e The element to check
   * @return The {@link TypeElement} representing the annotated class
   * @throws ProcessingException If element is not a CLASS
   */
  private TypeElement checkAndGetClass(Element e) throws ProcessingException {

    if (e.getKind() != ElementKind.CLASS) {
      throw new ProcessingException(e,
          "%s is annotated with @%s but only classes can be annotated with this annotation",
          e.toString(), ObjectMappable.class.getSimpleName());
    }

    return (TypeElement) e;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

    try {

      // Collect all annotated classes
      for (Element e : roundEnv.getElementsAnnotatedWith(ObjectMappable.class)) {

        TypeElement classElement = checkAndGetClass(e);

        // Skip abstract classes
        if (e.getModifiers().contains(Modifier.ABSTRACT)) {
          continue;
        }

        // Check if class is already added to the annotated classes
        ObjectMappableAnnotatedClass annotatedClass = new ObjectMappableAnnotatedClass(classElement);
        if (annotatedClasses.contains(annotatedClass)) {
          continue;
        }

        annotatedClasses.add(annotatedClass);
        annotatedClass.scanForAnnotatedFields(types, elements);
      }

      // generate the code
      generateCode();
    } catch (ProcessingException e) {
      messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), e.getElement());
    } catch (IOException e) {
      e.printStackTrace();
      messager.printMessage(Diagnostic.Kind.ERROR,
          "Could not write generated files because of an IOException: " + e.getMessage());
    } finally {
      annotatedClasses.clear();
    }

    return false;
  }

  /**
   * Generate code
   */
  private void generateCode() throws IOException {

    for (ObjectMappableAnnotatedClass clazz : annotatedClasses) {

      PackageElement pkg = elements.getPackageOf(clazz.getElement());
      String packageName = pkg.isUnnamed() ? "" : pkg.getQualifiedName().toString();

      MethodSpec listMethod = generateListMethod(clazz);
      MethodSpec simpleListMethod = generateSimpleListMethod(clazz);
      MethodSpec singleMethod = generateSingleMethod(clazz);
      MethodSpec simpleSingleMethod = generateSimpleSingleMethod(clazz);

      // Generate the mapper class
      TypeSpec mapperClass = TypeSpec.classBuilder(clazz.getSimpleClassName() + "Mapper")
          .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
          .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build())
          .addMethod(simpleSingleMethod)
          .addMethod(singleMethod)
          .addMethod(simpleListMethod)
          .addMethod(listMethod)
          .build();

      JavaFile.builder(packageName, mapperClass).build().writeTo(filer);
    }
  }

  /**
   * Generates the method to create retrieve a List of items from cursor that only takes one cursor
   * as parameter and internally calls the method generate by {@link #generateListMethod(ObjectMappableAnnotatedClass)}
   * with true as throwOnIndexNotFound parameter
   *
   * @param clazz {@link ObjectMappableAnnotatedClass}
   * @return The MethodSpec
   */
  private MethodSpec generateSimpleListMethod(ObjectMappableAnnotatedClass clazz) {

    String cursorVarName = "cursor";
    ClassName list = ClassName.get("java.util", "List");
    TypeName elementType = ClassName.get(clazz.getElement().asType());
    TypeName typedList = ParameterizedTypeName.get(list, elementType);

    // List Method
    return MethodSpec.methodBuilder("list")
    /*
        .addJavadoc(
            "Fetches a list of {@ling $L} from a Cursor by scanning for @$L annotated fields. Calls {@link #list(Cursor,boolean)} with true as second parameter ",
            clazz.getElement().getSimpleName().toString(), Column.class.getSimpleName())
        .addJavadoc("@param $L The Cursor", cursorVarName)
        .addJavadoc(
            "@return An empty List if cursor is empty or a list of items fetched from the cursor")
        .addJavadoc("@see #list(Cursor,boolean)")
        */.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addParameter(Cursor.class, cursorVarName)
        .returns(typedList)
        .addStatement("return list($L, true)", cursorVarName)
        .build();
  }

  /**
   * Generates the method that takes a cursor and a boolean flag as parameter and returns a list of
   * Items
   *
   * @param clazz The {@link ObjectMappableAnnotatedClass}
   * @return The MethodSpec
   */
  private MethodSpec generateListMethod(ObjectMappableAnnotatedClass clazz) {

    String listVarName = "list";
    String objectVarName = "item";
    String cursorVarName = "cursor";
    String throwOnIndexNotFoundVarName = "throwOnIndexNotFound";

    ClassName list = ClassName.get("java.util", "List");
    ClassName arrayList = ClassName.get("java.util", "ArrayList");

    TypeName elementType = ClassName.get(clazz.getElement().asType());

    TypeName typedArrayList = ParameterizedTypeName.get(arrayList, elementType);
    TypeName typedList = ParameterizedTypeName.get(list, elementType);

    // List Method
    MethodSpec.Builder listMethod = MethodSpec.methodBuilder("list")
       /*
        .addJavadoc(
            "Fetches a list of {@ling $L} from a Cursor by scanning for @$L annotated fields",
            clazz.getElement().getSimpleName().toString(), Column.class.getSimpleName())
        .addJavadoc("@param $L The Cursor", cursorVarName)
        .addJavadoc(
            "@param $L <b>true</b>, if an {@link IllegalArgumentException} should be thrown if the column doesn't exist. <b>false</b> if missing columns should be skipped")
        .addJavadoc(
            "@return An empty List if cursor is empty or a list of items fetched from the cursor")
            */.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addParameter(Cursor.class, cursorVarName)
        .addParameter(boolean.class, throwOnIndexNotFoundVarName)
        .returns(typedList)
        .beginControlFlow("try")
        .beginControlFlow("if ($L == null || $L.getCount() == 0 || !$L.moveToFirst()) ",
            cursorVarName, cursorVarName, cursorVarName)
        .addStatement("return new $T(0)", typedArrayList)
        .endControlFlow();

    gennerateColumnIndexCode(listMethod, clazz.getColumnAnnotatedElements(), cursorVarName,
        throwOnIndexNotFoundVarName);

    listMethod.addStatement("$T $L = new $T($L.getCount())", typedList, listVarName, typedArrayList,
        cursorVarName)
        .addCode("do {\n")
        .addStatement("$T $L = new $T()", elementType, objectVarName, elementType);

    for (ColumnAnnotateable e : clazz.getColumnAnnotatedElements()) {
      String indexVaName = e.getColumnName() + "Index";
      listMethod.beginControlFlow("if ($L >= 0)", indexVaName);
      e.generateAssignStatement(listMethod, objectVarName, cursorVarName, indexVaName);
      listMethod.endControlFlow();
    }

    listMethod.addStatement("$L.add($L)", listVarName, objectVarName)
        .addCode("} while ($L.moveToNext());\n", cursorVarName).addStatement("return $L",
        listVarName)

        // end try
        .nextControlFlow("finally")
        .beginControlFlow("if ($L != null)", cursorVarName)
        .addStatement("$L.close()", cursorVarName)
        .endControlFlow()
        .endControlFlow(); // end try

    return listMethod.build();
  }

  /**
   * Generates the method to create retrieve a List of items from cursor that only takes one cursor
   * as parameter and internally calls the method generate by {@link #generateListMethod(ObjectMappableAnnotatedClass)}
   * with true as throwOnIndexNotFound parameter
   *
   * @param clazz {@link ObjectMappableAnnotatedClass}
   * @return The MethodSpec
   */
  private MethodSpec generateSimpleSingleMethod(ObjectMappableAnnotatedClass clazz) {

    String cursorVarName = "cursor";
    TypeName elementType = ClassName.get(clazz.getElement().asType());

    // List Method
    return MethodSpec.methodBuilder("single")
        /*
        .addJavadoc(
            "Retrieves the first element from  Cursor by scanning for @$L annotated fields. Calls {@link #single(Cursor,boolean)} with true as second parameter ",
            clazz.getElement().getSimpleName().toString(), Column.class.getSimpleName())
        .addJavadoc("@param $L The Cursor", cursorVarName)
        .addJavadoc("@return null if Cursor is empty or  a single item fetched from the cursor")
        .addJavadoc("@see #single(Cursor,boolean)")
        */.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addParameter(Cursor.class, cursorVarName)
        .returns(elementType)
        .addStatement("return single($L, true)", cursorVarName)
        .build();
  }

  /**
   * Generates the method that fetches only one single Item (the first) from cursor
   *
   * @param clazz The {@link ObjectMappableAnnotatedClass}
   * @return MethodSpec
   */
  private MethodSpec generateSingleMethod(ObjectMappableAnnotatedClass clazz) {

    String objectVarName = "item";
    String cursorVarName = "cursor";
    String throwOnIndexNotFoundVarName = "throwOnIndexNotFound";

    TypeName elementType = ClassName.get(clazz.getElement().asType());

    // List Method
    MethodSpec.Builder builder = MethodSpec.methodBuilder("single")
        /*
        .addJavadoc("Retrieves the first element from  Cursor by scanning for @$L annotated fields",
            Column.class.getSimpleName())
        .addJavadoc("@param $L The Cursor", cursorVarName)
        .addJavadoc(
            "@param $L <b>true</b>, if an {@link IllegalArgumentException} should be thrown if the column doesn't exist. <b>false</b> if missing columns should be skipped")
        .addJavadoc("@return The fetched item from Cursor or <code>null</code> if cursor is empty")
        */.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addParameter(Cursor.class, cursorVarName)
        .addParameter(boolean.class, throwOnIndexNotFoundVarName)
        .returns(elementType)
        .beginControlFlow("try")
        .beginControlFlow("if ($L == null || $L.getCount() == 0 || !$L.moveToFirst()) ",
            cursorVarName, cursorVarName, cursorVarName)
        .addStatement("return null")
        .endControlFlow();

    gennerateColumnIndexCode(builder, clazz.getColumnAnnotatedElements(), cursorVarName,
        throwOnIndexNotFoundVarName);

    builder.addStatement("$T $L = new $T()", elementType, objectVarName, elementType);

    for (ColumnAnnotateable e : clazz.getColumnAnnotatedElements()) {
      String indexVaName = e.getColumnName() + "Index";
      builder.beginControlFlow("if ($L >= 0)", indexVaName);
      e.generateAssignStatement(builder, objectVarName, cursorVarName, indexVaName);
      builder.endControlFlow();
    }

    builder.addStatement("return $L", objectVarName)
        .nextControlFlow("finally")
        .beginControlFlow("if ($L != null)", cursorVarName)
        .addStatement("$L.close()", cursorVarName)
        .endControlFlow()
        .endControlFlow(); // end finally

    return builder.build();
  }

  /**
   * Generate the code statements to retrieve the index for the given column names:
   * Basically it writes <i>getColumnIndex</i> or <i>getColumnIndexOrThrow</i> statements.
   *
   * @param builder The builder
   * @param elements the {@link ColumnAnnotateable}s
   * @param cursorVarName The variable name of the cursor
   * @param throwOnIndexNotFoundVarName The variable name of the boolean variable to check if
   * <i>getColumnIndexOrThrow</i> should be used
   */
  private void gennerateColumnIndexCode(MethodSpec.Builder builder,
      Collection<ColumnAnnotateable> elements, String cursorVarName,
      String throwOnIndexNotFoundVarName) {

    for (ColumnAnnotateable e : elements) {
      builder.addStatement("int $LIndex", e.getColumnName());
    }

    builder.beginControlFlow("if ($L)", throwOnIndexNotFoundVarName);
    for (ColumnAnnotateable e : elements) {
      builder.addStatement("$LIndex = $L.getColumnIndexOrThrow($S)", e.getColumnName(),
          cursorVarName, e.getColumnName());
    }
    builder.nextControlFlow("else");
    for (ColumnAnnotateable e : elements) {
      builder.addStatement("$LIndex = $L.getColumnIndex($S)", e.getColumnName(), cursorVarName,
          e.getColumnName());
    }
    builder.endControlFlow();
  }
}
