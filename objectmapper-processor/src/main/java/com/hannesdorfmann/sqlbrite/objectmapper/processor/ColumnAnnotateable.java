package com.hannesdorfmann.sqlbrite.objectmapper.processor;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * @author Hannes Dorfmann
 */
public interface ColumnAnnotateable {

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

  /**
   * Genereate the ContentValues Builder
   *
   * @param builder The TypeSpec.Builder
   * @param type TypeName of the builder
   * @param contentValuesVarName The variable name of the ContentValues
   */
  void generateContentValuesBuilderMethod(TypeSpec.Builder builder, TypeName type,
      String contentValuesVarName);

  /**
   * Get the column name
   *
   * @return the name of the column this element is mappable for
   */
  String getColumnName();

  /**
   * Get the name of the Element like the field name or the method name. This method is maily used
   * to create pretty and meaningful error messages
   *
   * @return elements name.
   */
  String getElementName();

  /**
   * Get the full qualified class name of the surrounding class
   *
   * @return full qualified class name
   */
  String getQualifiedSurroundingClassName();

  /**
   * Should an exception be thrown if the column index is not found for a Cursor as result of a sql
   * query
   *
   * @return true if exception should be thrown, otherwise false
   */
  boolean isThrowOnColumnIndexNotFound();
}
