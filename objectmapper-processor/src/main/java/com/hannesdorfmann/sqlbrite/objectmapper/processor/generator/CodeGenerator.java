package com.hannesdorfmann.sqlbrite.objectmapper.processor.generator;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * Interface for code generator
 *
 * @author Hannes Dorfmann
 */
public interface CodeGenerator {

  /**
   * Generate the code for reading cursor into the object
   *
   * @param builder the {@link CodeBlock.Builder}
   * @param objectVarName The variable name of the object that gets instantiated and filled with
   * data from cursor
   * @param cursorVarName The variable name of the cursor from which you should read
   * @param indexVarName The variable name of the index of the column
   */
  void generateAssignStatement(CodeBlock.Builder builder, String objectVarName,
      String cursorVarName, String indexVarName);

  void generateContentValuesBuilderMethod(TypeSpec.Builder builder, TypeName type,
      String contentValuesVarName);
}
