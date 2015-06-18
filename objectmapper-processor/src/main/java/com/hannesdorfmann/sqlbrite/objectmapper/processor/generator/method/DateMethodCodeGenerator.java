package com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.method;

import com.hannesdorfmann.sqlbrite.objectmapper.processor.ColumnAnnotatedMethod;
import com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.CodeGenerator;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import javax.lang.model.element.Modifier;

/**
 * Generates the code to assign cursor data throught setter methods
 *
 * @author Hannes Dorfmann
 */
public class DateMethodCodeGenerator implements CodeGenerator {

  protected ColumnAnnotatedMethod method;

  public DateMethodCodeGenerator(ColumnAnnotatedMethod method) {
    this.method = method;
  }

  @Override public void generateAssignStatement(MethodSpec.Builder builder, String objectVarName,
      String cursorVarName, String indexVarName) {

    builder.addStatement("$L.$L( new java.util.Date($L.getLong( $L ) )", objectVarName, method.getMethodName(),
        cursorVarName, indexVarName);
  }


  @Override public void generateContentValuesBuilderMethod(TypeSpec.Builder builder,
      TypeName type, String contentValuesVarName) {

    String methodName = method.getMethodName();
    if (methodName.matches("set[A-Z]")){
      methodName = methodName.substring(3).toLowerCase();
    }

    builder.addMethod(MethodSpec.methodBuilder(methodName)
        .addJavadoc("Adds the given value to this ContentValues \n *")
        .addJavadoc("@param value The value \n *")
        .addJavadoc("@return $T", type)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(TypeName.get(method.getParameter().asType()), "value", Modifier.FINAL)
        .returns(type)
        .addStatement("$L.put($S, value.getTime())", contentValuesVarName, method.getColumnName())
        .addStatement("return this")
        .build());

    // null method
    builder.addMethod(MethodSpec.methodBuilder(methodName + "AsNull")
        .addModifiers(Modifier.PUBLIC)
        .addJavadoc("Adds a null value to this ContentValues")
        .addJavadoc("@return $T", type)
        .returns(type)
        .addStatement("$L.putNull( $S )", contentValuesVarName, method.getColumnName())
        .addStatement("return this")
        .build());
  }
}
