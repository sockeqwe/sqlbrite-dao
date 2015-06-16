package com.hannesdorfmann.sqlbrite.objectmapper.processor.generator;

import com.squareup.javapoet.MethodSpec;

/**
 * Interface for code generator
 *
 * @author Hannes Dorfmann
 */
public interface CodeGenerator {

  /**
   * Generate the code for reading cursor into the object
   *
   * @param builder the {@link MethodSpec.Builder}
   * @param objectVarName The variable name of the object that gets instantiated and filled with
   * data from cursor
   * @param cursorVarName The variable name of the cursor from which you should read
   * @param indexVarName The variable name of the index of the column
   */
  public void generateAssignStatement(MethodSpec.Builder builder, String objectVarName,
      String cursorVarName, String indexVarName);
}
