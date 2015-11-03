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

  public static CodeGenerator get(ColumnAnnotatedMethod method) throws ProcessingException {

    ExecutableElement element = method.getMethod();
    TypeMirror parameter =element.getParameters().get(0).asType();

    switch (parameter.getKind()) {

      case INT:
        return new MethodCodeGenerator(method, "getInt");

      case FLOAT:
        return new MethodCodeGenerator(method, "getFloat");

      case DOUBLE:
        return new MethodCodeGenerator(method, "getDouble");

      case LONG:
        return new MethodCodeGenerator(method, "getLong");

      case SHORT:
        return new MethodCodeGenerator(method, "getShort");

      case BOOLEAN:
        return new BooleanMethodCodeGenerator(method);
      case ARRAY:
        ArrayType parameterArrayType = (ArrayType) parameter;
        if (parameterArrayType.getComponentType().getKind() == TypeKind.BYTE) {
          return new MethodCodeGenerator(method, "getBlob");
        }
        break;

      case DECLARED:
        String parameterType = parameter.toString();
        if (parameterType.equals(String.class.getCanonicalName())) {
          return new MethodCodeGenerator(method, "getString");
        }

        if (parameterType.equals(Date.class.getCanonicalName())) {
          return new DateMethodCodeGenerator(method);
        }
        break;
    }

    throw new ProcessingException(element,
        "Unsupported type %s as parameter in method %s() in class %s annotated with @%s. Don't know how to read the parameter type",
        parameter.toString(), method,
        method.getQualifiedSurroundingClassName(), Column.class.getSimpleName());
  }
}
