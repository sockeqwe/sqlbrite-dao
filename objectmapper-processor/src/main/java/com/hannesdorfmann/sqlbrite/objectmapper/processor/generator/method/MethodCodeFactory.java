package com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.method;

import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column;
import com.hannesdorfmann.sqlbrite.objectmapper.processor.ColumnAnnotatedMethod;
import com.hannesdorfmann.sqlbrite.objectmapper.processor.ProcessingException;
import com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.CodeGenerator;
import java.util.Date;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Static factory to create a Field
 *
 * @author Hannes Dorfmann
 */
public class MethodCodeFactory {

  public static CodeGenerator get(ColumnAnnotatedMethod methodName) throws ProcessingException {

    ExecutableElement element = methodName.getMethod();
    TypeMirror parameter =element.getParameters().get(0).asType();

    switch (parameter.getKind()) {

      case INT:
        return new MethodCodeGenerator(methodName, "getInt");

      case FLOAT:
        return new MethodCodeGenerator(methodName, "getFloat");

      case DOUBLE:
        return new MethodCodeGenerator(methodName, "getDouble");

      case LONG:
        return new MethodCodeGenerator(methodName, "getLong");

      case SHORT:
        return new MethodCodeGenerator(methodName, "getShort");

      case ARRAY:
        ArrayType arrayType = (ArrayType) element.asType();
        if (arrayType.getComponentType().getKind() == TypeKind.BYTE) {
          return new MethodCodeGenerator(methodName, "getBlob");
        }
        break;

      case DECLARED:
        String varType = element.asType().toString();
        if (varType.equals(String.class.getCanonicalName())) {
          return new MethodCodeGenerator(methodName, "getString");
        }

        if (varType.equals(Date.class.getCanonicalName())) {
          return new DateMethodCodeGenerator(methodName);
        }
        break;
    }

    throw new ProcessingException(element,
        "Unsupported type %s as parameter in method %s() in class %s annotated with @%s. Don't know how to read the parameter type",
        parameter.toString(), methodName,
        methodName.getQualifiedSurroundingClassName(), Column.class.getSimpleName());
  }
}
