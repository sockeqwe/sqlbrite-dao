package com.hannesdorfmann.sqlbrite.objectmapper.processor;

import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column;
import com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.CodeGenerator;
import com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.field.FieldCodeFactory;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * @author Hannes Dorfmann
 */
public class ColumnAnnotatedField implements ColumnAnnotateable {

  private VariableElement field;
  private String columnName;
  private CodeGenerator codeGenerator;
  private boolean throwOnColumnIndexNotFound;

  /**
   * Creates a new instance
   *
   * @param field The VariableField representing the field
   * @param annotation The {@link Column} annotation
   * @throws ProcessingException if some checks fails
   */
  public ColumnAnnotatedField(VariableElement field, Column annotation) throws ProcessingException {

    if (field.getModifiers().contains(Modifier.PRIVATE)) {
      throw new ProcessingException(field,
          "Field %s is declared as private. Field must have at least package visibility",
          field.getSimpleName().toString());
    }

    if (field.getModifiers().contains(Modifier.PROTECTED)) {
      throw new ProcessingException(field,
          "Field %s is declared as protected. Field must have at least package visibility",
          field.getSimpleName().toString());
    }

    if (field.getModifiers().contains(Modifier.FINAL)) {
      throw new ProcessingException(field, "Field %s is declared as final, that is not allowed!",
          field.getSimpleName().toString());
    }

    if (field.getModifiers().contains(Modifier.STATIC)) {
      throw new ProcessingException(field, "Field %s is declared as static. That is not supported!",
          field.getSimpleName().toString());
    }

    columnName = annotation.value();
    if (columnName == null || columnName.length() == 0) {
      throw new ProcessingException(field, "The column name is unspecified for field %s",
          field.getSimpleName().toString());
    }

    throwOnColumnIndexNotFound = annotation.throwOnColumnIndexNotFound();

    // OK field is defined as expected
    this.field = field;

    // Code generator
    this.codeGenerator = FieldCodeFactory.get(this);
  }

  /**
   * Get the VariableElement representing this field
   *
   * @return VariableElement
   */
  public VariableElement getField() {
    return field;
  }

  /**
   * Get the column name
   *
   * @return The name of the column as specified in the annotation
   */
  @Override public String getColumnName() {
    return columnName;
  }

  /**
   * Get the fields name
   *
   * @return the fields name
   */
  public String getFieldName() {
    return field.getSimpleName().toString();
  }

  /**
   * Get the full qualified class name this field is part of.
   *
   * @return The full qualified class name
   */
  @Override public String getQualifiedSurroundingClassName() {
    TypeElement typeElement = (TypeElement) field.getEnclosingElement();
    return typeElement.getQualifiedName().toString();
  }

  @Override public void generateAssignStatement(CodeBlock.Builder builder, String objectVarName,
      String cursorVarName, String indexVarName) {

    codeGenerator.generateAssignStatement(builder, objectVarName, cursorVarName, indexVarName);
  }

  @Override public void generateContentValuesBuilderMethod(TypeSpec.Builder builder,
      TypeName type, String contentValuesVarName) {
    codeGenerator.generateContentValuesBuilderMethod(builder, type,
        contentValuesVarName);
  }

  @Override public String getElementName() {
    return field.toString();
  }

  @Override public boolean isThrowOnColumnIndexNotFound() {
    return throwOnColumnIndexNotFound;
  }
}
