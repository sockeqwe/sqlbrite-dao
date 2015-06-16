package com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.field;

import com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.CodeGenerator;
import com.squareup.javapoet.MethodSpec;

/**
 * Code Generator that generates statements to set the value by accessing a field of the
 *
 * @author Hannes Dorfmann
 */
public class FieldCodeGenerator implements CodeGenerator {

  protected String cursorMethodName;
  protected String fieldName;

  public FieldCodeGenerator(String fieldName, String cursorMethodName) {
    this.cursorMethodName = cursorMethodName;
    this.fieldName = fieldName;
  }

  @Override
  public void generateAssignStatement(MethodSpec.Builder builder, String objectVarName,
      String cursorVarName, String indexVarName) {
    builder.addStatement("$L.$L = $L.$L($L)", objectVarName, fieldName, cursorVarName,
        cursorMethodName, indexVarName);
  }
}
