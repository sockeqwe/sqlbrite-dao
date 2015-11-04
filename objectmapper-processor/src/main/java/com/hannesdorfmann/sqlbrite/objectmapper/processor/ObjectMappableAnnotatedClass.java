package com.hannesdorfmann.sqlbrite.objectmapper.processor;

import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
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
  private Map<String, ColumnAnnotateable> columnAnnotatedElementsMap = new HashMap<>();

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

    Set<VariableElement> annotatedFields = new LinkedHashSet<>();
    Map<String, ExecutableElement> possibleSetterFields = new HashMap<>();

    do {

      // Scan fields
      for (Element e : currentClass.getEnclosedElements()) {
        annotation = e.getAnnotation(Column.class);
        if (e.getKind() == ElementKind.FIELD && annotation != null) {
          annotatedFields.add((VariableElement) e);
        }
        // Check methods
        else if (e.getKind() == ElementKind.METHOD) {

          // Save possible setters
          ExecutableElement methodElement = (ExecutableElement) e;
          String setterMethodName = methodElement.getSimpleName().toString();

          if (setterMethodName.startsWith("set")) {
            ExecutableElement existingSetter = possibleSetterFields.get(setterMethodName);
            if (existingSetter != null) {
              // The new setter has better visibility, so pick that one
              if (ModifierUtils.compareModifierVisibility(methodElement, existingSetter) == -1) {
                possibleSetterFields.put(setterMethodName, methodElement);
              }
            } else {
              possibleSetterFields.put(setterMethodName, methodElement);
            }
          }

          // Is it an annotated setter?
          if (annotation != null) {

            ColumnAnnotatedMethod method = new ColumnAnnotatedMethod(methodElement, annotation);

            ColumnAnnotateable existingColumnAnnotateable =
                columnAnnotatedElementsMap.get(method.getColumnName());
            if (existingColumnAnnotateable != null) {
              throw new ProcessingException(methodElement,
                  "The method %s in class %s is annotated with @%s with column name = \"%s\" but this column name is already used by %s in class %s",
                  method.getMethodName(), method.getQualifiedSurroundingClassName(),
                  Column.class.getSimpleName(), method.getColumnName(),
                  existingColumnAnnotateable.getElementName(),
                  existingColumnAnnotateable.getQualifiedSurroundingClassName());
            }

            columnAnnotatedElementsMap.put(method.getColumnName(), method);
          }
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

    // Check fields
    for (VariableElement e : annotatedFields) {

      annotation = e.getAnnotation(Column.class);
      currentClass = (TypeElement) e.getEnclosingElement();

      if (e.getModifiers().contains(Modifier.PRIVATE)) {
        // Private field: Automatically try to detect a setter
        String fieldName = e.getSimpleName().toString();
        String perfectSetterName;
        if (fieldName.length() == 1) {
          perfectSetterName = "set" + fieldName.toUpperCase();
        } else {
          String withoutHungarianNotation = HungarianNotation.removeNotation(fieldName);
          perfectSetterName = "set" + Character.toUpperCase(withoutHungarianNotation.charAt(0))
              + withoutHungarianNotation.substring(1);
        }

        ExecutableElement setterMethod = possibleSetterFields.get(perfectSetterName);
        if (setterMethod != null && isSetterForField(setterMethod, e)) {
          // valid setter, so use this one
          ColumnAnnotatedMethod method = new ColumnAnnotatedMethod(setterMethod, annotation);
          columnAnnotatedElementsMap.put(method.getColumnName(), method);
          continue;
        } else {

          if (fieldName.startsWith("m")) {
            // setmFoo()  if field == mFoo
            setterMethod = possibleSetterFields.get("set" + fieldName);
            if (setterMethod != null && isSetterForField(setterMethod, e)) {
              // valid setter
              ColumnAnnotatedMethod method = new ColumnAnnotatedMethod(setterMethod, annotation);
              columnAnnotatedElementsMap.put(method.getColumnName(), method);
              continue;
            } else {
              // setMFoo  if field == mFoo
              if (fieldName.length() > 1) {
                setterMethod = possibleSetterFields.get(
                    "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1));
                if (setterMethod != null && isSetterForField(setterMethod, e)) {
                  // valid setter
                  ColumnAnnotatedMethod method =
                      new ColumnAnnotatedMethod(setterMethod, annotation);
                  columnAnnotatedElementsMap.put(method.getColumnName(), method);
                  continue;
                }
              }
            }
          }
        }

        throw new ProcessingException(e, "The field '%s' in class %s is private. "
            + "A corresponding setter method with the name '%s(%s)' is expected but haven't been found. Please add this setter method, "
            + "If you have another setter method named differently "
            + "please annotate your setter method with @%s ", fieldName,
            e.getEnclosingElement().getSimpleName().toString(), perfectSetterName,
            e.asType().toString(), Column.class.getSimpleName());
      } else {
        // A simple non private field
        ColumnAnnotatedField field = new ColumnAnnotatedField((VariableElement) e, annotation);

        // Check field visibility of super class field
        if (currentClass != typeElement && !field.getField()
            .getModifiers()
            .contains(Modifier.PUBLIC)) {

          superClassPackage = elementUtils.getPackageOf(currentClass);

          if ((superClassPackage != null && originPackage == null) || (superClassPackage == null
              && originPackage != null) || (superClassPackage != null && !superClassPackage.equals(
              originPackage)) || (originPackage != null && !originPackage.equals(
              superClassPackage))) {

            throw new ProcessingException(e,
                "The field %s in class %s can not be accessed from ObjectMapper because of "
                    + "visibility issue. Either move class %s into the same package "
                    + "as %s or make the field %s public or create and annotate a public setter "
                    + "method for this field with @%s instead of annotating the field itself",
                field.getFieldName(), field.getQualifiedSurroundingClassName(),
                typeElement.getQualifiedName().toString(), field.getQualifiedSurroundingClassName(),
                field.getFieldName(), Column.class.getSimpleName());
          }
        }

        ColumnAnnotateable existingColumnAnnotateable =
            columnAnnotatedElementsMap.get(field.getColumnName());
        if (existingColumnAnnotateable != null) {
          throw new ProcessingException(e,
              "The field %s in class %s is annotated with @%s with column name = \"%s\" but this column name is already used by %s in class %s",
              field.getFieldName(), field.getQualifiedSurroundingClassName(),
              Column.class.getSimpleName(), field.getColumnName(),
              existingColumnAnnotateable.getElementName(),
              existingColumnAnnotateable.getQualifiedSurroundingClassName());
        }

        columnAnnotatedElementsMap.put(field.getColumnName(), field);
      }
    }
  }

  /**
   * Checks if the setter method is valid for the given field
   *
   * @param setter The setter method
   * @param field The field
   * @return true if setter works for given field, otherwise false
   */
  private boolean isSetterForField(ExecutableElement setter, VariableElement field) {
    return setter.getParameters() != null && setter.getParameters().size() == 1
        && setter.getParameters().get(0).asType().equals(field.asType());
    // TODO inheritance? TypeUtils is applicable?
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
    return columnAnnotatedElementsMap.values();
  }
}
