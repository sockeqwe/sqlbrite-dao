package com.hannesdorfmann.sqlbrite.objectmapper.processor;

import com.squareup.javapoet.MethodSpec;

/**
 * @author Hannes Dorfmann
 */
public interface ColumnAnnotateable {

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

  /**
   * Get the column name
   *
   * @return the name of the column this element is mappable for
   */
  public String getColumnName();

  /**
   * Get the name of the Element like the field name or the method name. This method is maily used
   * to create pretty and meaningful error messages
   *
   * @return elements name.
   */
  public String getElementName();

  /**
   * Get the full qualified class name of the surrounding class
   *
   * @return full qualified class name
   */
  public String getQualifiedSurroundingClassName();
}
