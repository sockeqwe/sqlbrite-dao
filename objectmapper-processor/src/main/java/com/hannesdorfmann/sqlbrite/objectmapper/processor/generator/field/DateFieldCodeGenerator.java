package com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.field;

import com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.CodeGenerator;
import com.squareup.javapoet.MethodSpec;

/**
 * @author Hannes Dorfmann
 */
public class DateFieldCodeGenerator implements CodeGenerator {

  protected String fieldName;

  public DateFieldCodeGenerator(String fieldName) {
    this.fieldName = fieldName;
  }

  @Override
  public void generateAssignStatement(MethodSpec.Builder builder, String objectVarName,
      String cursorVarName, String indexVarName) {

    builder.addStatement("$L.$L = new java.util.Date($L.getLong($L))", objectVarName, fieldName, cursorVarName,
        indexVarName);
  }
}
