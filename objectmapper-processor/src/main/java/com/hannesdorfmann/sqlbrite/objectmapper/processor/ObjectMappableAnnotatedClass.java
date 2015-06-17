package com.hannesdorfmann.sqlbrite.objectmapper.processor;

import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Representing a class that contains {@link Column} annotated fields
 *
 * @author Hannes Dorfmann
 */
public class ObjectMappableAnnotatedClass {

  private TypeElement typeElement;
  private Map<String, ColumnAnnotateable> fieldsMap = new HashMap<>();

  public ObjectMappableAnnotatedClass(TypeElement typeElement) throws ProcessingException {
    this.typeElement = typeElement;

    // Visibility
    if (typeElement.getModifiers().contains(Modifier.PRIVATE)) {
      throw new ProcessingException(typeElement,
          "Private classes can not contain @%s annotated fields", Column.class.getSimpleName());
    }

    // Constructor check
    boolean constructorFound = false;
    for (Element e : typeElement.getEnclosedElements()) {
      if (e.getKind() == ElementKind.CONSTRUCTOR) {
        ExecutableElement constructor = (ExecutableElement) e;
        if ((constructor.getModifiers().contains(Modifier.PUBLIC)) && constructor.getParameters()
            .isEmpty()) {
          constructorFound = true;
          break;
        }
      }
    }

    if (!constructorFound) {
      throw new ProcessingException(typeElement,
          "Class %s has %s annotated fields (incl. super class) and therefore"
              + " must provide an public empty constructor (zero parameters)",
          typeElement.getQualifiedName().toString(), Column.class.getSimpleName());
    }
  }

  /**
   * Scans the class for annotated fields. Also scans inheritance hierarchy.
   *
   * @throws ProcessingException
   */
  public void scanForAnnotatedFields(Types typeUtils, Elements elementUtils)
      throws ProcessingException {

    // Scan inheritance hierarchy recursively to find all annotated fields
    TypeElement currentClass = typeElement;
    TypeMirror superClassType;
    Column annotation = null;
    PackageElement originPackage = elementUtils.getPackageOf(typeElement);
    PackageElement superClassPackage;

    do {

      // Scan fields
      for (Element e : currentClass.getEnclosedElements()) {

        annotation = e.getAnnotation(Column.class);

        if (e.getKind() == ElementKind.FIELD && annotation != null) {

          ColumnAnnotatedField field = new ColumnAnnotatedField((VariableElement) e, annotation);

          // Check field visibility of super class field
          if (currentClass != typeElement && !field.getField()
              .getModifiers()
              .contains(Modifier.PUBLIC)) {

            superClassPackage = elementUtils.getPackageOf(currentClass);

            if ((superClassPackage != null && originPackage == null) || (superClassPackage == null
                && originPackage != null) || (superClassPackage != null
                && !superClassPackage.equals(originPackage)) || (originPackage != null
                && !originPackage.equals(superClassPackage))) {

              throw new ProcessingException(e,
                  "The field %s in class %s can not be accessed from ObjectMapper because of "
                      + "visibility issue. Either move class %s into the same package "
                      + "as %s or make the field %s public or create and annotate a public setter "
                      + "method for this field with @%s instead of annotating the field itself",
                  field.getFieldName(), field.getQualifiedSurroundingClassName(),
                  typeElement.getQualifiedName().toString(),
                  field.getQualifiedSurroundingClassName(), field.getFieldName(),
                  Column.class.getSimpleName());
            }
          }

          ColumnAnnotateable existingColumnAnnotateable = fieldsMap.get(field.getColumnName());
          if (existingColumnAnnotateable != null) {
            throw new ProcessingException(e,
                "The field %s in class %s is annotated with @%s with column name = \"%s\" but this column name is already used by %s in class %s",
                field.getFieldName(), field.getQualifiedSurroundingClassName(),
                Column.class.getSimpleName(), field.getColumnName(),
                existingColumnAnnotateable.getElementName(),
                existingColumnAnnotateable.getQualifiedSurroundingClassName());
          }

          fieldsMap.put(field.getColumnName(), field);
        }
        // Check methods
        else if (e.getKind() == ElementKind.METHOD && annotation != null) {

          ColumnAnnotatedMethod method =
              new ColumnAnnotatedMethod((ExecutableElement) e, annotation);

          ColumnAnnotateable existingColumnAnnotateable = fieldsMap.get(method.getColumnName());
          if (existingColumnAnnotateable != null) {
            throw new ProcessingException(e,
                "The method %s in class %s is annotated with @%s with column name = \"%s\" but this column name is already used by %s in class %s",
                method.getMethodName(), method.getQualifiedSurroundingClassName(),
                Column.class.getSimpleName(), method.getColumnName(),
                existingColumnAnnotateable.getElementName(),
                existingColumnAnnotateable.getQualifiedSurroundingClassName());
          }

          fieldsMap.put(method.getColumnName(), method);
        } else if (annotation != null) {
          throw new ProcessingException(e,
              "%s is of type %s and annotated with @%s, but only Fields or setter Methods can be annotated with @%s",
              e.getSimpleName(), e.getKind().toString(), Column.class.getSimpleName(),
              Column.class.getSimpleName());
        }
      }

      superClassType = currentClass.getSuperclass();
      currentClass = (TypeElement) typeUtils.asElement(superClassType);
    } while (superClassType.getKind() != TypeKind.NONE);
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ObjectMappableAnnotatedClass that = (ObjectMappableAnnotatedClass) o;

    return typeElement.equals(that.typeElement);
  }

  @Override public int hashCode() {
    return typeElement.hashCode();
  }

  /**
   * Get the TypeElement representing this class
   *
   * @return the TypeElement
   */
  public TypeElement getElement() {
    return typeElement;
  }

  /**
   * Get the full qualified class name of this annotated class
   *
   * @return full qualified class name
   */
  public String getQualifiedClassName() {
    return typeElement.getQualifiedName().toString();
  }

  /**
   * Get the simple class name
   *
   * @return simple class name
   */
  public String getSimpleClassName() {
    return typeElement.getSimpleName().toString();
  }

  /**
   * Get the Elements annotated with {@link Column}
   *
   * @return annotated elements
   */
  public Collection<ColumnAnnotateable> getColumnAnnotatedElements() {
    return fieldsMap.values();
  }
}
