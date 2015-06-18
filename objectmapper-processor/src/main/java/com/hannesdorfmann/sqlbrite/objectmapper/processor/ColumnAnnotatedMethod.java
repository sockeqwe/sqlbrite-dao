package com.hannesdorfmann.sqlbrite.objectmapper.processor;

import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column;
import com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.CodeGenerator;
import com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.method.MethodCodeFactory;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * @author Hannes Dorfmann
 */
public class ColumnAnnotatedMethod implements ColumnAnnotateable {

  private ExecutableElement method;
  private String columnName;
  private CodeGenerator codeGenerator;

  public ColumnAnnotatedMethod(ExecutableElement method, Column annotation)
      throws ProcessingException {

    if (!method.getModifiers().contains(Modifier.PUBLIC)) {
      throw new ProcessingException(method,
          "Setter method %s in %s annotated with @%s is not public. Only PUBLIC setter methods are supported",
          method.getSimpleName().toString(), method.getEnclosingElement().toString(),
          Column.class.getSimpleName());
    }

    if (method.getParameters().size() != 1) {
      throw new ProcessingException(method,
          "Setter method %s in %s annotated with @%s MUST have exactly one parameter!",
          method.toString(), method.getEnclosingElement().toString(), Column.class.getSimpleName());
    }

    columnName = annotation.value();
    if (columnName == null || columnName.length() == 0) {
      throw new ProcessingException(method, "The column name is unspecified for method %s in %s",
          method.getSimpleName().toString(), method.getEnclosingElement().toString());
    }

    this.method = method;

    codeGenerator = MethodCodeFactory.get(this);
  }

  @Override public void generateAssignStatement(MethodSpec.Builder builder, String objectVarName,
      String cursorVarName, String indexVarName) {

    codeGenerator.generateAssignStatement(builder, objectVarName, cursorVarName, indexVarName);
  }

  @Override public String getColumnName() {
    return columnName;
  }

  @Override public String getElementName() {
    return method.toString();
  }

  @Override public String getQualifiedSurroundingClassName() {
    TypeElement typeElement = (TypeElement) method.getEnclosingElement();
    return typeElement.getQualifiedName().toString();
  }

  public String getMethodName() {
    return method.getSimpleName().toString();
  }

  public ExecutableElement getMethod() {
    return method;
  }

  public VariableElement getParameter() {
    return method.getParameters().get(0);
  }

  @Override public void generateContentValuesBuilderMethod(TypeSpec.Builder builder,
      TypeName type, String contentValuesVarName) {
    codeGenerator.generateContentValuesBuilderMethod(builder, type, contentValuesVarName);
  }
}
