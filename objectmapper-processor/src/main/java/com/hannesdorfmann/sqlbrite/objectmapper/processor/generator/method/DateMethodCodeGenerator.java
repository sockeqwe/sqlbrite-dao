package com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.method;

import com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.CodeGenerator;
import com.squareup.javapoet.MethodSpec;

/**
 * Generates the code to assign cursor data throught setter methods
 *
 * @author Hannes Dorfmann
 */
public class DateMethodCodeGenerator implements CodeGenerator {

  protected String methodName;

  public DateMethodCodeGenerator(String methodName) {
    this.methodName = methodName;
  }

  @Override public void generateAssignStatement(MethodSpec.Builder builder, String objectVarName,
      String cursorVarName, String indexVarName) {

    builder.addStatement("$L.$L( new java.util.Date($L.getLong( $L ) )", objectVarName, methodName,
        cursorVarName, indexVarName);
  }
}
