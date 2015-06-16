package com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.field;

import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column;
import com.hannesdorfmann.sqlbrite.objectmapper.processor.ColumnAnnotatedField;
import com.hannesdorfmann.sqlbrite.objectmapper.processor.ProcessingException;
import com.hannesdorfmann.sqlbrite.objectmapper.processor.generator.CodeGenerator;
import java.util.Date;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;

/**
 * Static factory to create a Field
 *
 * @author Hannes Dorfmann
 */
public class FieldCodeFactory {

  public static CodeGenerator get(ColumnAnnotatedField field) throws ProcessingException {

    String fieldName = field.getFieldName();

    VariableElement element = field.getField();
    switch (element.asType().getKind()) {

      case INT:
        return new FieldCodeGenerator(fieldName, "getInt");

      case FLOAT:
        return new FieldCodeGenerator(fieldName, "getFloat");

      case DOUBLE:
        return new FieldCodeGenerator(fieldName, "getDouble");

      case LONG:
        return new FieldCodeGenerator(fieldName, "getLong");

      case SHORT:
        return new FieldCodeGenerator(fieldName, "getShort");

      case ARRAY:
        ArrayType arrayType = (ArrayType) element.asType();
        if (arrayType.getComponentType().getKind() == TypeKind.BYTE) {
          return new FieldCodeGenerator(fieldName, "getBlob");
        }
        break;

      case DECLARED:
        String varType = element.asType().toString();
        if (varType.equals(String.class.getCanonicalName())) {
          return new FieldCodeGenerator(fieldName, "getString");
        }

        if (varType.equals(Date.class.getCanonicalName())) {
          return new DateFieldCodeGenerator(fieldName);
        }
        break;
    }

    throw new ProcessingException(element,
        "Unsupported type for field %s in class %S annotated with @%s. Don't know how to read the type %s",
        fieldName, field.getQualifiedSurroundingClassName(), Column.class.getSimpleName(),
        element.asType().toString());
  }
}
