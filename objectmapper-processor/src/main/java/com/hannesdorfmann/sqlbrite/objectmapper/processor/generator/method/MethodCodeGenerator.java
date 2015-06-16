package com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.method;

import com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.CodeGenerator;
import com.squareup.javapoet.MethodSpec;

/**
 * Generates the code to assign cursor data throught setter methods
 *
 * @author Hannes Dorfmann
 */
public class MethodCodeGenerator implements CodeGenerator {

  protected String methodName;
  protected String cursorMethodName;

  public MethodCodeGenerator(String methodName, String cursorMethodName) {
    this.methodName = methodName;
    this.cursorMethodName = cursorMethodName;
  }

  @Override public void generateAssignStatement(MethodSpec.Builder builder, String objectVarName,
      String cursorVarName, String indexVarName) {

    builder.addStatement("$L.$L( $L.$L( $L ) )", objectVarName, methodName, cursorVarName,
        cursorMethodName, indexVarName);
  }
}
