package com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.method;

import com.hannesdorfmann.sqlbrite.objectmapper.processor.ColumnAnnotatedMethod;
import com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.CodeGenerator;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import javax.lang.model.element.Modifier;

/**
 * Generates the code to assign cursor data throught setter methods
 *
 * @author Hannes Dorfmann
 */
public class BooleanMethodCodeGenerator implements CodeGenerator {

  protected ColumnAnnotatedMethod method;

  public BooleanMethodCodeGenerator(ColumnAnnotatedMethod method) {
    this.method = method;
  }

  @Override public void generateAssignStatement(CodeBlock.Builder builder, String objectVarName,
      String cursorVarName, String indexVarName) {

    builder.addStatement("$L.$L( $L.getInt( $L ) == 1 )", objectVarName, method.getMethodName(),
        cursorVarName, indexVarName);
  }

  @Override public void generateContentValuesBuilderMethod(TypeSpec.Builder builder, TypeName type,
      String contentValuesVarName) {

    String methodName = method.getMethodName();
    if (methodName.matches("^set[A-Z]\\w*")) {
      methodName = methodName.substring(3).toLowerCase();
    }

    // setter method
    builder.addMethod(MethodSpec.methodBuilder(methodName)
        .addJavadoc("Adds the given value to this ContentValues\n")
        .addJavadoc("@param value The value\n")
        .addJavadoc("@return $T\n", type)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(TypeName.get(method.getParameter().asType()), "value", Modifier.FINAL)
        .returns(type)
        .addStatement("$L.put($S, value)", contentValuesVarName, method.getColumnName())
        .addStatement("return this")
        .build());

    // null method
    builder.addMethod(MethodSpec.methodBuilder(methodName + "AsNull")
        .addModifiers(Modifier.PUBLIC)
        .addJavadoc("Adds a null value to this ContentValues\n")
        .addJavadoc("@return $T\n", type)
        .returns(type)
        .addStatement("$L.putNull( $S )", contentValuesVarName, method.getColumnName())
        .addStatement("return this")
        .build());
  }
}
