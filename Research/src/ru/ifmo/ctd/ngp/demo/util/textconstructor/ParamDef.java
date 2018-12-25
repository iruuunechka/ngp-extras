package ru.ifmo.ctd.ngp.demo.util.textconstructor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that defines names of parameters for constructors or methods.
 *
 * @author Maxim Buzdalov
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ParamDef {
    String name();
    String value() default "";
}
