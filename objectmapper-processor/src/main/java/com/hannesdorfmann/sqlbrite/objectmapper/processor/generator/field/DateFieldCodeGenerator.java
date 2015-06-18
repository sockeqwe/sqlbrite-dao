package com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.field;

import com.hannesdorfmann.sqlbrite.objectmapper.processor.ColumnAnnotatedField;
import com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.CodeGenerator;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.Date;
import javax.lang.model.element.Modifier;

/**
 * @author Hannes Dorfmann
 */
public class DateFieldCodeGenerator implements CodeGenerator {

  protected ColumnAnnotatedField field;

  public DateFieldCodeGenerator(ColumnAnnotatedField field) {
    this.field = field;
  }

  @Override public void generateAssignStatement(MethodSpec.Builder builder, String objectVarName,
      String cursorVarName, String indexVarName) {

    builder.addStatement("$L.$L = new java.util.Date($L.getLong($L))", objectVarName,
        field.getFieldName(), cursorVarName, indexVarName);
  }

  @Override public void generateContentValuesBuilderMethod(TypeSpec.Builder builder, TypeName type,
      String contentValuesVarName) {

    builder.addMethod(MethodSpec.methodBuilder(field.getFieldName())
        .addJavadoc("Adds the given value to this ContentValues \n *")
        .addJavadoc("@param value The value \n *")
        .addJavadoc("@return $T", type)
        .addModifiers(Modifier.PUBLIC)
        .addParameter(TypeName.get(Date.class), "value", Modifier.FINAL)
        .returns(type)
        .addStatement("$L.put($S, value.getTime())", contentValuesVarName, field.getColumnName())
        .addStatement("return this")
        .build());

    builder.addMethod(MethodSpec.methodBuilder(field.getFieldName() + "AsNull")
        .addJavadoc("Adds a null value to this ContentValues")
        .addJavadoc("@return $T", type)
        .addModifiers(Modifier.PUBLIC).returns(type)
        .addStatement("$L.putNull( $S )", contentValuesVarName, field.getColumnName())
        .addStatement("return this").build());
  }
}
