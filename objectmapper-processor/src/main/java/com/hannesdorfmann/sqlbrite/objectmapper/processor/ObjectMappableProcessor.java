package com.hannesdorfmann.sqlbrite.objectmapper.processor;

import android.content.ContentValues;
import android.database.Cursor;
import com.google.auto.service.AutoService;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column;
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.ObjectMappable;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
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
import rx.functions.Func1;

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
        ObjectMappableAnnotatedClass annotatedClass =
            new ObjectMappableAnnotatedClass(messager, classElement);
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

      String packageName = getPackageName(clazz);

      // Generate the mapper class
      TypeSpec mapperClass = TypeSpec.classBuilder(clazz.getSimpleClassName() + "Mapper")
          .addJavadoc("Generated class to work with Cursors and ContentValues for $T\n",
              ClassName.get(clazz.getElement()))
          .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
          .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build())
          .addField(generateRxMappingMethod(clazz))
          .addMethod(generateContentValuesMethod(clazz, "ContentValuesBuilder"))
          .addType(generateContentValuesBuilderClass(clazz, "ContentValuesBuilder"))
          .build();

      JavaFile.builder(packageName, mapperClass).build().writeTo(filer);
    }
  }

  /**
   * Generates the field that can be used as RxJava method
   *
   * @param clazz The {@link ObjectMappableAnnotatedClass}
   * @return MethodSpec
   */
  private FieldSpec generateRxMappingMethod(ObjectMappableAnnotatedClass clazz) {

    String objectVarName = "item";
    String cursorVarName = "cursor";

    TypeName elementType = ClassName.get(clazz.getElement().asType());

    // new Func1<Cursor, ListsItem>()

    CodeBlock.Builder initBlockBuilder = CodeBlock.builder()
        .add("new $L<$L, $L>() {\n", Func1.class.getSimpleName(), Cursor.class.getSimpleName(),
            clazz.getSimpleClassName())
        .indent()
        .add("@Override public $L call($L cursor) {\n", clazz.getSimpleClassName(),
            Cursor.class.getSimpleName())
        .indent();

    // assign the columns indexes
    generateColumnIndexCode(initBlockBuilder, clazz.getColumnAnnotatedElements(), cursorVarName);

    // Instantiate element
    initBlockBuilder.addStatement("$T $L = new $T()", elementType, objectVarName, elementType);

    // read cursor into element variable
    for (ColumnAnnotateable e : clazz.getColumnAnnotatedElements()) {
      String indexVaName = e.getColumnName() + "Index";
      initBlockBuilder.beginControlFlow("if ($L >= 0)", indexVaName);
      e.generateAssignStatement(initBlockBuilder, objectVarName, cursorVarName, indexVaName);
      initBlockBuilder.endControlFlow();
    }

    initBlockBuilder.addStatement("return $L", objectVarName).unindent().add(
        "}\n") // end call () method
        .unindent().add("}") // end anonymous class
        .build();

    ParameterizedTypeName fieldType =
        ParameterizedTypeName.get(ClassName.get(Func1.class), ClassName.get(Cursor.class),
            elementType);

    return FieldSpec.builder(fieldType, "MAPPER", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
        .initializer(initBlockBuilder.build())
        .build();
  }

  private void generateColumnIndexCode(CodeBlock.Builder builder,
      Collection<ColumnAnnotateable> elements, String cursorVarName) {

    for (ColumnAnnotateable e : elements) {
      if (e.isThrowOnColumnIndexNotFound()) {
        builder.addStatement("int $LIndex = $L.getColumnIndexOrThrow($S)", e.getColumnName(),
            cursorVarName, e.getColumnName());
      } else {
        builder.addStatement("int $LIndex = $L.getColumnIndex($S)", e.getColumnName(),
            cursorVarName, e.getColumnName());
      }
    }
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
  @Deprecated private void generateColumnIndexCode(MethodSpec.Builder builder,
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

  /**
   * Generates the ContentValues Builder Class
   *
   * @param clazz The class you want to create a builder for
   * @param className The classname
   * @return The Builder class
   */
  private TypeSpec generateContentValuesBuilderClass(ObjectMappableAnnotatedClass clazz,
      String className) {

    String cvVarName = "contentValues";

    MethodSpec constructor = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PRIVATE)
        .addStatement("$L = new $T()", cvVarName, ClassName.get(ContentValues.class))
        .build();

    TypeSpec.Builder builder = TypeSpec.classBuilder(className)
        .addJavadoc(
            "Builder class to generate type sage {@link $T } . At the end you have to call {@link #build()}\n",
            TypeName.get(ContentValues.class))
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addField(ContentValues.class, cvVarName, Modifier.PRIVATE)
        .addMethod(constructor)
        .addMethod(MethodSpec.methodBuilder("build")
            .addJavadoc("Creates and returnes a $T from the builder\n",
                TypeName.get(ContentValues.class))
            .addJavadoc("@return $T", TypeName.get(ContentValues.class))
            .addModifiers(Modifier.PUBLIC)
            .addStatement("return $L", cvVarName)
            .returns(ContentValues.class)
            .build());

    String packageName = getPackageName(clazz);
    for (ColumnAnnotateable e : clazz.getColumnAnnotatedElements()) {
      e.generateContentValuesBuilderMethod(builder, ClassName.get(packageName, className),
          cvVarName);
    }

    return builder.build();
  }

  /**
   * Generates a static file to get
   */
  private MethodSpec generateContentValuesMethod(ObjectMappableAnnotatedClass clazz,
      String className) {

    ClassName typeName = ClassName.get(getPackageName(clazz), className);

    return MethodSpec.methodBuilder("contentValues")
        .addJavadoc("Get a typesafe ContentValues Builder \n")
        .addJavadoc("@return The ContentValues Builder \n")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .returns(typeName)
        .addStatement("return new $T()", typeName)
        .build();
  }

  /**
   * Get the package name of a certain clazz
   *
   * @param clazz The class you want the packagename for
   * @return The package name
   */
  private String getPackageName(ObjectMappableAnnotatedClass clazz) {
    PackageElement pkg = elements.getPackageOf(clazz.getElement());
    return pkg.isUnnamed() ? "" : pkg.getQualifiedName().toString();
  }
}
