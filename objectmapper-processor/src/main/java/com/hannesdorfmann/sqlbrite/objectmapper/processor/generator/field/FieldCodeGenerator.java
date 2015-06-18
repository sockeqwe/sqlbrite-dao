package com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.field;

import com.hannesdorfmann.sqlbrite.objectmapper.processor.ColumnAnnotatedField;
import com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.CodeGenerator;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import javax.lang.model.element.Modifier;

/**
 * Code Generator that generates statements to set the value by accessing a field of the
 *
 * @author Hannes Dorfmann
 */
public class FieldCodeGenerator implements CodeGenerator {

  protected String cursorMethodName;
  protected ColumnAnnotatedField field;

  public FieldCodeGenerator(ColumnAnnotatedField field, String cursorMethodName) {
    this.cursorMethodName = cursorMethodName;
    this.field = field;
  }

  @Override public void generateAssignStatement(MethodSpec.Builder builder, String objectVarName,
      String cursorVarName, String indexVarName) {
    builder.addStatement("$L.$L = $L.$L($L)", objectVarName, field.getFieldName(), cursorVarName,
        cursorMethodName, indexVarName);
  }

  @Override public void generateContentValuesBuilderMethod(TypeSpec.Builder builder, TypeName type,
      String contentValuesVarName) {

    builder.addMethod(MethodSpec.methodBuilder(field.getFieldName())
        .addJavadoc("Adds the given value to this ContentValues\n")
        .addJavadoc("@param value The value\n")
        .addJavadoc("@return $T\n", type)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(TypeName.get(field.getField().asType()), "value", Modifier.FINAL)
        .returns(type)
        .addStatement("$L.put($S, value)", contentValuesVarName, field.getColumnName())
        .addStatement("return this").build());

    builder.addMethod(MethodSpec.methodBuilder(field.getFieldName() + "AsNull")
        .addJavadoc("Adds a null value to this ContentValues\n")
        .addJavadoc("@return $T\n", type)
        .addModifiers(Modifier.PUBLIC)
        .returns(type)
        .addStatement("$L.putNull( $S )", contentValuesVarName, field.getColumnName())
        .addStatement("return this").build());
  }
}
