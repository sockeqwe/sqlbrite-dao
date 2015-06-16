package com.hannesdorfmann.sqlbrite.objectmapper.processor;

import javax.lang.model.element.Element;

/**
 * This kind of execption gets thrown if processing an element has failed
 * @author Hannes Dorfmann
 */
public class ProcessingException extends Exception {

  private Element element;

  public ProcessingException(Element element, String message, Object... args) {
    super(String.format(message, args));
    this.element = element;
  }

  public Element getElement() {
    return element;
  }
}
