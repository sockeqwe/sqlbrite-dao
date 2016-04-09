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


    VariableElement element = field.getField();
    switch (element.asType().getKind()) {

      case INT:
        return new FieldCodeGenerator(field, "getInt");

      case FLOAT:
        return new FieldCodeGenerator(field, "getFloat");

      case DOUBLE:
        return new FieldCodeGenerator(field, "getDouble");

      case LONG:
        return new FieldCodeGenerator(field, "getLong");

      case SHORT:
        return new FieldCodeGenerator(field, "getShort");

      case BOOLEAN:
        return new BooleanFieldCodeGenerator(field);

      case ARRAY:
        ArrayType arrayType = (ArrayType) element.asType();
        if (arrayType.getComponentType().getKind() == TypeKind.BYTE) {
          return new FieldCodeGenerator(field, "getBlob");
        }
        break;

      case DECLARED:
        String varType = element.asType().toString();
        if (varType.equals(String.class.getCanonicalName())) {
          return new FieldCodeGenerator(field, "getString");
        }

        if (varType.equals(Date.class.getCanonicalName())) {
          return new DateFieldCodeGenerator(field);
        }
        break;
    }

    throw new ProcessingException(element,
        "Unsupported type for field %s in class %s annotated with @%s. Don't know how to read the type %s",
        field.getElementName(), field.getQualifiedSurroundingClassName(), Column.class.getSimpleName(),
        element.asType().toString());
  }
}
