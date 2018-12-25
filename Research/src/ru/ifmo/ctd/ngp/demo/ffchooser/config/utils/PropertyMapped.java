package ru.ifmo.ctd.ngp.demo.ffchooser.config.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that marks a getter method that returns something
 * that is stored as a property somewhere.
 *
 * @author Maxim Buzdalov
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PropertyMapped {
    String value() default "";
}
