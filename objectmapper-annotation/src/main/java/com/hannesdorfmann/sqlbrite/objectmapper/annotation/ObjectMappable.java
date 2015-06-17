package com.hannesdorfmann.sqlbrite.objectmapper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate your model classes with this Annotation to mark them as "mappable" which means that for
 * this class a "Mapper" class get generated to read values from Cursor to Object and the
 * ContentValue
 *
 * @author Hannes Dorfmann
 */
@Target(ElementType.TYPE) @Retention(RetentionPolicy.SOURCE) @Documented
public @interface ObjectMappable {
}
